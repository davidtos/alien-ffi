package com.davidvlijmincx;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class Assignment11_PerformanceTest {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Assignment11_PerformanceTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark()
    public void improvedProcessPing(Blackhole blackhole) throws Throwable {
        var result = Assignment10_Performance.improvedProcessPing(1,1);
        blackhole.consume(result);
    }

    @Benchmark()
    public void poorImplemention(Blackhole blackhole){
        var result = Assignment10_Performance.processPing(1,1);
        blackhole.consume(result);
    }

}