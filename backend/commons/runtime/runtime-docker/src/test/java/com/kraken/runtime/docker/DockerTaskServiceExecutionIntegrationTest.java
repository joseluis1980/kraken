package com.kraken.runtime.docker;

import com.google.common.collect.ImmutableMap;
import com.kraken.TestConfiguration;
import com.kraken.runtime.entity.*;
import com.kraken.runtime.logs.LogsService;
import com.kraken.tools.configuration.properties.ApplicationPropertiesTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class, ApplicationPropertiesTestConfiguration.class}, initializers = {ConfigFileApplicationContextInitializer.class})
@EnableAutoConfiguration
public class DockerTaskServiceExecutionIntegrationTest {

  @Autowired
  DockerTaskService taskService;

  @Autowired
  LogsService logsService;

  @Test
  public void shouldExecuteAndCancelStatus() throws InterruptedException {
    final var appId = "appId";
    final var logs = new ArrayList<Log>();
    final var disposable = logsService.listen(appId)
        .subscribeOn(Schedulers.elastic())
        .subscribe(logs::add);

    final var taskId = taskService.execute(appId, "description", TaskType.RECORD, ImmutableMap.of("KRAKEN_IMAGE", "nginx")).block();

    taskService.watch().filter(tasks -> tasks.size() > 0 && tasks.get(0).getStatus() == ContainerStatus.STARTING).next().block();

    final var task = taskService.list().next().block();

    assertThat(task).isNotNull();
    assertThat(task.getType()).isEqualTo(TaskType.RECORD);
    assertThat(task.getStatus()).isEqualTo(ContainerStatus.STARTING);
    assertThat(task.getDescription()).isEqualTo("description");
    assertThat(task.getId()).isEqualTo(taskId);
    assertThat(task.getContainers().size()).isEqualTo(2);
    assertThat(task.getContainers().get(0).getContainerId()).startsWith(taskId);
    assertThat(task.getContainers().get(0).getStatus()).isEqualTo(ContainerStatus.STARTING);

    taskService.cancel(appId, task).block();
    Thread.sleep(10000);
    disposable.dispose();
    final var logsString = logs.stream().map(Log::getText).reduce((s, s2) -> s + s2).orElse("");

    System.out.println(logs);

    assertThat(logsString).contains("Creating container-two_STARTING ... done");
    assertThat(logsString).contains("Creating container-one_STARTING ... done");
    assertThat(logsString).contains("Removing container-two_STARTING ... done");
    assertThat(logsString).contains("Removing container-one_STARTING ... done");
  }
}