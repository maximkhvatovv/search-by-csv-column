package ru.khvatov.testtasks.searchbycsvcolumn.banchmarktools;

import java.util.concurrent.TimeUnit;

import static java.lang.System.nanoTime;

public interface Stopwatch {
    void start();

    long getElapsedTime(final TimeUnit timeUnit);


    final class NanoTimeBasedStopwatch implements Stopwatch {
        private long startTime;

        public NanoTimeBasedStopwatch() {
            this.startTime = nanoTime();
        }

        @Override
        public void start() {
            startTime = nanoTime();
        }

        @Override
        public long getElapsedTime(final TimeUnit timeUnit) {
            final long duration = nanoTime() - startTime;
            return timeUnit.convert(duration, TimeUnit.NANOSECONDS);
        }

    }

}
