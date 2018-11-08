package com.wordpress._0x10f8.logcheck.rule;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

/**
 * Check if a log entry contains a specific string
 *
 */
public class ContainsRule extends AbstractRule {

	/**
	 * Enum used to determine whether the String case matters
	 *
	 */
	enum CaseType {
		IGNORE_CASE, CASE_SENSITIVE;
	}

	private CaseType caseType;
	private String containsString;

	/**
	 * Get the case to match
	 * 
	 * @return case type
	 */
	public CaseType getCaseType() {
		return caseType;
	}

	/**
	 * Set the case type to match
	 * 
	 * @param caseType The case type
	 */
	public void setCaseType(final CaseType caseType) {
		this.caseType = caseType;
	}

	/**
	 * Get the contains string this rule matches
	 * 
	 * @return the contains string
	 */
	public String getContainsString() {
		return containsString;
	}

	/**
	 * Set the contains string this rule matches
	 * 
	 * @param containsString the contains string
	 */
	public void setContainsString(final String containsString) {
		this.containsString = containsString;
	}

	/**
	 * Evaluate the contains string against each log line in the log file matching
	 * case if required
	 */
	@Override
	public List<RuleMatch> evaluate(final File logFile) throws IOException {
		final List<RuleMatch> matches;
		if (this.caseType == CaseType.CASE_SENSITIVE) {
			matches = super.mapFunctionToFile(logFile, (s -> s.contains(this.containsString)));
		} else {
			matches = super.mapFunctionToFile(logFile,
					(s -> s.toLowerCase().contains(this.containsString.toLowerCase())));
		}
		return matches;
	}

}
