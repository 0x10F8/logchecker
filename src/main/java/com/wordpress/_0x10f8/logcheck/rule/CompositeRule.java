package com.wordpress._0x10f8.logcheck.rule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;

/**
 * A composite rule is a rule which is made up of other rules. The composite
 * result of these rules is either the logical OR or logical AND result of the
 * subset of results.
 */
public class CompositeRule extends AbstractRule {

	/**
	 * Enum providing logical operators and their resulting filters
	 *
	 */
	enum LogicalOperator {
		AND, OR;

		/**
		 * Filter the composite result subsets based on the logical AND operation. This
		 * takes the sub sets of results and produces a final single set of results
		 * where results in the final set appear in ALL of the sub sets.
		 * 
		 * @param compositeRule    The composite rule used to create these results
		 * @param compositeResults The subsets of composite results
		 * @return A single list of results where each final results occurs in all
		 *         parameter sub sets
		 */
		public static List<RuleMatch> filterAND(final Rule compositeRule,
				final List<List<RuleMatch>> compositeResults) {
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

		/**
		 * Filter the composite result subsets based on the logical OR operation. This
		 * takes the sub sets of results and produces a final single set of results
		 * where results in the final set could have appeared in any of the sub sets.
		 * 
		 * @param compositeRule    The composite rule used to create these results
		 * @param compositeResults The subsets of composite results
		 * @return A single list of results where any particular result occurred in any
		 *         of the sub sets provided
		 */
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

	/**
	 * Set the logical operator to use when creating the final results set
	 * 
	 * @param operator The logical operator (e.g. OR, AND)
	 */
	public void setLogicalOperator(final LogicalOperator operator) {
		this.operator = operator;
	}

	/**
	 * Add a rule to the composite list
	 * 
	 * @param rule The rule to add
	 */
	public void addRule(final Rule rule) {
		this.rules.add(rule);
	}

	/**
	 * Set the whole rules list for this composite rule
	 * 
	 * @param rules The rule list
	 */
	public void setRules(final List<Rule> rules) {
		this.rules = rules;
	}

	/**
	 * Get all of the rules used in this composite rule
	 * 
	 * @return all of the composite rules
	 */
	public List<Rule> getRules() {
		return this.rules;
	}

	/**
	 * Evaluate the log file using all of the rules within this composite rule and
	 * then produce a final result list using the logical operation specified
	 */
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
