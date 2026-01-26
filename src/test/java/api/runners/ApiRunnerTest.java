package api.runners;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/api")
@IncludeTags("api")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "api.stepdefinitions.api")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:build/reports/cucumber/api-report.html, json:build/reports/cucumber/api-report.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@api")
public class ApiRunnerTest {
}