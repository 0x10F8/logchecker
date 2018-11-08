package com.wordpress._0x10f8.logcheck.match;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;

/**
 * Encapsulates a rule matching a log entry
 */
public class RuleMatch {

	private String matchingRuleName;
	private LogEntryReference logReference;

	/* optional description */
	private String description;

	/**
	 * Create a rule match with the specified rule and log reference
	 * 
	 * @param matchingRuleName The matching rule name
	 * @param logReference     The log reference matched
	 */
	public RuleMatch(final String matchingRuleName, final LogEntryReference logReference) {
		this.matchingRuleName = matchingRuleName;
		this.logReference = logReference;
	}

	/**
	 * Get the matching rule name
	 * 
	 * @return the rule name
	 */
	public String getMatchingRuleName() {
		return matchingRuleName;
	}

	/**
	 * Set the matching rule name
	 * 
	 * @param matchingRuleName the rule name
	 */
	public void setMatchingRuleName(final String matchingRuleName) {
		this.matchingRuleName = matchingRuleName;
	}

	/**
	 * Get the matching log reference
	 * 
	 * @return the log entry
	 */
	public LogEntryReference getLogReference() {
		return logReference;
	}

	/**
	 * Set the matching log reference
	 * 
	 * @param logReference the log entry
	 */
	public void setLogReference(final LogEntryReference logReference) {
		this.logReference = logReference;
	}

	/**
	 * Get the description, note that this may return null as the field is optional
	 * 
	 * @return the description if set
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description, optional
	 * 
	 * @param description The description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}
