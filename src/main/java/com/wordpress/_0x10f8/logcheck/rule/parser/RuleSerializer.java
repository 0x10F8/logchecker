package com.wordpress._0x10f8.logcheck.rule.parser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.wordpress._0x10f8.logcheck.rule.CompositeRule;
import com.wordpress._0x10f8.logcheck.rule.Rule;

/**
 * The rule serializer uses GSON to parse JSON rule files into constructed
 * classes.
 *
 */
public class RuleSerializer implements JsonSerializer<Rule>, JsonDeserializer<Rule> {

	private static final String RULE_TYPE_FIELD = "ruleType";
	private static final String RULES_PROP = "rules";
	private static final String RULE_PACKAGE_FMT_STR = "com.wordpress._0x10f8.logcheck.rule.%s";
	private final Gson gson = new Gson();

	/**
	 * Serialize the rule into JSON
	 */
	@Override
	public JsonElement serialize(final Rule rule, final Type type,
			final JsonSerializationContext jsonSerializationContext) {
		final JsonElement json = gson.toJsonTree(rule);
		/*
		 * If the rule is composite then serialize the sub-rules independently and add
		 * to the JSON rule list
		 */
		if (rule instanceof CompositeRule) {
			json.getAsJsonObject().addProperty(RULE_TYPE_FIELD, rule.getClass().getSimpleName());
			final List<Rule> rules = ((CompositeRule) rule).getRules();
			int totalRulesInJson = json.getAsJsonObject().getAsJsonArray(RULES_PROP).size();
			for (int i = totalRulesInJson - 1; i >= 0; i--) {
				json.getAsJsonObject().getAsJsonArray(RULES_PROP).remove(i);
			}
			for (final Rule compositeRule : rules) {
				json.getAsJsonObject().getAsJsonArray(RULES_PROP)
						.add(serialize(compositeRule, type, jsonSerializationContext));
			}
		} else {
			json.getAsJsonObject().addProperty(RULE_TYPE_FIELD, rule.getClass().getSimpleName());
		}
		return json;
	}

	/**
	 * Deserialize the JSON content into a instantiated class
	 */
	@Override
	public Rule deserialize(final JsonElement jsonElement, final Type type,
			final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		Class<?> actualClass;
		final JsonObject jsonObject = jsonElement.getAsJsonObject();
		final String simpleClassName = jsonObject.get(RULE_TYPE_FIELD).getAsString();
		final String className = String.format(RULE_PACKAGE_FMT_STR, simpleClassName);
		try {
			actualClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new JsonParseException(e.getMessage());
		}

		final Rule rule;

		/*
		 * If the rule is composite then deserialize the sub-rules independently and add
		 * to the rule list
		 */
		if (actualClass == CompositeRule.class) {
			final JsonElement jsonRulesList = jsonElement.getAsJsonObject().getAsJsonArray(RULES_PROP);
			jsonElement.getAsJsonObject().remove(RULES_PROP);
			rule = (CompositeRule) gson.fromJson(jsonElement, actualClass);
			((CompositeRule) rule).setRules(new ArrayList<>());
			for (int i = 0; i < jsonRulesList.getAsJsonArray().size(); i++) {
				Rule compositeRule = deserialize(jsonRulesList.getAsJsonArray().get(i), type,
						jsonDeserializationContext);
				((CompositeRule) rule).addRule(compositeRule);
			}
		} else {
			rule = (Rule) gson.fromJson(jsonElement, actualClass);
		}

		return rule;
	}
}
