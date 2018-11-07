package com.wordpress._0x10f8.logcheck;

import com.wordpress._0x10f8.logcheck.cli.CommandLineArguments;
import com.wordpress._0x10f8.logcheck.engine.ParsingEngine;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.report.CLiReport;
import com.wordpress._0x10f8.logcheck.report.Report;
import com.wordpress._0x10f8.logcheck.report.TXTReport;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        final CommandLineArguments cliArgs = new CommandLineArguments();
        cliArgs.gatherOptions(args);

        if (cliArgs.isArgumentsSet()) {

            final ParsingEngine engine = new ParsingEngine(cliArgs.getLogFiles(), cliArgs.getRuleFiles(), cliArgs.isVerbose());

            final List<RuleMatch> results = engine.runParser();

            final Report cliReport = new CLiReport();
            cliReport.handleOutput(results);

            final Report txtReport = new TXTReport(cliArgs.getOutputFile());
            txtReport.handleOutput(results);
        }
    }

}
