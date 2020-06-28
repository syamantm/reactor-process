package io.github.syamantm.reactor.process;

import io.github.syamantm.reactor.process.internal.ProcessPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.IOException;

public class Processes {

  private static final Logger LOGGER = LoggerFactory.getLogger(Processes.class);

  public static Flux<String> fromBuilder(ProcessBuilder pb) {
    try {
      Process process = pb.redirectErrorStream(true).start();
      return Flux.from(new ProcessPublisher(process));
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
      return Flux.error(new ProcessException(e));
    }
  }
}
