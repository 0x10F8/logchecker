package com.wordpress._0x10f8.logcheck.cli;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class ProgressBar {

    private final int barLength;
    private int maximumValue;
    private final int minimumValue;
    private final int messageLength;

    private String message;
    private long startTime;
    private int currentValue;

    private static final String PROGRESS_BAR_FORMAT = "%s %s%% %s %s/%d (%s)";

    public ProgressBar() {
        this.barLength = 30;
        this.messageLength = 15;
        this.maximumValue = 100;
        this.minimumValue = 0;
        this.currentValue = 0;
    }

    public ProgressBar(final int barLength, final int minimumValue, final int maximumValue, final int messageLength) {
        this.barLength = barLength;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.messageLength = messageLength;
        this.currentValue = minimumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setCurrentValue(final int currentValue) {
        this.currentValue = currentValue;
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public String getTotalTime() {
        return getTimeString();
    }

    public void printProgressBar(final PrintStream printStream) {
        final String formattedMessage = padString(this.message, this.messageLength, ' ');

        final int difference = 0 - this.minimumValue;
        final int max = (this.maximumValue - difference);
        final int curr = (this.currentValue - difference);
        final int percent = (int) (1d * curr / max * 100d);

        final String percentString = String.format("%03d", percent);

        final String bar = createProgressBar(percent);

        final String time = getTimeString();


        final int numLength = ("" + maximumValue).length();
        final String currentValueString = String.format("%0" + numLength + "d", this.currentValue);

        final String progressBar = String.format(PROGRESS_BAR_FORMAT + "\r", formattedMessage, percentString, bar, currentValueString, this.maximumValue, time);

        printStream.print(progressBar);
    }

    public void done(final PrintStream printStream) {
        printStream.println("\ndone.");
    }

    private String padString(final String original, final int length, final char paddingChar) {
        String newString = original;
        if (newString.length() > length) {
            newString = newString.substring(0, length);
        } else {
            while (newString.length() <= length) {
                newString += paddingChar;
            }
        }
        return newString;
    }

    private String createProgressBar(final int percent) {
        String bar = "[";
        final int number = (int) (1d * percent / 100d * barLength);

        boolean arrowSet = false;

        for (int i = 0; i < barLength - 1; i++) {
            if (i < number) {
                bar += "=";
            } else if (!arrowSet) {
                arrowSet = true;
                bar += ">";
            } else {
                bar += " ";
            }
        }

        bar += "]";

        return bar;
    }

    private String getTimeString() {
        final long currentTime = System.currentTimeMillis();
        final long duration = currentTime - this.startTime;

        return String.format("%02dm, %02ds",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }


}
