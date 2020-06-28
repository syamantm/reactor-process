package io.github.syamantm.reactor.process;

public class ProcessException extends RuntimeException {

  private int exitCode = -1;

  public ProcessException(int exitCode) {
    super("Process exited with code : " + exitCode);
    this.exitCode = exitCode;
  }

  public ProcessException(Throwable t) {
    super(t);
  }

  public int getExitCode() {
    return exitCode;
  }
}
