package api.runners;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/web")
@IncludeTags("web")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "api.stepdefinitions.web")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:build/reports/cucumber/web-report.html, json:build/reports/cucumber/web-report.json")
public class WebRunnerTest {
}