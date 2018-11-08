package com.wordpress._0x10f8.logcheck.report;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

/**
 * CLi report, outputs some basic information about the parsing: <br>
 * For example:
 * 
 * <pre>
 * {@code
 * ================= RESULTS =================
Found a total of 13037 matches across 3 files.
File [/Users/c0g/Logs/clarknet_access_log_Aug28]: 9 matches
	 The rule XSS Composite matched 6 times
	 The rule Directory Traversal matched 3 times
File [/Users/c0g/Logs/stanmore_access.log]: 6 matches
	 The rule Very Active Client matched 6 times
File [/Users/c0g/Logs/almhuette_access.log]: 13022 matches
	 The rule XSS Composite matched 990 times
	 The rule Directory Traversal matched 2776 times
	 The rule SQL Injection Composite matched 1655 times
	 The rule Very Active Client matched 133 times
	 The rule Vulnerability scanner matched 7466 times
	 The rule Command Injection Composite matched 2 times
Output text report to file /Users/c0g/Desktop/logchecker/target/output.txt
 * }
 * </pre>
 */
public class CLiReport extends AbstractReport {

	/**
	 * Output the basic report to STD OUT
	 */
	@Override
	public void handleOutput(final List<RuleMatch> evaluationResults) throws IOException {
		super.handleOutput(evaluationResults);
		System.out.println("================= RESULTS =================");
		System.out.println(
				String.format("Found a total of %d matches across %d files.", totalResults, uniqueLogFilesAffected));
		for (Map.Entry<File, List<RuleMatch>> perFileResultsEntry : perFileResults.entrySet()) {
			System.out.println(String.format("File [%s]: %d matches", perFileResultsEntry.getKey().getAbsolutePath(),
					perFileResultsEntry.getValue().size()));
			final Map<String, List<RuleMatch>> ruleMatchesForFile = this.perFilePerRule
					.get(perFileResultsEntry.getKey());
			for (Map.Entry<String, List<RuleMatch>> matchesForFileEntry : ruleMatchesForFile.entrySet()) {
				System.out.println("\t The rule " + matchesForFileEntry.getKey() + " matched "
						+ matchesForFileEntry.getValue().size() + " times");
			}
		}
	}
}
