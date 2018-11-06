package com.wordpress._0x10f8.logcheck.rule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordpress._0x10f8.logcheck.rule.parser.RuleSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class RuleFactory {

    public static List<Rule> loadRulesFromFiles(final List<File> ruleJsonFiles) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Rule.class, new RuleSerializer());
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        Gson gson = builder.create();

        final List<Rule> rulesLoaded = new ArrayList<>();

        for (final File ruleFile : ruleJsonFiles) {
            final Rule fromFile = gson.fromJson(new String(Files.readAllBytes(Paths.get(ruleFile.toURI()))), Rule.class);
            rulesLoaded.add(fromFile);
        }

        return rulesLoaded;
    }
}
