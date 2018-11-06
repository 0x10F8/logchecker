package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CompositeRule extends AbstractRule {

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
