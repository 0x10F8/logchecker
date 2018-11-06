package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ContainsRule extends AbstractRule {

    enum CaseType {
        IGNORE_CASE, CASE_SENSITIVE;
    }

    private CaseType caseType;
    private String containsString;

    public CaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(final CaseType caseType) {
        this.caseType = caseType;
    }

    public String getContainsString() {
        return containsString;
    }

    public void setContainsString(final String containsString) {
        this.containsString = containsString;
    }

    @Override
    public List<RuleMatch> evaluate(final File logFile) throws IOException {
        final List<RuleMatch> matches;
        if (this.caseType == CaseType.CASE_SENSITIVE) {
            matches = super.mapFunctionToFile(logFile, (s -> s.contains(this.containsString)));
        } else {
            matches = super.mapFunctionToFile(logFile, (s -> s.toLowerCase().contains(this.containsString.toLowerCase())));
        }
        return matches;
    }

}
