package com.wordpress._0x10f8.logcheck;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.Rule;
import com.wordpress._0x10f8.logcheck.rule.RuleFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        final List<Rule> rules = RuleFactory.loadRulesFromFiles(Arrays.asList(
                new File("./rules/SQLiCompositeRule.json"),
                new File("./rules/VeryActiveClient.json"),
                new File("./rules/XSSCompositeRule.json"),
                new File("./rules/CommandiCompositeRule.json")));

        final File logFile = new File("./logs/test_log");

        final List<RuleMatch> allMatches = new ArrayList<>();

        for (final Rule rule : rules) {
            System.out.println("Analysing logfile " + logFile.getName() + " with rule " + rule.getName());
            allMatches.addAll(rule.evaluate(logFile));
        }

        System.out.println("Found " + allMatches.size() + " log rows in file [" + logFile.getName() + "]");

        for (int i = 0; i < allMatches.size() && i < 100; i++) {
            final RuleMatch match = allMatches.get(i);

            final String ruleName = match.getMatchingRuleName();
            final String description = match.getDescription();
            final LogEntryReference reference = match.getLogReference();
            final String logLine = getLogLine(reference.getLogFile(), reference.getLogLine());

            System.out.println("Found match " + ruleName + " " + (description == null ? "" : description) +
                    " in log file [" + reference.getLogFile().getAbsolutePath() + "] line: " + reference.getLogLine());
            System.out.println("\t " + logLine);

        }

    }

    private static String getLogLine(final File logFile, final int line) throws IOException {
        final List<String> lines = Files.readAllLines(Paths.get(logFile.toURI()));
        return lines.get(line - 1);
    }
}
