package com.wordpress._0x10f8.logcheck.report;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.IOException;
import java.util.List;

public interface Report {

    public void handleOutput(final List<RuleMatch> evaluationResults) throws IOException;
}
