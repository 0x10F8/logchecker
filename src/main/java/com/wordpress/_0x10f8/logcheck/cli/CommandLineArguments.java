package com.wordpress._0x10f8.logcheck.cli;

import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser for command line arguments
 * 
 * <pre>
 * {@code
 * Usage: logchecker.jar [-v] -o=<outputFile> --logs=<logFiles>[,<logFiles>...]
 *                       [--logs=<logFiles>[,<logFiles>...]]... --rules=<ruleFiles>
 *                       [,<ruleFiles>...] [--rules=<ruleFiles>[,
 *                       <ruleFiles>...]]...
 * Parse the log files using the specified rules.
 *       --logs=<logFiles>[,<logFiles>...]
 *                   Log files to analyse
 *       --rules=<ruleFiles>[,<ruleFiles>...]
 *                   Rule files to analyse logs
 *   -o, --output=<outputFile>
 *                   Output report file
 *   -v, --verbose   Verbose progress output
 *     }
 * </pre>
 */
@CommandLine.Command(name = "logchecker.jar", description = "Parse the log files using the specified rules.")
public class CommandLineArguments {

	@CommandLine.Option(names = "--rules", description = "Rule files to analyse logs", split = ",", required = true)
	private List<File> ruleFiles;

	@CommandLine.Option(names = "--logs", description = "Log files to analyse", split = ",", required = true)
	private List<File> logFiles;

	@CommandLine.Option(names = { "-v", "--verbose" }, description = "Verbose progress output", required = false)
	private boolean verbose;

	@CommandLine.Option(names = { "-o", "--output" }, description = "Output report file", required = true)
	private String outputFile;

	private boolean argumentsSet = false;

	private static final String WILD_CARD = "*";

	/**
	 * Gather the options used from the command line arguments.
	 *
	 * @param args the CLi arguments
	 */
	public void gatherOptions(String... args) {
		try {
			CommandLine.populateCommand(this, args);
			this.argumentsSet = true;
			this.logFiles = handleWildCards(this.logFiles);
			this.ruleFiles = handleWildCards(this.ruleFiles);
		} catch (Exception e) {
			CommandLine.usage(this, System.out);
		}
	}

	/**
	 * Handle wild cards at the end of rule and log directory selections.
	 *
	 * @param files List of files passed as arguments
	 *
	 * @return List of files with any wildcard selections expanded
	 */
	private List<File> handleWildCards(final List<File> files) {
		final List<File> withWildCardsExpanded = new ArrayList<>();
		/* Iterate through the file list */
		for (final File file : files) {
			if (file.getName().endsWith(WILD_CARD)) {
				if (file.exists()) {
					withWildCardsExpanded.add(file);
				} else {
					final String searchName = file.getName().replace(WILD_CARD, "");
					final File directory = file.getParentFile();
					final File[] expandedFileList = directory.listFiles(f -> f.getName().startsWith(searchName));
					if (expandedFileList != null) {
						withWildCardsExpanded.addAll(Arrays.asList(expandedFileList));
					}
				}
			} else {
				withWildCardsExpanded.add(file);
			}
		}

		return withWildCardsExpanded;
	}

	/**
	 * Get the rule files selected
	 *
	 * @return the rule files
	 */
	public List<File> getRuleFiles() {
		return ruleFiles;
	}

	/**
	 * Get the log files selected
	 *
	 * @return the log files
	 */
	public List<File> getLogFiles() {
		return logFiles;
	}

	/**
	 * Check if arguments were successfully parsed and set
	 *
	 * @return true if arguments were successfully parsed
	 */
	public boolean isArgumentsSet() {
		return argumentsSet;
	}

	/**
	 * Check if the verbose flag was set
	 *
	 * @return true if verbose
	 */
	public boolean isVerbose() {
		return this.verbose;
	}

	/**
	 * Get the output file
	 *
	 * @return the output file
	 */
	public String getOutputFile() {
		return this.outputFile;
	}

}
