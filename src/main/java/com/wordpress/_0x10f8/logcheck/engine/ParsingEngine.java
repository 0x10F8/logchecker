package com.wordpress._0x10f8.logcheck.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.wordpress._0x10f8.logcheck.cli.ProgressBar;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.AbstractRule;
import com.wordpress._0x10f8.logcheck.rule.Rule;
import com.wordpress._0x10f8.logcheck.rule.RuleFactory;

/**
 * The parsing engine for the log checker, takes the log files and rules and
 * applies rules to log files. If the verbose option is set it will also display
 * the progress of the parsing as this can take some time.
 *
 */
public class ParsingEngine {

	private final List<File> logFiles;
	private final List<File> ruleFiles;
	private final boolean verboseProgress;
	private final ProgressBar progressBar = new ProgressBar(50, 0, 100, 80);
	private final int threads;

	private int currentOperations = 0;

	/* Thread synchronization lock */
	private final Object syncLock = new Object();

	/**
	 * Initialise the parsing engine with the log files, rules to apply to the logs
	 * and the verbose option flag.
	 * 
	 * @param logFiles        The log files to analyse
	 * @param ruleFiles       The rules to analyse with
	 * @param threads         Number of threads to work with
	 * @param verboseProgress The verbose flag
	 */
	public ParsingEngine(final List<File> logFiles, final List<File> ruleFiles, final int threads,
			final boolean verboseProgress) {
		this.logFiles = logFiles;
		this.ruleFiles = ruleFiles;
		this.threads = threads;
		this.verboseProgress = verboseProgress;
	}

	/**
	 * Run the parser and return the results
	 * 
	 * @return The results of running the rules on the logs, a list of
	 *         {@link RuleMatch}
	 * @throws IOException          If there was an issue reading the log or rule
	 *                              files
	 * @throws ExecutionException   If there was an issue threading
	 * @throws InterruptedException If a thread was interrupted
	 */
	public List<RuleMatch> runParser() throws IOException, InterruptedException, ExecutionException {

		long totalSize = 0;

		final List<Rule> rules = RuleFactory.loadRulesFromFiles(this.ruleFiles);
		final List<RuleMatch> allMatches = new ArrayList<>();
		final int maximumOperations = (rules.size() * logFiles.size());

		progressBar.setMaximumValue(maximumOperations);

		currentOperations = 0;

		progressBar.setStartTime();

		if (verboseProgress) {
			printProgress("", currentOperations);
		}

		/* Start a thread pool to delegate work to */
		final ExecutorService threadPool = Executors.newFixedThreadPool(threads);

		try {
			/*
			 * Iterate through files, we will multi-process rule matching to a specific
			 * file. Using the syncLock to synchronise important sections
			 */
			for (final File logFile : logFiles) {
				totalSize += logFile.length();

				final List<Future<List<RuleMatch>>> matchesForFile = new ArrayList<>();
				final String fileMessage = "Analysing log [" + logFile.getName() + "] with " + rules.size() + " rules";
				if (verboseProgress) {
					printProgress(fileMessage, currentOperations);
				}

				for (final Rule rule : rules) {

					matchesForFile.add(threadPool.submit(() -> {
						try {
							return rule.evaluate(logFile);
						} finally {
							synchronized (syncLock) {
								if (verboseProgress) {
									printProgress(fileMessage, currentOperations);
									currentOperations++;
								}
							}
						}
					}));

				}

				for (Future<List<RuleMatch>> finishedParse : matchesForFile) {
					allMatches.addAll(finishedParse.get());
				}

				if (verboseProgress) {
					printProgress(fileMessage, currentOperations);
				}
				clearRuleFileCache();
			}

			if (verboseProgress) {
				progressBar.done(System.out);
				System.out.println();
				System.out.println(String.format("Took %s to analyse %dMB of logs %d times.",
						progressBar.getTotalTime(), (totalSize / 1024 / 1024), rules.size()));
			}

			return allMatches;
		} finally {
			threadPool.shutdown();
		}
	}

	/**
	 * Clear the rule file cache and suggest GC happens
	 */
	private void clearRuleFileCache() {
		AbstractRule.clearCache();
		System.gc();
	}

	/**
	 * Print the progress to STD OUT
	 * 
	 * @param message           The message to display with the progress
	 * @param currentOperations The current progress
	 */
	private void printProgress(final String message, final int currentOperations) {
		progressBar.setMessage(message);
		progressBar.setCurrentValue(currentOperations);
		progressBar.printProgressBar(System.out);
	}
}