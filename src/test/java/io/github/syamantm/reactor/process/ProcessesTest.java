package io.github.syamantm.reactor.process;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class ProcessesTest {

  @Test
  public void shouldStreamProcessOutputSuccessfully() {
    //Given
    ProcessBuilder pb = new ProcessBuilder().command("ls");

    //When
    Flux<String> result = Processes.fromBuilder(pb);

    //Then
    StepVerifier
        .create(result)
        .thenConsumeWhile(Objects::nonNull, System.out::println)
        .expectComplete()
        .verify();
  }

  @Test
  public void shouldStreamAScriptOutputSuccessfully() {
    //Given
    ProcessBuilder pb = new ProcessBuilder().command("./src/test/scripts/success.sh");

    //When
    Flux<String> result = Processes.fromBuilder(pb);

    //Then
    StepVerifier
        .create(result)
        .thenConsumeWhile(Objects::nonNull, System.out::println)
        .expectComplete()
        .verify();
  }

  @Test
  public void shouldStreamAScriptWithFailure() {
    //Given
    ProcessBuilder pb = new ProcessBuilder().command("./src/test/scripts/failure.sh");

    //When
    Flux<String> result = Processes.fromBuilder(pb);

    //Then
    StepVerifier
        .create(result)
        .thenConsumeWhile(Objects::nonNull, System.out::println)
        .expectError()
        .verify();
  }

  @Test
  public void shouldSignalFailureWhenProcessDoesNotExist() {
    //Given
    ProcessBuilder pb = new ProcessBuilder().command("foo");

    //When
    Flux<String> result = Processes.fromBuilder(pb);

    //Then
    StepVerifier
        .create(result)
        .thenConsumeWhile(Objects::nonNull, System.out::println)
        .verifyError();
  }

}