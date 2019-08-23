package com.kraken.analysis.properties;

import com.kraken.tools.configuration.properties.ApplicationPropertiesTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(
    classes = {AnalysisPropertiesConfiguration.class, ApplicationPropertiesTestConfiguration.class},
    initializers = {ConfigFileApplicationContextInitializer.class})
public class AnalysisPropertiesConfigurationTest {

  @Autowired
  AnalysisProperties properties;

  @Test
  public void shouldCreateProperties() {
    assertThat(properties.getResultPath("testId")).isEqualTo(Paths.get("testDir", "data", "testId"));
  }

}
