package com.wordpress._0x10f8.logcheck.gui.renderer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileListCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (value instanceof File) {
            return super.getListCellRendererComponent(list, ((File) value).getName(), index, isSelected, cellHasFocus);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    }

}
