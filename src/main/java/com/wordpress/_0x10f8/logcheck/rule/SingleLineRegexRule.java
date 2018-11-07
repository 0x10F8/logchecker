package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Rule to match log file entries based on a regular expression mapped to a single log line.
 */
public class SingleLineRegexRule extends AbstractRule {

    private String regularExpression;

    /**
     * Get the regular expression used for this rule
     *
     * @return the regular expression used
     */
    public String getRegularExpression() {
        return regularExpression;
    }

    /**
     * Set the regular expression used for this rule
     *
     * @param regularExpression The regular expression to check the log with
     */
    public void setRegularExpression(final String regularExpression) {
        this.regularExpression = regularExpression;
    }


    @Override
    public List<RuleMatch> evaluate(final File logFile) throws IOException {
        final Pattern regularExpressionPattern = Pattern.compile(regularExpression);
        return super.mapFunctionToFile(logFile, (s -> regularExpressionPattern.matcher(s).find()));
    }
}
