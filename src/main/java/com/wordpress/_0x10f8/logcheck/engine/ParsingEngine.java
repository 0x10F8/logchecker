package com.wordpress._0x10f8.logcheck.engine;

import com.wordpress._0x10f8.logcheck.cli.ProgressBar;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.Rule;
import com.wordpress._0x10f8.logcheck.rule.RuleFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParsingEngine {

    private final List<File> logFiles;
    private final List<File> ruleFiles;
    private final boolean verboseProgress;
    private final ProgressBar progressBar = new ProgressBar(50, 0, 100, 80);

    public ParsingEngine(final List<File> logFiles, final List<File> ruleFiles, final boolean verboseProgress) {
        this.logFiles = logFiles;
        this.ruleFiles = ruleFiles;
        this.verboseProgress = verboseProgress;
    }

    public List<RuleMatch> runParser() throws IOException {

        long totalSize = 0;

        final List<Rule> rules = RuleFactory.loadRulesFromFiles(this.ruleFiles);
        final List<RuleMatch> allMatches = new ArrayList<>();
        final int maximumOperations = (rules.size() * logFiles.size());

        progressBar.setMaximumValue(maximumOperations);

        int currentOperations = 0;

        progressBar.setStartTime();


        if (verboseProgress) {
            printProgress("", currentOperations);
        }


        for (final File logFile : logFiles) {
            totalSize += logFile.length();
            for (final Rule rule : rules) {
                final String fileMessage = "Analysing log [" + logFile.getName() + "] with Rule [" + rule.getName() + "]";
                if (verboseProgress) {
                    printProgress(fileMessage, currentOperations);
                    currentOperations++;
                }
                allMatches.addAll(rule.evaluate(logFile));

                if (verboseProgress) {
                    printProgress(fileMessage, currentOperations);
                }
            }
        }

        if (verboseProgress) {
            progressBar.done(System.out);
            System.out.println();
            System.out.println(String.format("Took %s to analyse %dMB of logs %d times.", progressBar.getTotalTime(), (totalSize / 1024 / 1024), rules.size()));
        }

        return allMatches;
    }

    private void printProgress(final String name, final int currentOperations) {
        progressBar.setMessage(name);
        progressBar.setCurrentValue(currentOperations);
        progressBar.printProgressBar(System.out);
    }
}