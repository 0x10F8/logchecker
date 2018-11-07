package com.wordpress._0x10f8.logcheck.cli;

import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(
        name = "logchecker.jar",
        description = "Parse the log files using the specified rules."
)
public class CommandLineArguments {

    @CommandLine.Option(names = "--rules", description = "Rule files to analyse logs", split = ",", required = true)
    private List<File> ruleFiles;

    @CommandLine.Option(names = "--logs", description = "Log files to analyse", split = ",", required = true)
    private List<File> logFiles;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Verbose progress output", required = false)
    private boolean verbose;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output report file", required = true)
    private String outputFile;

    private boolean argumentsSet = false;


    public void gatherOptions(String... args) {
        try {
            CommandLine.populateCommand(this, args);
            this.argumentsSet = true;
        } catch (Exception e) {
            CommandLine.usage(this, System.out);
        }
    }

    public List<File> getRuleFiles() {
        return ruleFiles;
    }


    public List<File> getLogFiles() {
        return logFiles;
    }


    public boolean isArgumentsSet() {
        return argumentsSet;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public String getOutputFile() {
        return this.outputFile;
    }

}
