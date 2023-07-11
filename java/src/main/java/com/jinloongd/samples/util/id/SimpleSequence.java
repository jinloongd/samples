package com.jinloongd.samples.util.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author JinLoong.Du
 */
public class SimpleSequence implements IdGenerator {

    private final AtomicLong atomicLong;

    public SimpleSequence() {
        this(1L);
    }

    public SimpleSequence(long start) {
        this.atomicLong = new AtomicLong(start);
    }

    @Override
    public synchronized long nextId() {
        return atomicLong.getAndIncrement();
    }
}
