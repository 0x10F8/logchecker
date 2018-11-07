package com.wordpress._0x10f8.logcheck.report;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class AbstractReport implements Report {

    protected Map<File, List<RuleMatch>> perFileResults;
    protected Map<File, Map<String, List<RuleMatch>>> perFilePerRule;
    protected Set<File> uniqueFileSet;
    protected int totalResults;
    protected int uniqueLogFilesAffected;

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
