package com.wordpress._0x10f8.logcheck.report;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CLiReport extends AbstractReport {


    @Override
    public void handleOutput(final List<RuleMatch> evaluationResults) throws IOException {
        super.handleOutput(evaluationResults);
        System.out.println("================= RESULTS =================");
        System.out.println(String.format("Found a total of %d matches across %d files.", totalResults, uniqueLogFilesAffected));
        for (Map.Entry<File, List<RuleMatch>> perFileResultsEntry : perFileResults.entrySet()) {
            System.out.println(String.format("File [%s]: %d matches", perFileResultsEntry.getKey().getAbsolutePath(), perFileResultsEntry.getValue().size()));
            final Map<String, List<RuleMatch>> ruleMatchesForFile = this.perFilePerRule.get(perFileResultsEntry.getKey());
            for (Map.Entry<String, List<RuleMatch>> matchesForFileEntry : ruleMatchesForFile.entrySet()) {
                System.out.println("\t The rule " + matchesForFileEntry.getKey() + " matched " + matchesForFileEntry.getValue().size() + " times");
            }
        }
    }
}
