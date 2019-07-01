package com.kraken.commons.docker.client;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
@ContextConfiguration(
    classes = {DockerClientConfiguration.class},
    initializers = {ConfigFileApplicationContextInitializer.class})
public class DockerClientPropertiesConfigurationTest {

  @Autowired
  DockerClientProperties properties;

  @Qualifier("webClientDocker")
  @Autowired
  WebClient dockerWebClient;

  @Test
  public void shouldCreateProperties() {
    Assertions.assertThat(properties.getDockerUrl()).isNotNull();
  }

  @Test
  public void shouldCreateWebClients() {
    Assertions.assertThat(dockerWebClient).isNotNull();
  }
}
