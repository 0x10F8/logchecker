package com.wordpress._0x10f8.logcheck.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

/**
 * Abstract report implementation, provides some useful collection of matches
 * into "per file" maps and "per file per rule" maps.
 *
 */
public abstract class AbstractReport implements Report {

	/** A mapping of files to the matches in those files */
	protected Map<File, List<RuleMatch>> perFileResults;

	/** A mapping of files to a mapping of rule names to the list of rule matches */
	protected Map<File, Map<String, List<RuleMatch>>> perFilePerRule;

	/** Set of unique files in matches */
	protected Set<File> uniqueFileSet;

	/** The total results found */
	protected int totalResults;

	/** Number of unique log files matched */
	protected int uniqueLogFilesAffected;

	/**
	 * Populate the protected helper fields
	 */
	@Override
	public void handleOutput(final List<RuleMatch> evaluationResults) throws IOException {
		this.totalResults = evaluationResults.size();
		this.uniqueFileSet = new HashSet<>();
		this.perFileResults = new HashMap<>();
		this.perFilePerRule = new HashMap<>();

		for (final RuleMatch match : evaluationResults) {
			uniqueFileSet.add(match.getLogReference().getLogFile());

			List<RuleMatch> perFileList = perFileResults.get(match.getLogReference().getLogFile());
			if (perFileList == null) {
				perFileList = new ArrayList<>();
			}
			perFileList.add(match);
			perFileResults.put(match.getLogReference().getLogFile(), perFileList);

			Map<String, List<RuleMatch>> perRuleForFile = perFilePerRule.get(match.getLogReference().getLogFile());
			if (perRuleForFile == null) {
				perRuleForFile = new HashMap<>();
			}
			perFilePerRule.put(match.getLogReference().getLogFile(), perRuleForFile);

			List<RuleMatch> perTypeList = perRuleForFile.get(match.getMatchingRuleName());
			if (perTypeList == null) {
				perTypeList = new ArrayList<>();
			}
			perTypeList.add(match);
			perRuleForFile.put(match.getMatchingRuleName(), perTypeList);

		}

		this.uniqueLogFilesAffected = uniqueFileSet.size();
	}
}
