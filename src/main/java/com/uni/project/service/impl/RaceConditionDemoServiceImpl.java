package com.uni.project.service.impl;

import com.uni.project.model.dto.response.RaceConditionDemoResponse;
import com.uni.project.service.RaceConditionDemoService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class RaceConditionDemoServiceImpl implements RaceConditionDemoService {
    private static final String UNSAFE_MODE = "unsafe";
    private static final String ATOMIC_MODE = "atomic";

    @Override
    public RaceConditionDemoResponse runUnsafeDemo(int threadCount, int incrementsPerThread) {
        return runDemo(UNSAFE_MODE, new UnsafeCounter(), threadCount, incrementsPerThread);
    }

    @Override
    public RaceConditionDemoResponse runAtomicDemo(int threadCount, int incrementsPerThread) {
        return runDemo(ATOMIC_MODE, new AtomicCounter(), threadCount, incrementsPerThread);
    }

    private RaceConditionDemoResponse runDemo(
            String mode,
            Counter counter,
            int threadCount,
            int incrementsPerThread
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        try {
            IntStream.range(0, threadCount)
                    .forEach(index -> executorService.execute(
                            () -> runWorker(counter, incrementsPerThread, readyLatch, startLatch, doneLatch)
                    ));

            awaitLatch(readyLatch, "Race condition demo setup interrupted");
            startLatch.countDown();
            awaitLatch(doneLatch, "Race condition demo execution interrupted");

            long expected = (long) threadCount * incrementsPerThread;
            long actual = counter.getValue();
            return new RaceConditionDemoResponse(
                    mode,
                    threadCount,
                    incrementsPerThread,
                    expected,
                    actual,
                    expected - actual
            );
        } finally {
            shutdownExecutor(executorService);
        }
    }

    private void runWorker(
            Counter counter,
            int incrementsPerThread,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            CountDownLatch doneLatch
    ) {
        readyLatch.countDown();
        try {
            startLatch.await();
            for (int i = 0; i < incrementsPerThread; i++) {
                counter.increment();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Race condition worker interrupted", ex);
        } finally {
            doneLatch.countDown();
        }
    }

    private void awaitLatch(CountDownLatch latch, String errorMessage) {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(errorMessage, ex);
        }
    }

    private void shutdownExecutor(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private interface Counter {
        void increment();

        int getValue();
    }

    private static final class UnsafeCounter implements Counter {
        private int value;

        @Override
        public void increment() {
            int currentValue = value;
            Thread.yield();
            value = currentValue + 1;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    private static final class AtomicCounter implements Counter {
        private final AtomicInteger value = new AtomicInteger();

        @Override
        public void increment() {
            value.incrementAndGet();
        }

        @Override
        public int getValue() {
            return value.get();
        }
    }
}
