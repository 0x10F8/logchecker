package com.wordpress._0x10f8.logcheck.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

/**
 * Outputs a text file containing a detailed report of the parsing
 *
 */
public class TXTReport extends AbstractReport {

	/** weak cache containing files already loaded */
	private Map<File, List<String>> cachedLogLines = new WeakHashMap<>();

	private final File outputFile;
	private boolean firstWrite = true;

	/**
	 * Create a text report with the specified output file.
	 * 
	 * @param outputFile The output file
	 */
	public TXTReport(final String outputFile) {
		this.outputFile = new File(outputFile);
	}

	/**
	 * Generates a detailed report where each line either contains the rule match
	 * details or the line below contains the actual line matched
	 */
	@Override
	public void handleOutput(final List<RuleMatch> evaluationResults) throws IOException {

		super.handleOutput(evaluationResults);

		if (outputFile.exists() && firstWrite) {
			outputFile.delete();
		}

		for (Map.Entry<File, List<RuleMatch>> perFileResultsEntry : perFileResults.entrySet()) {
			Files.write(Paths.get(outputFile.toURI()),
					("Outputing detailed logs for file " + perFileResultsEntry.getKey().getAbsolutePath() + "\n")
							.getBytes(StandardCharsets.UTF_8),
					firstWrite ? StandardOpenOption.CREATE_NEW : StandardOpenOption.APPEND);
			firstWrite = false;
			for (final RuleMatch match : perFileResultsEntry.getValue()) {
				Files.write(Paths.get(outputFile.toURI()),
						(match.getMatchingRuleName() + " matched "
								+ (match.getDescription() != null ? match.getDescription() : "") + " in file "
								+ match.getLogReference().getLogFile().getAbsolutePath() + " on line: "
								+ match.getLogReference().getLogLine() + "\n").getBytes(StandardCharsets.UTF_8),
						StandardOpenOption.APPEND);
				Files.write(Paths.get(outputFile.toURI()),
						("\t" + getLogLine(match.getLogReference().getLogFile(), match.getLogReference().getLogLine())
								+ "\n").getBytes(StandardCharsets.UTF_8),
						StandardOpenOption.APPEND);
			}
			removeFileFromCache(perFileResultsEntry.getKey());
		}
		System.out.println("Output text report to file " + outputFile.getAbsolutePath());
	}

	/**
	 * Get the actual log line from the log file
	 * 
	 * @param logFile The log file
	 * @param logLine The log file line
	 * @return String containing the line
	 * 
	 * @throws IOException If there was an issue reading the file
	 */
	private String getLogLine(final File logFile, final int logLine) throws IOException {
		List<String> logLines = cachedLogLines.get(logFile);
		if (logLines == null) {
			logLines = Files.readAllLines(Paths.get(logFile.toURI()), StandardCharsets.ISO_8859_1);
			cachedLogLines.put(logFile, logLines);

		}
		return logLines.get(logLine - 1);
	}

	/**
	 * Remove a file from the lines cache
	 * 
	 * @param logFile The file to remove
	 */
	private void removeFileFromCache(final File logFile) {
		this.cachedLogLines.remove(logFile);
		System.gc();
	}
}
