package com.wordpress._0x10f8.logcheck.match;

import com.wordpress._0x10f8.logcheck.log.LogEntryReference;

public class RuleMatch {

    private String matchingRuleName;
    private LogEntryReference logReference;
    private String description;

    public RuleMatch(final String matchingRuleName, final LogEntryReference logReference) {
        this.matchingRuleName = matchingRuleName;
        this.logReference = logReference;
    }

    public String getMatchingRuleName() {
        return matchingRuleName;
    }

    public void setMatchingRuleName(final String matchingRuleName) {
        this.matchingRuleName = matchingRuleName;
    }

    public LogEntryReference getLogReference() {
        return logReference;
    }

    public void setLogReference(final LogEntryReference logReference) {
        this.logReference = logReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
