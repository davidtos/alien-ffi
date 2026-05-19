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
public class Assignment12_PerformanceTest {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Assignment12_PerformanceTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    int x = 1;
    int y = 1;

    @Benchmark()
    public void improvedProcessPing(Blackhole blackhole, Assignment12_PerformanceTest plan) throws Throwable {
        var result = Assignment12_Performance.improvedProcessPing(plan.x,plan.y);
        blackhole.consume(result);
    }

    @Benchmark()
    public void poorImplemention(Blackhole blackhole, Assignment12_PerformanceTest plan){
        var result = Assignment12_Performance.processPing(plan.x,plan.y);
        blackhole.consume(result);
    }

}