package software.validation;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:build/reports/tests/test/cucumber-html-report.html", 
              "json:build/reports/tests/test/cucumber.json"},
    features = "src/test/resources",   // Path to your feature files
    glue = {"software.validation"}  
)
public class CucumberTestsRunner{
}
