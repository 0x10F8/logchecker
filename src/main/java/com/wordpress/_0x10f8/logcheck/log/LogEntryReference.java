package com.wordpress._0x10f8.logcheck.log;

import java.io.File;

/**
 * An entry in a log file
 * 
 */
public class LogEntryReference {

	private final File logFile;
	private final int logLine;

	/**
	 * Create a Log file entry reference
	 * 
	 * @param logFile The log file
	 * @param logLine The line in the log file
	 */
	public LogEntryReference(final File logFile, final int logLine) {
		this.logFile = logFile;
		this.logLine = logLine;
	}

	/**
	 * Get the log file
	 * 
	 * @return the log file
	 */
	public File getLogFile() {
		return logFile;
	}

	/**
	 * Get the log file line
	 * 
	 * @return the log file line
	 */
	public int getLogLine() {
		return logLine;
	}

	/**
	 * Check if the specified object is both a log entry and the file and line
	 * fields match
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean equals = false;
		if (obj instanceof LogEntryReference) {
			final LogEntryReference otherEntry = (LogEntryReference) obj;
			equals = this.logFile.equals(otherEntry.getLogFile()) && this.logLine == otherEntry.getLogLine();
		}
		return equals;
	}
}
