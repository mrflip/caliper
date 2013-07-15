Caliper is Google's [open-source framework](https://code.google.com/p/caliper/) for writing, running and viewing the results of JavaMicrobenchmarks.

* Project: https://code.google.com/p/caliper/
* Code: `git clone https://code.google.com/p/caliper/`
* Download: https://code.google.com/p/caliper/downloads/list
* About Microbenchmarks: https://code.google.com/p/caliper/wiki/JavaMicrobenchmarks
* Javadoc: http://caliper.googlecode.com/svn/static/api/reference/com/google/caliper/package-summary.html

The latest release is 1.0-beta-1. It can be downloaded as a [single uberjar](https://code.google.com/p/caliper/downloads/detail?name=caliper-1.0-beta-1-all.jar) or referenced as a maven dependency:

```xml
    <dependency>
      <groupId>com.google.caliper</groupId>
      <artifactId>caliper</artifactId>
      <version>1.0-beta-1</version>
    </dependency>
```

### Getting Started

The simplest complete Caliper benchmark looks like this:

```java
    public class MyBenchmark extends Benchmark {
      public void timeMyOperation(int reps) {
        for (int i = 0; i < reps; i++) {
            MyClass.myOperation();
        }
      }
    }
```

See the examples in `examples/src/main/java/examples`, notably:

* `DemoBenchmark.java` -- an annotated example
* `NoOpBenchmark.java` -- ze goggles, zey do nussing! The absolute minimal benchmark, does nothing but time the rep loop.
* `MessageDigestCreationBenchmark` -- runs with several parameters
* `ArraySortBenchmark.java` -- example of assembling complex input before running

### Build

1. Get a local copy of the caliper repository with this command:

        git clone https://code.google.com/p/caliper/
	cd caliper

2. To build this project with Maven:

        cd caliper
        mvn eclipse:configure-workspace eclipse:eclipse install

3. To build examples:

        cd examples
        mvn eclipse:configure-workspace eclipse:eclipse install

### Usage

Run the included 'caliper' script, passing in classnames to benchmark:

        cd examples
        ../script/caliper examples.ArraySortBenchmark examples.DemoBenchmark.java

Or use maven directly; set 'exec.args' to the classname to benchmark

        cd examples
        mvn compile exec:java -Dexec.mainClass=com.google.caliper.runner.CaliperMain -Dexec.args=examples.ArraySortBenchmark

### License

[Apache License Version 2.0](http://www.apache.org/licenses/), January 2004 -- see the file COPYING in this directory
