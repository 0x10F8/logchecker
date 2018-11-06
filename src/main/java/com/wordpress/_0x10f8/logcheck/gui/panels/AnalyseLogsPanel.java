package com.wordpress._0x10f8.logcheck.gui.panels;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.Rule;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalyseLogsPanel extends JPanel {

    private List<Rule> rules = new ArrayList<>();
    private List<File> logFiles = new ArrayList<>();
    private final List<RuleMatch> allMatches = new ArrayList<>();

    private JProgressBar progressBar;
    private JLabel messageLabel;

    public AnalyseLogsPanel() {
        super(new BorderLayout());
        this.messageLabel = new JLabel("");
        this.progressBar = new JProgressBar();
        this.progressBar.setMaximum(100);
        this.progressBar.setValue(0);

        this.add(messageLabel, BorderLayout.CENTER);
        this.add(progressBar, BorderLayout.SOUTH);
    }

    public List<RuleMatch> results() {
        return this.allMatches;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public List<File> getLogFiles() {
        return logFiles;
    }

    public void setLogFiles(List<File> logFiles) {
        this.logFiles = logFiles;
    }

    public void reset() {
        int totalNumberOfActions = this.logFiles.size() * this.rules.size();
        this.progressBar.setMaximum(totalNumberOfActions);
        this.messageLabel.setText("Ready to analyse selected files");
        this.allMatches.clear();
    }

    public void startAnalysis() {
        new Thread(() -> {
            for (final File logFile : logFiles) {
                for (final Rule rule : rules) {
                    try {
                        this.progressBar.setValue(this.progressBar.getValue() + 1);
                        this.messageLabel.setText("Analysing file " + logFile.getName() + " with rule " + rule.getName());
                        allMatches.addAll(rule.evaluate(logFile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.messageLabel.setText("Analysis complete.");
        }).start();
    }
}
