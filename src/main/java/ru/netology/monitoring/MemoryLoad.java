package ru.netology.monitoring;

import java.util.ArrayList;
import java.util.List;

/** Bounded heap load simulator. Entries expire to avoid an unbounded accidental OOM. */
public final class MemoryLoad {
    private static final int MAX_RETAINED_MIB = 384;
    private static final long TTL_NANOS = 120_000_000_000L;
    private static final List<Entry> RETAINED = new ArrayList<>();

    private MemoryLoad() {}

    public static synchronized void allocate(int mebibytes) {
        long now = System.nanoTime();
        RETAINED.removeIf(entry -> now - entry.createdAt > TTL_NANOS);
        int retained = RETAINED.stream().mapToInt(entry -> entry.bytes.length / (1024 * 1024)).sum();
        int actual = Math.min(mebibytes, Math.max(1, MAX_RETAINED_MIB - retained));
        byte[] bytes = new byte[actual * 1024 * 1024];
        for (int i = 0; i < bytes.length; i += 4096) bytes[i] = 1;
        RETAINED.add(new Entry(bytes, now));
    }

    public static synchronized int retainedMegabytes() {
        long now = System.nanoTime();
        RETAINED.removeIf(entry -> now - entry.createdAt > TTL_NANOS);
        return RETAINED.stream().mapToInt(entry -> entry.bytes.length / (1024 * 1024)).sum();
    }

    private record Entry(byte[] bytes, long createdAt) {}
}
