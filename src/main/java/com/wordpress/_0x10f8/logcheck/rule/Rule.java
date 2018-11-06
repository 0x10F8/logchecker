package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Rule {

    String getName();

    void setName(final String name);

    List<RuleMatch> evaluate(final File logFile) throws IOException;
}
