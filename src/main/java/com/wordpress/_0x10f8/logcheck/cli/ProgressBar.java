package com.wordpress._0x10f8.logcheck.cli;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

/**
 * A CLi progress bar
 */
public class ProgressBar {

	private final short barLength;
	private final short minimumValue;
	private final short messageLength;

	private short maximumValue;
	private String message;
	private long startTime;
	private short currentValue;

	/* Progress Bar Formatting String */
	private static final String PROGRESS_BAR_FORMAT = "%s %s%% %s %s/%d (%s)";

	/**
	 * No args constructor, sets up a progress bar length 30 with values 0-100 and
	 * message length 15
	 */
	public ProgressBar() {
		this.barLength = 30;
		this.messageLength = 15;
		this.maximumValue = 100;
		this.minimumValue = 0;
		this.currentValue = 0;
	}

	/**
	 * Setup a fully customized progress bar
	 *
	 * @param barLength     The bar length in characters
	 * @param minimumValue  The minimum progress value
	 * @param maximumValue  The maximum progress value
	 * @param messageLength The message length
	 */
	public ProgressBar(final short barLength, final short minimumValue, final short maximumValue, final short messageLength) {
		this.barLength = barLength;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
		this.messageLength = messageLength;
		this.currentValue = minimumValue;
	}

	/**
	 * Set the maximum value
	 *
	 * @param maximumValue The maximum value
	 */
	public void setMaximumValue(short maximumValue) {
		this.maximumValue = maximumValue;
	}

	/**
	 * Set the message to display
	 *
	 * @param message the message
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * Set the current value
	 *
	 * @param currentValue the current value
	 */
	public void setCurrentValue(final short currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * Set the start time to calculate progress time from
	 */
	public void setStartTime() {
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * Get the total time taken as a formatted string in XXm XXs
	 *
	 * @return total time string
	 */
	public String getTotalTime() {
		return getTimeString();
	}

	/**
	 * Print the progress bar
	 *
	 * @param printStream The print stream to print the progress bar to
	 */
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

		final String progressBar = String.format(PROGRESS_BAR_FORMAT + "\r", formattedMessage, percentString, bar,
				currentValueString, this.maximumValue, time);

		printStream.print(progressBar);
	}

	/**
	 * Print done to the stream with a newline either side
	 *
	 * @param printStream the stream to print to
	 */
	public void done(final PrintStream printStream) {
		printStream.println("\ndone.");
	}

	/**
	 * Pad or trim a string down to a specific size using a specific padding
	 * character
	 *
	 * @param original    The original string
	 * @param length      The target length
	 * @param paddingChar The padding character
	 *
	 * @return a new string of length specified padded with the padding character
	 */
	private String padString(final String original, final int length, final char paddingChar) {
		String newString = original;
		final int extraSpace = 2;
		if (newString.length() > length - extraSpace) {
			newString = newString.substring(0, length - extraSpace);
		}
		while (newString.length() <= length) {
			newString += paddingChar;
		}
		return newString;
	}

	/**
	 * Create the progress bar section of the message of a specified percent
	 * complete. Creates a string which has format [=====> ] where the number of
	 * equals is the percent complete in related to the bar length field.
	 *
	 * @param percent the percent complete to show on the progress bar
	 *
	 * @return a formatted string progress bar
	 */
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

	/**
	 * Get the time taken as a string in minutes and seconds
	 *
	 * @return time taken string
	 */
	private String getTimeString() {
		final long currentTime = System.currentTimeMillis();
		final long duration = currentTime - this.startTime;

		return String.format("%02dm, %02ds", TimeUnit.MILLISECONDS.toMinutes(duration),
				TimeUnit.MILLISECONDS.toSeconds(duration)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
	}

}
