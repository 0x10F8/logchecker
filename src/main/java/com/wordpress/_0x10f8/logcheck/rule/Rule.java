package com.wordpress._0x10f8.logcheck.rule;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

/**
 * Rule interface, defines that rules must have a name and the ability to
 * evaluate a log file.
 */
public interface Rule {

	/**
	 * Get the rule name.
	 *
	 * @return The rule name
	 */
	String getName();

	/**
	 * Set the rule name
	 *
	 * @param name The rule name
	 */
	void setName(final String name);

	/**
	 * Evaluate the log file with this rule.
	 *
	 * @param logFile The log file
	 *
	 * @return A list of rule matches indicating where in the log file matched the
	 *         rule
	 *
	 * @throws IOException If there was an exception reading the log file
	 */
	List<RuleMatch> evaluate(final File logFile) throws IOException;
}
