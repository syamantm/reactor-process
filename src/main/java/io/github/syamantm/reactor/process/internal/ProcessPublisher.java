package io.github.syamantm.reactor.process.internal;

import io.github.syamantm.reactor.process.ProcessException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of org.reactivestreams.Publisher that publishes the output stream of a
 * java.lang.Process
 */
public class ProcessPublisher implements Publisher<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPublisher.class);

  private static final int END_PROCESS_SIGNAL_COUNT = 2;

  private final Process process;

  private final CountDownLatch terminationSignal;

  private final ExecutorService executorService;

  public ProcessPublisher(Process process) {
    this.process = process;
    this.terminationSignal = new CountDownLatch(END_PROCESS_SIGNAL_COUNT);
    this.executorService = Executors.newSingleThreadExecutor();
  }

  @Override
  public void subscribe(Subscriber<? super String> subscriber) {
    registerExitCodeHandler();
    this.executorService.submit(() -> handleTermination(subscriber));

    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

    subscriber.onSubscribe(new Subscription() {
      @Override
      public void request(long n) {
        streamProcessOutput(n, br, subscriber);
      }

      @Override
      public void cancel() {
        process.destroy();
      }
    });
  }

  private void streamProcessOutput(long n, BufferedReader br,
      Subscriber<? super String> subscriber) {
    try {
      LOGGER.debug("Requested {} lines", n);
      for (int i = 0; i < n; i++) {
        String line = br.readLine();
        if (line != null) {
          LOGGER.debug(line);
          subscriber.onNext(line);
        } else {
          this.terminationSignal.countDown();
          break;
        }
      }
    } catch (Exception e) {
      subscriber.onError(new ProcessException(e));
    }
  }

  private void registerExitCodeHandler() {
    process.onExit().thenAccept(p -> {
      LOGGER.debug("Received end of process signal");
      this.terminationSignal.countDown();
    });
  }

  /**
   * A function to handle process termination. This can't be done simply on Process.onExit() as the
   * process output may not be fully consumed yet by the subscriber. It waits for a latch which acts
   * as signals to indicates : 1. the processing on the process stream has finished 2. the
   * Process.onExit event has happened. Once, both the signals are received, this class indicates
   * the terminal state to the subscriber by either calling subscriber.onComplete() or
   * subscriber.onError() based on Process.exitValue()
   */

  private void handleTermination(Subscriber<? super String> subscriber) {
    try {
      terminationSignal.await();
      int exitCode = process.exitValue();
      if (exitCode != 0) {
        LOGGER.error("Exit code : {}", exitCode);
        subscriber.onNext("Exit code : " + exitCode);
        subscriber.onError(new ProcessException(exitCode));
      } else {
        subscriber.onNext("Exit code : " + exitCode);
        subscriber.onComplete();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
