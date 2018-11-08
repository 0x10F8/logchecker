package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;
import com.wordpress._0x10f8.logcheck.match.Matcher;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract rule, contains a useful method which maps a lambda function (or
 * {@link Matcher}) to each line of a log, creating matches
 *
 */
public abstract class AbstractRule implements Rule {

	private String name;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wordpress._0x10f8.logcheck.rule.Rule#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wordpress._0x10f8.logcheck.rule.Rule#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Tokenize the log file into lines
	 * 
	 * @param logFile The log file
	 * @return List of log file lines
	 * @throws IOException If there was an issue reading the file
	 */
	List<String> tokenizeLog(final File logFile) throws IOException {
		return Files.readAllLines(Paths.get(logFile.toURI()), StandardCharsets.ISO_8859_1);
	}

	/**
	 * Map the specified function to each line of the log file
	 * 
	 * @param logFile         The log file
	 * @param matcherFunction the function
	 * @return List of {@link RuleMatch} where matcherFunction.matches(line) is true
	 * @throws IOException If there was an issue reading the log file
	 */
	List<RuleMatch> mapFunctionToFile(final File logFile, final Matcher<String> matcherFunction) throws IOException {
		final List<String> logFileLines = tokenizeLog(logFile);
		final List<RuleMatch> matches = new ArrayList<>();
		for (int i = 0; i < logFileLines.size(); i++) {
			final String line = logFileLines.get(i).trim();
			final int logLine = i + 1;
			if (matcherFunction.matches(line)) {
				final LogEntryReference logEntry = new LogEntryReference(logFile, logLine);
				matches.add(new RuleMatch(this.name, logEntry));
			}
		}
		return matches;
	}

}
