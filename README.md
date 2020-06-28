# reactor-process

![Java CI](https://github.com/syamantm/reactor-process/workflows/CI%20Build/badge.svg?branch=master)

## Code Style
[Follow google styleguide](https://github.com/google/styleguide)

## Add dependency

### Gradle
```groovy
api("io.github.syamantm:reactor-process:0.0.1-SNAPSHOT")
```
### Maven
```xml
<dependency>
  <groupId>io.github.syamantm</groupId>
  <artifactId>reactor-process</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Usage

```java
// Import the Processes utility class
import io.github.syamantm.reactor.process.Processes;

public class MyStreamingProcess {
  public Flux<String> processAsStream() { 
    // Create a ProcessBuilder
    ProcessBuilder pb = new ProcessBuilder().command("ls");

    // Return the reactive stream from a ProcessBuilder
    return Processes.fromBuilder(pb);
  }
}
```
