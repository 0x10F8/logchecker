package com.wordpress._0x10f8.logcheck.rule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;

/**
 * A surrounding match contains rule looks at a specific log line, performs a
 * regular expression group match on that line. Then using the string obtained
 * from the regex it searches surround log lines for this using a string
 * contains match. If the regex search is found in X of the Y surrounding lines
 * then the line is marked as a match. As this may produce quite a lot of
 * results a limit results flag is provided so that if a particular regex match
 * string is ever used it can not be used again.
 */
public class SurroundingMatchContainsRule extends AbstractRule {

	private static final String DESC_FORMAT_STR = "The string [%s] was found in %d of %d surrounding lines";

	private String regularExpression;
	private int regexGroup;

	private ContainsRule.CaseType caseType;
	private int surroundingCountTrigger;
	private int surroundingLinesToSearch;
	private boolean limitResults;

	/**
	 * Get the regular expression used to create the regex match string
	 * 
	 * @return the regex string
	 */
	public String getRegularExpression() {
		return regularExpression;
	}

	/**
	 * Set the regular expression match string
	 * 
	 * @param regularExpression the regex string
	 */
	public void setRegularExpression(final String regularExpression) {
		this.regularExpression = regularExpression;
	}

	/**
	 * Get the number of matches in surrounding logs required to trigger a line
	 * match
	 * 
	 * @return the trigger count
	 */
	public int getSurroundingCountTrigger() {
		return surroundingCountTrigger;
	}

	/**
	 * Set the number of matches in surrounding logs required to trigger a match
	 * 
	 * @param surroundingCountTrigger The trigger count
	 */
	public void setSurroundingCountTrigger(final int surroundingCountTrigger) {
		this.surroundingCountTrigger = surroundingCountTrigger;
	}

	/**
	 * Get the number of surrounding lines to search
	 * 
	 * @return surround lines to search
	 */
	public int getSurroundingLinesToSearch() {
		return surroundingLinesToSearch;
	}

	/**
	 * Set the number of surrounding lines to search
	 * 
	 * @param surroundingLinesToSearch surrounding lines to search
	 */
	public void setSurroundingLinesToSearch(final int surroundingLinesToSearch) {
		this.surroundingLinesToSearch = surroundingLinesToSearch;
	}

	/**
	 * Get the case type to match
	 * 
	 * @return case type to match (CASE_SENSITIVE, IGNORE_CASE)
	 */
	public ContainsRule.CaseType getCaseType() {
		return caseType;
	}

	/**
	 * Set the case type to match
	 * 
	 * @param caseType The case type to match
	 */
	public void setCaseType(final ContainsRule.CaseType caseType) {
		this.caseType = caseType;
	}

	/**
	 * The regular expression group to match for the search string
	 * 
	 * @return regex group number
	 */
	public int getRegexGroup() {
		return regexGroup;
	}

	/**
	 * Set the regular expression group number to match for the search string
	 * 
	 * @param regexGroup regex group number
	 */
	public void setRegexGroup(int regexGroup) {
		this.regexGroup = regexGroup;
	}

	/**
	 * Set the limit results flag. This indicates whether, if a match is made with a
	 * particular search string, we should keep searching further logs with this
	 * particular search string. For example if the search string was an IP address
	 * and this flag was set to true, even if the IP address was in 20/30
	 * surrounding lines for 100,000 lines only 1 line would be returned as a match.
	 * 
	 * @param limitResults The limit results flag
	 */
	public void setLimitResults(final boolean limitResults) {
		this.limitResults = limitResults;
	}

	/**
	 * Check if we are limiting results
	 * 
	 * @return boolean flag
	 */
	public boolean isLimitResults() {
		return this.limitResults;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wordpress._0x10f8.logcheck.rule.Rule#evaluate(java.io.File)
	 */
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
					final List<Integer> matchingLines = listLinesWithMatchingString(allLines, i,
							containsStringForThisLine, this.caseType);
					if (matchingLines.size() >= this.surroundingCountTrigger) {
						final int logLine = i + 1;
						final RuleMatch match = new RuleMatch(this.getName(), new LogEntryReference(logFile, logLine));
						/*
						 * If limit results is set then only pick out a single result for each contains
						 * string
						 */
						match.setDescription(String.format(DESC_FORMAT_STR, containsStringForThisLine,
								matchingLines.size(), this.surroundingLinesToSearch));
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

	/**
	 * Search the lines surrounding a specified line for a contains string
	 * 
	 * @param allLines         All of the log lines
	 * @param searchAroundLine The line we are searching around
	 * @param containsString   The string to search for
	 * @param caseType         Whether we are case sensitive or not
	 * @return List of Integers containing matching line numbers
	 */
	private List<Integer> listLinesWithMatchingString(final List<String> allLines, final int searchAroundLine,
			final String containsString, final ContainsRule.CaseType caseType) {

		final List<Integer> matchingLines = new ArrayList<>();

		/*
		 * Only search forward half of the surrounding lines and backwards the other
		 * half
		 */
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

	/**
	 * Do a case sensitive or insensitive contains match
	 * 
	 * @param containString The contains string to check for
	 * @param line          The line to compare to
	 * @param caseType      Whether we are case sensitive or not
	 * @return boolean flag
	 */
	private boolean contains(final String containString, final String line, final ContainsRule.CaseType caseType) {
		boolean contains = false;
		if (caseType == ContainsRule.CaseType.CASE_SENSITIVE) {
			contains = line.contains(containString);
		} else if (caseType == ContainsRule.CaseType.IGNORE_CASE) {
			contains = line.toLowerCase().contains(containString.toLowerCase());
		}
		return contains;
	}

}
