package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SurroundingMatchContainsRule extends AbstractRule {

    private static final String DESC_FORMAT_STR = "The string [%s] was found in %d surrounding lines";

    private String regularExpression;
    private Pattern regularExpressionPattern;
    private int regexGroup;

    private ContainsRule.CaseType caseType;
    private int surroundingCountTrigger;
    private int surroundingLinesToSearch;

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(final String regularExpression) {
        this.regularExpression = regularExpression;
        this.regularExpressionPattern = Pattern.compile(regularExpression);
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

    @Override
    public List<RuleMatch> evaluate(final File logFile) throws IOException {
        final List<RuleMatch> matches = Collections.synchronizedList(new ArrayList<>());
        final List<String> allLines = tokenizeLog(logFile);

        for (int i = 0; i < allLines.size(); i++) {
            final String line = allLines.get(i);
            final Matcher matcher = regularExpressionPattern.matcher(line);
            if (matcher.find()) {
                final String containsStringForThisLine = matcher.group(this.regexGroup);
                if (containsStringForThisLine != null && !containsStringForThisLine.trim().isEmpty()) {
                    final List<Integer> matchingLines = listLinesWithMatchingString(allLines, i, containsStringForThisLine, this.caseType);
                    if (matchingLines != null && matchingLines.size() >= this.surroundingCountTrigger) {
                        final int logLine = i + 1;
                        final RuleMatch match = new RuleMatch(this.getName(), new LogEntryReference(logFile, logLine));
                        match.setDescription(String.format(DESC_FORMAT_STR, containsStringForThisLine, matchingLines.size()));
                        matches.add(match);
                    }
                }
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
