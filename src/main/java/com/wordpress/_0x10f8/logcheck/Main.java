package com.wordpress._0x10f8.logcheck;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordpress._0x10f8.logcheck.match.RuleMatch;
import com.wordpress._0x10f8.logcheck.rule.Rule;
import com.wordpress._0x10f8.logcheck.rule.parser.RuleSerializer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Rule.class, new RuleSerializer());
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        Gson gson = builder.create();


        Rule fromFile = gson.fromJson(new String(Files.readAllBytes(Paths.get("./rules/VeryActiveClient.json"))), Rule.class);
        final File logFile = new File("./logs/test_log");
        final List<RuleMatch> matches = fromFile.evaluate(logFile);
        System.out.println("Found " + matches.size() + " log rows in file [" + logFile.getName() + "] matching the rule [" + fromFile.getName() + "]");

        for (int i = 0; i < matches.size() && i < 100; i++) {
            final RuleMatch match = matches.get(i);
            System.out.println("Found match " + match.getMatchingRuleName() + " " + match.getDescription());
        }

    }
}
