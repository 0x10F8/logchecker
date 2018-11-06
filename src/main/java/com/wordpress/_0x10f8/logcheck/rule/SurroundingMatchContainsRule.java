package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SurroundingMatchContainsRule extends AbstractRule {

    private static final String DESC_FORMAT_STR = "The string [%s] was found in %d surrounding lines";

    private String regularExpression;
    private int regexGroup;

    private ContainsRule.CaseType caseType;
    private int surroundingCountTrigger;
    private int surroundingLinesToSearch;
    private boolean limitResults;

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(final String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public int getSurroundingCountTrigger() {
        return surroundingCountTrigger;
    }

    public void setSurroundingCountTrigger(final int surroundingCountTrigger) {
        this.surroundingCountTrigger = surroundingCountTrigger;
    }

    public int getSurroundingLinesToSearch() {
        return surroundingLinesToSearch;
    }

    public void setSurroundingLinesToSearch(final int surroundingLinesToSearch) {
        this.surroundingLinesToSearch = surroundingLinesToSearch;
    }

    public ContainsRule.CaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(final ContainsRule.CaseType caseType) {
        this.caseType = caseType;
    }

    public int getRegexGroup() {
        return regexGroup;
    }

    public void setRegexGroup(int regexGroup) {
        this.regexGroup = regexGroup;
    }

    public void setLimitResults(final boolean limitResults) {
        this.limitResults = limitResults;
    }

    public boolean isLimitResults() {
        return this.limitResults;
    }

    @Override
    public List<RuleMatch> evaluate(final File logFile) throws IOException {
        final Pattern regularExpressionPattern = Pattern.compile(regularExpression);
        final List<RuleMatch> matches = Collections.synchronizedList(new ArrayList<>());
        final List<String> allLines = tokenizeLog(logFile);

        /* Used to pick out unique matches if limit results is set */
        final Map<String, RuleMatch> matchesMap = new HashMap<>();

        for (int i = 0; i < allLines.size(); i++) {
            final String line = allLines.get(i);
            final Matcher matcher = regularExpressionPattern.matcher(line);
            if (matcher.find()) {
                final String containsStringForThisLine = matcher.group(this.regexGroup);
                if (containsStringForThisLine != null && !containsStringForThisLine.trim().isEmpty()
                        && (!limitResults || !matchesMap.containsKey(containsStringForThisLine))) {
                    final List<Integer> matchingLines = listLinesWithMatchingString(allLines, i, containsStringForThisLine, this.caseType);
                    if (matchingLines != null && matchingLines.size() >= this.surroundingCountTrigger) {
                        final int logLine = i + 1;
                        final RuleMatch match = new RuleMatch(this.getName(), new LogEntryReference(logFile, logLine));
                        /* If limit results is set then only pick out a single result for each contains string */
                        match.setDescription(String.format(DESC_FORMAT_STR, containsStringForThisLine, matchingLines.size()));
                        if (this.limitResults && !matchesMap.containsKey(containsStringForThisLine)) {
                            matchesMap.put(containsStringForThisLine, match);
                        } else {
                            matches.add(match);
                        }
                    }
                }
            }
        }
        /* Pick the unique entries from the map if limit results is set */
        if (this.limitResults) {
            for (Map.Entry<String, RuleMatch> matchEntry : matchesMap.entrySet()) {
                matches.add(matchEntry.getValue());
            }
        }

        return matches;
    }

    private List<Integer> listLinesWithMatchingString(final List<String> allLines, final int searchAroundLine,
                                                      final String containsString, final ContainsRule.CaseType caseType) {

        final List<Integer> matchingLines = new ArrayList<>();


        /* Only search forward half of the surrounding lines and backwards the other half */
        final int max = searchAroundLine + (this.surroundingLinesToSearch / 2);
        final int min = searchAroundLine - (this.surroundingLinesToSearch / 2);

        /* Search forwards first */
        for (int i = searchAroundLine; i < allLines.size() && i < max; ++i) {
            final String line = allLines.get(i);
            if (contains(containsString, line, caseType)) {
                matchingLines.add(i);
            }
        }

        /* Then search backwards */
        for (int i = searchAroundLine; i >= 0 && i >= min; --i) {
            final String line = allLines.get(i);
            if (contains(containsString, line, caseType)) {
                matchingLines.add(i);
            }
        }

        return matchingLines;
    }

    private boolean contains(final String containString, final String line,
                             final ContainsRule.CaseType caseType) {
        boolean contains = false;
        if (caseType == ContainsRule.CaseType.CASE_SENSITIVE) {
            contains = line.contains(containString);
        } else if (caseType == ContainsRule.CaseType.IGNORE_CASE) {
            contains = line.toLowerCase().contains(containString.toLowerCase());
        }
        return contains;
    }


}
