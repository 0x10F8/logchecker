package com.wordpress._0x10f8.logcheck.report;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.IOException;
import java.util.List;

/**
 * This interface indicates the method which should be provided by all report
 * classes. At their simplest level reports should produce output from the
 * results.
 *
 */
public interface Report {

	/**
	 * Create a report from the results
	 * 
	 * @param evaluationResults The results
	 * @throws IOException If there was an error handling IO
	 */
	public void handleOutput(final List<RuleMatch> evaluationResults) throws IOException;
}
