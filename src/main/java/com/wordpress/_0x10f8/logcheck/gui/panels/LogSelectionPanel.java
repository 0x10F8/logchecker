package com.wordpress._0x10f8.logcheck.gui.panels;

import com.wordpress._0x10f8.logcheck.gui.renderer.FileListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class LogSelectionPanel extends JPanel {

    private static final String SELECT_LOGS_DIRECTORY = "Select Logs Directory...";

    private final JFileChooser directoryChooser;
    private File ruleDirectory;
    private final JList<File> fileList;
    private final DefaultListModel<File> listModel;

    public LogSelectionPanel() {
        super(new BorderLayout());

        final JButton selectLogsDirectory = new JButton(SELECT_LOGS_DIRECTORY);
        selectLogsDirectory.addActionListener(e -> chooseDirectory());

        this.listModel = new DefaultListModel<>();
        this.fileList = new JList<>(listModel);
        this.fileList.setCellRenderer(new FileListCellRenderer());

        this.add(selectLogsDirectory, BorderLayout.NORTH);
        this.add(new JScrollPane(this.fileList), BorderLayout.CENTER);

        directoryChooser = new JFileChooser();
        directoryChooser.setDialogTitle(SELECT_LOGS_DIRECTORY);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setAcceptAllFileFilterUsed(false);


    }

    public List<File> getSelectedLogs() {
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
        for (final File jsonFile : this.ruleDirectory.listFiles()) {
            this.listModel.addElement(jsonFile);
        }
        this.fileList.repaint();
    }

}
