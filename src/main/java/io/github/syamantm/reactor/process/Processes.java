package io.github.syamantm.reactor.process;

import io.github.syamantm.reactor.process.internal.ProcessPublisher;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * A utility class providing the main entrypoint to the API.
 * <pre>{@code
 *  import io.github.syamantm.reactor.process.Processes
 *  ......
 *
 *  public class MyStreamingProcess {
 *    public Flux<String> processAsStream() {
 *      ProcessBuilder pb = new ProcessBuilder().command(...);
 *
 *      // Return the reactive stream from a ProcessBuilder
 *      return Processes.fromBuilder(pb);
 *    }
 *  }
 *  }</pre>
 */
public class Processes {

  private static final Logger LOGGER = LoggerFactory.getLogger(Processes.class);

  /**
   * Create a A Reactive Streams of type Flux from a java.lang.Process. Process output and error
   * will be captured in the same stream and Flux error will be signaled on process failure, i.e. if
   * the Process.exitValue() is non-zero.
   *
   * @param pb a fully constructed ProcessBuilder object, with all commands and arguments
   * @return A Reactive Streams from the process builder
   */
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
