package com.wordpress._0x10f8.logcheck;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.Rule;
import com.wordpress._0x10f8.logcheck.rule.RuleFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        final List<Rule> rules = RuleFactory.loadRulesFromFiles(Arrays.asList(new File("./rules/SQLiCompositeRule.json"),
                new File("./rules/VeryActiveClient.json"), new File("./rules/XSSCompositeRule.json")));
        final File logFile = new File("./logs/test_log");

        final List<RuleMatch> allMatches = new ArrayList<>();

        for (final Rule rule : rules) {
            System.out.println("Analysing logfile " + logFile.getName() + " with rule " + rule.getName());
            allMatches.addAll(rule.evaluate(logFile));
        }

        System.out.println("Found " + allMatches.size() + " log rows in file [" + logFile.getName() + "]");

        for (int i = 0; i < allMatches.size() && i < 100; i++) {
            final RuleMatch match = allMatches.get(i);
            System.out.println("Found match " + match.getMatchingRuleName() + " " + match.getDescription());
        }

    }
}
