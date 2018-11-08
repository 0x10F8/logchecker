package com.wordpress._0x10f8.logcheck.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CommandLineArgumentsTest {

	private static final String LOG_FILE = "logfile";
	private static final String RULE_FILE = "rulefile";
	private static final String OUTPUT_FILE = "output.txt";
	private static final short THREADS = 8;

	private static final String[] SINGLE_FILE_ARGS = ("-v " + "-o=" + OUTPUT_FILE + " --logs=" + LOG_FILE + " --rules="
			+ RULE_FILE + " -t=" + THREADS).split(" ");

	@Test
	public void testGetRuleFiles() throws Exception {
		final CommandLineArguments cliArgs = new CommandLineArguments();
		cliArgs.gatherOptions(SINGLE_FILE_ARGS);
		assertEquals(RULE_FILE, cliArgs.getRuleFiles().get(0).getName());
	}

	@Test
	public void testGetLogFiles() throws Exception {
		final CommandLineArguments cliArgs = new CommandLineArguments();
		cliArgs.gatherOptions(SINGLE_FILE_ARGS);
		assertEquals(LOG_FILE, cliArgs.getLogFiles().get(0).getName());
	}

	@Test
	public void testIsArgumentsSet() throws Exception {
		final CommandLineArguments cliArgs = new CommandLineArguments();
		cliArgs.gatherOptions(SINGLE_FILE_ARGS);
		assertTrue(cliArgs.isArgumentsSet());
	}

	@Test
	public void testIsVerbose() throws Exception {
		final CommandLineArguments cliArgs = new CommandLineArguments();
		cliArgs.gatherOptions(SINGLE_FILE_ARGS);
		assertTrue(cliArgs.isVerbose());
	}

	@Test
	public void testGetOutputFile() throws Exception {
		final CommandLineArguments cliArgs = new CommandLineArguments();
		cliArgs.gatherOptions(SINGLE_FILE_ARGS);
		assertEquals(OUTPUT_FILE, cliArgs.getOutputFile());
	}

	@Test
	public void testGetThreads() throws Exception {
		final CommandLineArguments cliArgs = new CommandLineArguments();
		cliArgs.gatherOptions(SINGLE_FILE_ARGS);
		assertEquals(new Short(THREADS), new Short(cliArgs.getThreads()));
	}

}
