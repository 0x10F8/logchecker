package com.wordpress._0x10f8.logcheck.report;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class TXTReport extends AbstractReport {

    private Map<File, List<String>> cachedLogLines = new WeakHashMap<>();

    private final File outputFile;

    private boolean firstWrite = true;

    public TXTReport(final String outputFile) {
        this.outputFile = new File(outputFile);
    }


    @Override
    public void handleOutput(final List<RuleMatch> evaluationResults) throws IOException {

        super.handleOutput(evaluationResults);

        if (outputFile.exists() && firstWrite) {
            outputFile.delete();
        }

        for (Map.Entry<File, List<RuleMatch>> perFileResultsEntry : perFileResults.entrySet()) {
            Files.write(Paths.get(outputFile.toURI()),
                    ("Outputing detailed logs for file " + perFileResultsEntry.getKey().getAbsolutePath() + "\n").getBytes(StandardCharsets.UTF_8),
                    firstWrite ? StandardOpenOption.CREATE_NEW : StandardOpenOption.APPEND);
            firstWrite = false;
            for (final RuleMatch match : perFileResultsEntry.getValue()) {
                Files.write(Paths.get(outputFile.toURI()),
                        (match.getMatchingRuleName() + " matched "
                                + (match.getDescription() != null ? match.getDescription() : "")
                                + " in file " + match.getLogReference().getLogFile().getAbsolutePath()
                                + " on line: " + match.getLogReference().getLogLine() + "\n")
                                .getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.APPEND);
                Files.write(Paths.get(outputFile.toURI()),
                        ("\t" + getLogLine(match.getLogReference().getLogFile(), match.getLogReference().getLogLine()) + "\n").getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.APPEND);
            }
        }
        System.out.println("Output text report to file " + outputFile.getAbsolutePath());
    }

    private String getLogLine(final File logFile, final int logLine) throws IOException {
        List<String> logLines = cachedLogLines.get(logFile);
        if (logLines == null) {
            logLines = Files.readAllLines(Paths.get(logFile.toURI()), StandardCharsets.ISO_8859_1);
            cachedLogLines.put(logFile, logLines);

        }
        return logLines.get(logLine - 1);
    }
}
