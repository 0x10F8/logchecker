package com.wordpress._0x10f8.logcheck.rule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.parser.RuleSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ContainsRule extends AbstractRule {

    public static void main(String[] args) throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Rule.class, new RuleSerializer());
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        Gson gson = builder.create();

        // from file
        Rule fromFile = gson.fromJson(new String(Files.readAllBytes(Paths.get("./rules/SQLiContains.json"))), Rule.class);
        final File logFile = new File("./logs/ssl_access_log");
        final List<RuleMatch> matches = fromFile.evaluate(logFile);
        System.out.println("Found " + matches.size() + " log rows in file [" + logFile.getName() + "] matching the rule [" + fromFile.getName() + "]");


    }

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
