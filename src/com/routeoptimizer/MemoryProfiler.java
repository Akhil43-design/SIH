package com.routeoptimizer;

public class MemoryProfiler {
    
    public static long getUsedMemoryMb() {
        System.gc(); // Request garbage collection for more accurate baseline
        Runtime runtime = Runtime.getRuntime();
        long usedBytes = runtime.totalMemory() - runtime.freeMemory();
        return usedBytes / (1024 * 1024);
    }
    
    public static long getPeakMemoryMb() {
        // Since Java doesn't track historical peak natively easily without MXBeans,
        // we return the max memory available to JVM as a fallback or the current used.
        // For actual profiling, we sample getUsedMemoryMb() periodically.
        Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory() / (1024 * 1024);
    }
    
    public static void printMemoryStats(String stage) {
        long used = getUsedMemoryMb();
        long total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        System.out.println(String.format("[%s] Memory - Used: %d MB, Total allocated: %d MB, Max allowed: %d MB", 
                stage, used, total, max));
    }
}
