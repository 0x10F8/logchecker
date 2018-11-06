package com.wordpress._0x10f8.logcheck.rule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.parser.RuleSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CompositeRule extends AbstractRule {

    public static void main(String[] args) throws Exception {
        ContainsRule containsRule = new ContainsRule();
        containsRule.setCaseType(ContainsRule.CaseType.IGNORE_CASE);
        containsRule.setName("Test for my name");
        containsRule.setContainsString("Calum");

        CompositeRule rule = new CompositeRule();
        rule.setLogicalOperator(LogicalOperator.OR);
        rule.addRule(containsRule);
        rule.setName("Test Comp Rule");


        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Rule.class, new RuleSerializer());
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        Gson gson = builder.create();

        System.out.println(gson.toJson(rule));


        Rule fromFile = gson.fromJson(new String(Files.readAllBytes(Paths.get("./rules/CompositeRuleTest.json"))), Rule.class);
        final File logFile = new File("./logs/ssl_access_log");
        final List<RuleMatch> matches = fromFile.evaluate(logFile);
        System.out.println("Found " + matches.size() + " log rows in file [" + logFile.getName() + "] matching the rule [" + fromFile.getName() + "]");

    }

    enum LogicalOperator {
        AND, OR;

        public static List<RuleMatch> filterAND(final Rule compositeRule, final List<List<RuleMatch>> compositeResults) {
            final Map<LogEntryReference, Integer> referenceCounts = new HashMap<>();

            for (final List<RuleMatch> singleResults : compositeResults) {
                for (final RuleMatch singleMatch : singleResults) {
                    Integer countForThisReference = referenceCounts.get(singleMatch.getLogReference());
                    if (countForThisReference == null) {
                        countForThisReference = 1;
                    } else {
                        countForThisReference = countForThisReference + 1;
                    }
                    referenceCounts.put(singleMatch.getLogReference(), countForThisReference);
                }
            }

            final Integer allRuleCount = compositeResults.size();
            final List<RuleMatch> matches = new ArrayList<>();
            for (final Map.Entry<LogEntryReference, Integer> logEntryCount : referenceCounts.entrySet()) {
                if (logEntryCount.getValue().equals(allRuleCount)) {
                    matches.add(new RuleMatch(compositeRule.getName(), logEntryCount.getKey()));
                }
            }

            return matches;
        }

        public static List<RuleMatch> filterOR(final Rule compositeRule, final List<List<RuleMatch>> compositeResults) {
            final Set<LogEntryReference> uniqueLogEntries = new HashSet<>();
            for (final List<RuleMatch> singleResults : compositeResults) {
                for (final RuleMatch singleMatch : singleResults) {
                    uniqueLogEntries.add(singleMatch.getLogReference());
                }
            }

            final List<RuleMatch> matches = new ArrayList<>();
            for (final LogEntryReference uniqueReference : uniqueLogEntries) {
                matches.add(new RuleMatch(compositeRule.getName(), uniqueReference));
            }
            return matches;
        }
    }

    private List<Rule> rules = new ArrayList<>();
    private LogicalOperator operator;

    public void setLogicalOperator(final LogicalOperator operator) {
        this.operator = operator;
    }

    public void addRule(final Rule rule) {
        this.rules.add(rule);
    }

    public void setRules(final List<Rule> rules) {
        this.rules = rules;
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    @Override
    public List<RuleMatch> evaluate(final File logFile) throws IOException {

        final List<List<RuleMatch>> compositeResults = new ArrayList<>();
        List<RuleMatch> results = null;

        for (final Rule compositeRule : rules) {
            compositeResults.add(compositeRule.evaluate(logFile));
        }

        if (this.operator == LogicalOperator.OR) {
            results = LogicalOperator.filterOR(this, compositeResults);
        } else if (this.operator == LogicalOperator.AND) {
            results = LogicalOperator.filterAND(this, compositeResults);
        }


        return results;
    }

}
