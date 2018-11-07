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

/**
 * Factory for generating {@link Rule} implementations parsed from JSON configuration files.
 */
public final class RuleFactory {

    /**
     * Load the {@link Rule} implementations from the configuration files specified.
     *
     * @param ruleJsonFiles A list of files containing the JSON configurations for the rules
     *
     * @return A list of {@link Rule} implementations for the configurations
     *
     * @throws IOException If there was an issue reading the configuration files
     */
    public static List<Rule> loadRulesFromFiles(final List<File> ruleJsonFiles) throws IOException {

        /* Create a Gson object which will deserialize with our serializer */
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Rule.class, new RuleSerializer());
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();

        final Gson gson = builder.create();

        final List<Rule> rulesLoaded = new ArrayList<>();

        /* Deserialize the rules from the files */
        for (final File ruleFile : ruleJsonFiles) {
            final Rule fromFile = gson.fromJson(new String(Files.readAllBytes(Paths.get(ruleFile.toURI()))), Rule.class);
            rulesLoaded.add(fromFile);
        }

        return rulesLoaded;
    }
}
