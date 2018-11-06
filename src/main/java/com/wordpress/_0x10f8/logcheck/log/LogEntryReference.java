package com.wordpress._0x10f8.logcheck.log;

import java.io.File;

public class LogEntryReference {

    private final File logFile;
    private final int logLine;

    public LogEntryReference(final File logFile, final int logLine) {
        this.logFile = logFile;
        this.logLine = logLine;
    }

    public File getLogFile() {
        return logFile;
    }


    public int getLogLine() {
        return logLine;
    }

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
