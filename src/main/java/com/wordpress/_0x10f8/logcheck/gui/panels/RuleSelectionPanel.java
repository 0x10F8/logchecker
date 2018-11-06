package com.wordpress._0x10f8.logcheck.gui.panels;

import com.wordpress._0x10f8.logcheck.gui.renderer.FileListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class RuleSelectionPanel extends JPanel {

    private static final String SELECT_RULES_TEXT = "Select Rules Directory...";
    private static final String RULE_EXT = "json";

    private final JFileChooser directoryChooser;
    private File ruleDirectory;
    private final JList<File> fileList;
    private final DefaultListModel<File> listModel;

    public RuleSelectionPanel() {
        super(new BorderLayout());

        final JButton selectRulesDirectory = new JButton(SELECT_RULES_TEXT);
        selectRulesDirectory.addActionListener(e -> chooseDirectory());

        this.listModel = new DefaultListModel<>();
        this.fileList = new JList<>(listModel);
        this.fileList.setCellRenderer(new FileListCellRenderer());

        this.add(selectRulesDirectory, BorderLayout.NORTH);
        this.add(new JScrollPane(this.fileList), BorderLayout.CENTER);

        directoryChooser = new JFileChooser();
        directoryChooser.setDialogTitle(SELECT_RULES_TEXT);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setAcceptAllFileFilterUsed(false);


    }

    public List<File> getSelectedRules() {
        return this.fileList.getSelectedValuesList();
    }

    private void chooseDirectory() {
        if (this.directoryChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.ruleDirectory = this.directoryChooser.getSelectedFile();
            updateFileList();
        }
    }

    private void updateFileList() {
        this.fileList.clearSelection();
        this.listModel.removeAllElements();
        for (final File jsonFile : this.ruleDirectory.listFiles(f -> f.getName().endsWith(RULE_EXT))) {
            this.listModel.addElement(jsonFile);
        }
        this.fileList.repaint();
    }

}
