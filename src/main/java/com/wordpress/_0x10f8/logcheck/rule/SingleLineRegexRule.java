package com.wordpress._0x10f8.logcheck.rule;

import com.wordpress._0x10f8.logcheck.match.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SingleLineRegexRule extends AbstractRule {

    private String regularExpression;

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(final String regularExpression) {
        this.regularExpression = regularExpression;
    }

    @Override
    public List<RuleMatch> evaluate(final File logFile) throws IOException {
        final Pattern regularExpressionPattern = Pattern.compile(regularExpression);
        return super.mapFunctionToFile(logFile, (s -> regularExpressionPattern.matcher(s).find()));
    }
}
