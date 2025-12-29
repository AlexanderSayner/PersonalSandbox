# Advanced Java Concurrency Guide: A Comprehensive Deep Dive

## Table of Contents
1. [Introduction](#introduction)
2. [Advanced Threading Concepts](#advanced-threading-concepts)
3. [Memory Visibility & Happens-Before Relationships](#memory-visibility--happens-before-relationships)
4. [Advanced Synchronization Techniques](#advanced-synchronization-techniques)
5. [Concurrent Data Structures](#concurrent-data-structures)
6. [Advanced Executors & Thread Pools](#advanced-executors--thread-pools)
7. [Lock-Free Programming](#lock-free-programming)
8. [Actor Model & Alternative Paradigms](#actor-model--alternative-paradigms)
9. [Performance Monitoring & Debugging](#performance-monitoring--debugging)
10. [Microservices Concurrency Patterns](#microservices-concurrency-patterns)
11. [Advanced Code Examples](#advanced-code-examples)
12. [Monitoring Tools for Microservices](#monitoring-tools-for-microservices)

## Introduction

Java concurrency is a complex and nuanced topic that goes far beyond basic threading. This guide explores the most advanced concepts, patterns, and techniques for managing concurrent execution in Java applications, particularly in microservice environments.

## Advanced Threading Concepts

### Thread States & Lifecycle

Java threads have 6 distinct states:
- NEW: Thread created but not started
- RUNNABLE: Thread executing in JVM (includes waiting for OS resources)
- BLOCKED: Thread waiting for monitor lock
- WAITING: Thread waiting indefinitely for another thread
- TIMED_WAITING: Thread waiting with timeout
- TERMINATED: Thread execution completed

### Thread-Local Variables

ThreadLocal provides thread-local variables that are local to each thread:

```java
public class AdvancedThreadLocalExample {
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = 
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    
    // Custom cleanup to prevent memory leaks
    public static void cleanup() {
        DATE_FORMAT.remove();
    }
    
    public String formatDate(Date date) {
        return DATE_FORMAT.get().format(date);
    }
}
```

### Virtual Threads (Project Loom - Java 19+)

Virtual threads provide lightweight concurrency:

```java
public class VirtualThreadExample {
    public void runVirtualThreadExample() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10000; i++) {
                executor.submit(() -> {
                    // Simulate blocking I/O
                    try {
                        Thread.sleep(1000);
                        System.out.println("Virtual thread completed: " + Thread.currentThread());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }
}
```

## Memory Visibility & Happens-Before Relationships

### The Java Memory Model (JMM)

The JMM defines rules for when changes made by one thread are visible to others. Key happens-before relationships:

- **Program Order Rule**: Each action in a thread happens-before every action that comes later in that thread
- **Monitor Lock Rule**: An unlock happens-before every subsequent lock on the same monitor
- **Volatile Variable Rule**: A write to a volatile field happens-before every subsequent read of that field
- **Thread Start Rule**: A call to Thread.start() happens-before any actions in the started thread
- **Thread Termination Rule**: Any action in a thread happens-before another thread detects that thread has terminated
- **Interrupt Rule**: A thread calling interrupt on another thread happens-before the interrupted thread detects the interrupt
- **Finalizer Rule**: An object's constructor completing happens-before its finalizer runs
- **Transitivity**: If A happens-before B, and B happens-before C, then A happens-before C

### Advanced Visibility Example

```java
public class VisibilityExample {
    private volatile boolean flag = false;
    private int data = 0;
    
    public void writer() {
        data = 42;           // 1
        flag = true;         // 2 - volatile write
    }
    
    public void reader() {
        if (flag) {          // 3 - volatile read
            int localData = data; // 4 - guaranteed to see 42
            System.out.println("Data: " + localData);
        }
    }
}
```

## Advanced Synchronization Techniques

### Custom Lock Implementations

```java
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class CustomMutex {
    private final Sync sync = new Sync();
    
    private static class Sync extends AbstractQueuedSynchronizer {
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
        
        protected boolean tryRelease(int releases) {
            assert releases == 1;
            if (getState() == 0) throw new IllegalMonitorStateException();
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
        
        protected boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }
    }
    
    public void lock() { sync.acquire(1); }
    public void unlock() { sync.release(1); }
    public boolean isLocked() { return sync.isHeldExclusively(); }
}
```

### Read-Write Locks with Stamped Locks

```java
import java.util.concurrent.locks.StampedLock;

public class StampedLockExample {
    private double x, y;
    private final StampedLock sl = new StampedLock();
    
    public double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead();
        double currentX = x, currentY = y;
        
        // Validate the optimistic read
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
    
    public void move(double deltaX, double deltaY) {
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }
}
```

### Condition Variables

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Queue;
import java.util.LinkedList;

public class BoundedBuffer<E> {
    final ReentrantLock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();
    final Queue<E> queue = new LinkedList<>();
    final int capacity;
    
    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }
    
    public void put(E item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.add(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public E take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            E item = queue.poll();
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
```

## Concurrent Data Structures

### Advanced ConcurrentHashMap Usage

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class AdvancedConcurrentHashMapExample {
    private final ConcurrentHashMap<String, LongAdder> counters = new ConcurrentHashMap<>();
    
    public void incrementCounter(String key) {
        counters.computeIfAbsent(key, k -> new LongAdder()).increment();
    }
    
    public long getCounter(String key) {
        LongAdder adder = counters.get(key);
        return adder != null ? adder.sum() : 0L;
    }
    
    // Parallel operations
    public void processAllCounters() {
        counters.forEach((key, value) -> {
            System.out.println(key + ": " + value.sum());
        });
        
        // Parallel computation
        long total = counters.reduceValuesAsLong(Long.MAX_VALUE, LongAdder::sum, Long::sum);
        System.out.println("Total: " + total);
    }
}
```

### Blocking Queues for Producer-Consumer

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerExample {
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
    private final AtomicInteger producerId = new AtomicInteger(0);
    private final AtomicInteger consumerId = new AtomicInteger(0);
    
    public void startProducerConsumer() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // Multiple producers
        for (int i = 0; i < 3; i++) {
            final int id = producerId.incrementAndGet();
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        String item = "Producer-" + id + "-Item-" + j;
                        queue.put(item); // Blocking put
                        System.out.println("Produced: " + item);
                        Thread.sleep(10); // Simulate work
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Multiple consumers
        for (int i = 0; i < 2; i++) {
            final int id = consumerId.incrementAndGet();
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        String item = queue.take(); // Blocking take
                        System.out.println("Consumer-" + id + " consumed: " + item);
                        Thread.sleep(15); // Simulate processing
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
    }
}
```

## Advanced Executors & Thread Pools

### Custom Thread Pool with Monitoring

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class AdvancedThreadPoolExample {
    private final AtomicLong taskCount = new AtomicLong(0);
    private final AtomicLong completedTasks = new AtomicLong(0);
    private final AtomicLong failedTasks = new AtomicLong(0);
    
    public ThreadPoolExecutor createMonitoredThreadPool() {
        return new ThreadPoolExecutor(
            4,                          // core pool size
            16,                         // max pool size
            60L,                        // keep alive time
            TimeUnit.SECONDS,           // time unit
            new LinkedBlockingQueue<>(100), // work queue
            new ThreadFactory() {
                private final AtomicLong threadNumber = new AtomicLong(0);
                
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "AdvancedPool-" + threadNumber.incrementAndGet());
                    t.setDaemon(false);
                    t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() { // Rejection policy
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                    System.out.println("Task rejected: " + r.toString());
                    super.rejectedExecution(r, e);
                }
            }
        ) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                System.out.println("Executing task by " + t.getName());
                taskCount.incrementAndGet();
            }
            
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                completedTasks.incrementAndGet();
                if (t != null) {
                    failedTasks.incrementAndGet();
                    System.out.println("Task failed: " + t.getMessage());
                }
            }
            
            @Override
            protected void terminated() {
                super.terminated();
                System.out.println("Thread pool terminated");
            }
        };
    }
    
    public void printStats() {
        System.out.println("Active threads: " + getActiveCount());
        System.out.println("Completed tasks: " + getCompletedTaskCount());
        System.out.println("Task count: " + getTaskCount());
        System.out.println("Queue size: " + getQueue().size());
    }
}
```

### Fork-Join Framework

```java
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinExample extends RecursiveTask<Long> {
    private final long[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 1000;
    
    public ForkJoinExample(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // Direct computation for small arrays
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            // Split the work
            int mid = (start + end) / 2;
            ForkJoinExample leftTask = new ForkJoinExample(array, start, mid);
            ForkJoinExample rightTask = new ForkJoinExample(array, mid, end);
            
            // Fork the left task and compute right task directly
            leftTask.fork();
            long rightResult = rightTask.compute();
            long leftResult = leftTask.join();
            
            return leftResult + rightResult;
        }
    }
    
    public static long parallelSum(long[] array) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinExample task = new ForkJoinExample(array, 0, array.length);
        return forkJoinPool.invoke(task);
    }
}
```

## Lock-Free Programming

### Atomic Operations & Compare-And-Swap

```java
import java.util.concurrent.atomic.*;
import java.util.function.IntBinaryOperator;

public class LockFreeExample {
    private final AtomicInteger atomicCounter = new AtomicInteger(0);
    private final AtomicReference<String> atomicReference = new AtomicReference<>();
    private final AtomicLongArray atomicLongArray = new AtomicLongArray(10);
    
    // Atomic operations with update functions
    public void advancedAtomicOperations() {
        // Update with function
        atomicCounter.updateAndGet(x -> x * 2 + 1);
        
        // Accumulate with function
        atomicCounter.accumulateAndGet(5, (current, update) -> current + update * 2);
        
        // Get and update
        int oldValue = atomicCounter.getAndAccumulate(3, Integer::sum);
        
        // Atomic reference with compare-and-set
        atomicReference.compareAndSet(null, "Initial Value");
        atomicReference.accumulateAndGet("New Value", String::concat);
    }
    
    // Lock-free stack implementation
    public static class LockFreeStack<T> {
        private final AtomicReference<Node<T>> head = new AtomicReference<>();
        
        private static class Node<T> {
            final T data;
            Node<T> next;
            
            Node(T data) {
                this.data = data;
            }
        }
        
        public void push(T item) {
            Node<T> newNode = new Node<>(item);
            Node<T> currentHead;
            do {
                currentHead = head.get();
                newNode.next = currentHead;
            } while (!head.compareAndSet(currentHead, newNode));
        }
        
        public T pop() {
            Node<T> currentHead;
            Node<T> newHead;
            do {
                currentHead = head.get();
                if (currentHead == null) {
                    return null;
                }
                newHead = currentHead.next;
            } while (!head.compareAndSet(currentHead, newHead));
            
            return currentHead.data;
        }
    }
}
```

### Advanced Atomic Operations

```java
import java.util.concurrent.atomic.*;

public class AdvancedAtomicOperations {
    private final LongAdder longAdder = new LongAdder();
    private final DoubleAdder doubleAdder = new DoubleAdder();
    private final Striped64.Striped64Cell[] cells; // Internal implementation
    
    public void demonstrateAdders() {
        // LongAdder - better performance under high contention
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    longAdder.add(1);
                    doubleAdder.add(1.0);
                }
            }).start();
        }
        
        // Wait for completion and get results
        // longAdder.sum() and doubleAdder.sum()
    }
    
    // AtomicIntegerFieldUpdater for existing objects
    public static class AtomicUpdateExample {
        private volatile int value = 0;
        private static final AtomicIntegerFieldUpdater<AtomicUpdateExample> 
            UPDATER = AtomicIntegerFieldUpdater.newUpdater(AtomicUpdateExample.class, "value");
        
        public boolean increment() {
            return UPDATER.compareAndSet(this, 0, 1);
        }
        
        public int getValue() {
            return UPDATER.get(this);
        }
    }
}
```

## Actor Model & Alternative Paradigms

### CompletableFuture Advanced Usage

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

public class CompletableFutureAdvanced {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    public CompletableFuture<String> complexAsyncWorkflow() {
        return CompletableFuture.supplyAsync(() -> {
            // Step 1: Initial computation
            System.out.println("Step 1: " + Thread.currentThread().getName());
            return "Initial Result";
        }, executor)
        .thenApply(result -> {
            // Step 2: Transform result
            System.out.println("Step 2: " + Thread.currentThread().getName());
            return result + " -> Transformed";
        })
        .thenCompose(transformed -> {
            // Step 3: Chain with another async operation
            System.out.println("Step 3: " + Thread.currentThread().getName());
            return CompletableFuture.supplyAsync(() -> transformed + " -> Chained", executor);
        })
        .handle((result, exception) -> {
            // Step 4: Handle both success and exception
            if (exception != null) {
                System.err.println("Error occurred: " + exception.getMessage());
                return "Error Result";
            }
            return result;
        })
        .whenComplete((result, exception) -> {
            // Step 5: Final completion callback
            System.out.println("Workflow completed: " + result);
        });
    }
    
    public CompletableFuture<List<String>> parallelProcessing() {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000); // Simulate work
                    return "Task " + taskId + " completed";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Task " + taskId + " interrupted";
                }
            }, executor);
            futures.add(future);
        }
        
        // Combine all futures
        CompletableFuture<Void> allDone = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        return allDone.thenApply(v -> {
            List<String> results = new ArrayList<>();
            for (CompletableFuture<String> f : futures) {
                results.add(f.join()); // Safe to join now
            }
            return results;
        });
    }
    
    public CompletableFuture<String> raceCondition() {
        // Return the first completed future
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
                return "Slow Result";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Interrupted";
            }
        });
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
                return "Fast Result";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Interrupted";
            }
        });
        
        return CompletableFuture.anyOf(future1, future2)
            .thenApply(result -> (String) result);
    }
}
```

## Performance Monitoring & Debugging

### Thread Dump Analysis

```java
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.ThreadInfo;

public class ThreadMonitoring {
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    
    public void analyzeThreads() {
        long[] threadIds = threadBean.getAllThreadIds();
        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadIds, true, true);
        
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo != null) {
                System.out.println("Thread: " + threadInfo.getThreadName());
                System.out.println("State: " + threadInfo.getThreadState());
                System.out.println("Blocked Time: " + threadInfo.getBlockedTime());
                System.out.println("Waited Time: " + threadInfo.getWaitedTime());
                
                // Print stack trace
                for (StackTraceElement element : threadInfo.getStackTrace()) {
                    System.out.println("  " + element);
                }
                System.out.println("---");
            }
        }
    }
    
    public void detectDeadlocks() {
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            ThreadInfo[] infos = threadBean.getThreadInfo(deadlockedThreads, true);
            System.out.println("DEADLOCK DETECTED!");
            for (ThreadInfo info : infos) {
                System.out.println("Deadlocked thread: " + info.getThreadName());
                for (StackTraceElement element : info.getStackTrace()) {
                    System.out.println("  " + element);
                }
            }
        }
    }
    
    public void monitorThreadContention() {
        if (threadBean.isThreadContentionMonitoringSupported()) {
            threadBean.setThreadContentionMonitoringEnabled(true);
            
            System.out.println("Current thread CPU time: " + 
                threadBean.getCurrentThreadCpuTime());
            System.out.println("Current thread user time: " + 
                threadBean.getCurrentThreadUserTime());
        }
    }
}
```

### Custom Performance Metrics

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;
import java.util.Map;

public class PerformanceMetrics {
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Gauge> gauges = new ConcurrentHashMap<>();
    
    public static class Timer {
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private final AtomicLong maxTime = new AtomicLong(0);
        
        public void record(long durationNanos) {
            count.incrementAndGet();
            totalTime.addAndGet(durationNanos);
            maxTime.accumulateAndGet(durationNanos, Math::max);
        }
        
        public double getAvgTime() {
            long cnt = count.get();
            return cnt > 0 ? (double) totalTime.get() / cnt : 0.0;
        }
        
        public long getMaxTime() {
            return maxTime.get();
        }
        
        public long getCount() {
            return count.get();
        }
    }
    
    public static class Counter {
        private final AtomicLong value = new AtomicLong(0);
        
        public void increment() { value.incrementAndGet(); }
        public void add(long delta) { value.addAndGet(delta); }
        public long getValue() { return value.get(); }
    }
    
    public interface Gauge {
        long getValue();
    }
    
    public void time(String name, Runnable operation) {
        Timer timer = timers.computeIfAbsent(name, k -> new Timer());
        long start = System.nanoTime();
        try {
            operation.run();
        } finally {
            timer.record(System.nanoTime() - start);
        }
    }
    
    public <T> T time(String name, java.util.function.Supplier<T> operation) {
        Timer timer = timers.computeIfAbsent(name, k -> new Timer());
        long start = System.nanoTime();
        try {
            return operation.get();
        } finally {
            timer.record(System.nanoTime() - start);
        }
    }
    
    public void incrementCounter(String name) {
        counters.computeIfAbsent(name, k -> new Counter()).increment();
    }
    
    public void registerGauge(String name, Gauge gauge) {
        gauges.put(name, gauge);
    }
    
    public void printMetrics() {
        System.out.println("=== Performance Metrics ===");
        
        for (Map.Entry<String, Timer> entry : timers.entrySet()) {
            Timer timer = entry.getValue();
            System.out.printf("%s: Count=%d, Avg=%.2fμs, Max=%.2fμs%n", 
                entry.getKey(),
                timer.getCount(),
                timer.getAvgTime() / 1000.0,
                timer.getMaxTime() / 1000.0);
        }
        
        for (Map.Entry<String, Counter> entry : counters.entrySet()) {
            System.out.printf("%s: %d%n", entry.getKey(), entry.getValue().getValue());
        }
        
        for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
            System.out.printf("%s: %d%n", entry.getKey(), entry.getValue().getValue());
        }
    }
}
```

## Microservices Concurrency Patterns

### Distributed Locks with Redis

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DistributedLock {
    private final Jedis jedis;
    private final String lockKey;
    private final String lockValue;
    private final long lockTimeoutMs;
    
    public DistributedLock(Jedis jedis, String lockKey, long timeoutMs) {
        this.jedis = jedis;
        this.lockKey = lockKey;
        this.lockValue = UUID.randomUUID().toString();
        this.lockTimeoutMs = timeoutMs;
    }
    
    public boolean acquire() {
        SetParams params = SetParams.setParams()
            .nx()  // Only set if key doesn't exist
            .px(lockTimeoutMs); // Set expiration
        
        String result = jedis.set(lockKey, lockValue, params);
        return "OK".equals(result);
    }
    
    public void release() {
        // Lua script to ensure atomicity
        String luaScript = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end";
        
        jedis.eval(luaScript, 1, lockKey, lockValue);
    }
    
    public boolean isLocked() {
        return jedis.get(lockKey) != null;
    }
}
```

### Circuit Breaker Pattern

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }
    
    private final int failureThreshold;
    private final long timeoutMs;
    private final long retryTimeoutMs;
    
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private volatile State state = State.CLOSED;
    private final AtomicInteger successCount = new AtomicInteger(0);
    
    public CircuitBreaker(int failureThreshold, long timeoutMs, long retryTimeoutMs) {
        this.failureThreshold = failureThreshold;
        this.timeoutMs = timeoutMs;
        this.retryTimeoutMs = retryTimeoutMs;
    }
    
    public <T> T execute(java.util.function.Supplier<T> operation) throws Exception {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime.get() > retryTimeoutMs) {
                state = State.HALF_OPEN;
            } else {
                throw new CircuitBreakerOpenException("Circuit breaker is OPEN");
            }
        }
        
        try {
            T result = operation.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
    
    private void onSuccess() {
        failureCount.set(0);
        successCount.incrementAndGet();
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
        }
    }
    
    private void onFailure() {
        failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        if (failureCount.get() >= failureThreshold && state != State.OPEN) {
            state = State.OPEN;
        }
    }
    
    public State getState() {
        return state;
    }
    
    public static class CircuitBreakerOpenException extends Exception {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
}
```

## Advanced Code Examples

### Reactive Programming with CompletableFuture

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

public class ReactiveConcurrencyExample {
    public class UserService {
        public CompletableFuture<User> getUser(String userId) {
            return CompletableFuture.supplyAsync(() -> {
                // Simulate database call
                try {
                    Thread.sleep(100);
                    return new User(userId, "User " + userId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });
        }
        
        public CompletableFuture<List<User>> getUsers(List<String> userIds) {
            List<CompletableFuture<User>> futures = userIds.stream()
                .map(this::getUser)
                .collect(ArrayList::new, 
                        (list, future) -> list.add(future.join()), 
                        (list1, list2) -> list1.addAll(list2));
            
            CompletableFuture<Void> allDone = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            return allDone.thenApply(v -> 
                futures.stream()
                    .map(CompletableFuture::join)
                    .collect(ArrayList::new, 
                            (list, item) -> list.add(item), 
                            (list1, list2) -> list1.addAll(list2))
            );
        }
    }
    
    public class User {
        private final String id;
        private final String name;
        
        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        // getters
        public String getId() { return id; }
        public String getName() { return name; }
    }
    
    // Event-driven processing
    public class EventProcessor {
        private final List<CompletableFuture<Void>> pendingTasks = new ArrayList<>();
        
        public void processEvent(Event event) {
            CompletableFuture<Void> task = CompletableFuture
                .supplyAsync(() -> validateEvent(event))
                .thenApply(this::transformEvent)
                .thenAccept(this::saveToDatabase)
                .exceptionally(throwable -> {
                    handleError(event, throwable);
                    return null;
                });
            
            pendingTasks.add(task);
        }
        
        public CompletableFuture<Void> waitForAllEvents() {
            return CompletableFuture.allOf(
                pendingTasks.toArray(new CompletableFuture[0])
            );
        }
        
        private ValidatedEvent validateEvent(Event event) {
            // validation logic
            return new ValidatedEvent(event);
        }
        
        private ProcessedEvent transformEvent(ValidatedEvent event) {
            // transformation logic
            return new ProcessedEvent(event);
        }
        
        private void saveToDatabase(ProcessedEvent event) {
            // save logic
        }
        
        private void handleError(Event event, Throwable throwable) {
            System.err.println("Error processing event: " + event + ", error: " + throwable.getMessage());
        }
    }
    
    // Placeholder classes for the example
    public static class Event {}
    public static class ValidatedEvent { public ValidatedEvent(Event event) {} }
    public static class ProcessedEvent { public ProcessedEvent(ValidatedEvent event) {} }
}
```

### Advanced Thread Pool Configuration

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AdvancedThreadPoolManager {
    private final ThreadPoolExecutor[] executors;
    private final String[] executorNames;
    
    public AdvancedThreadPoolManager() {
        this.executorNames = new String[]{"IO_BOUND", "CPU_BOUND", "SCHEDULED", "CACHED"};
        this.executors = new ThreadPoolExecutor[executorNames.length];
        
        // IO-bound executor - many threads for blocking operations
        executors[0] = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 4,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            createThreadFactory("IO-Thread"),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        // CPU-bound executor - limited threads to avoid context switching
        executors[1] = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            createThreadFactory("CPU-Thread"),
            new ThreadPoolExecutor.AbortPolicy()
        );
        
        // Scheduled executor for periodic tasks
        executors[2] = new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            createThreadFactory("Scheduled-Thread"),
            new ThreadPoolExecutor.DiscardPolicy()
        );
        
        // Cached executor for short-lived tasks
        executors[3] = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            createThreadFactory("Cached-Thread"),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
    
    private ThreadFactory createThreadFactory(String prefix) {
        return new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            private final AtomicLong createdCount = new AtomicLong(0);
            
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, prefix + "-" + threadNumber.getAndIncrement());
                createdCount.incrementAndGet();
                t.setDaemon(false);
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        };
    }
    
    public ThreadPoolExecutor getExecutor(String type) {
        for (int i = 0; i < executorNames.length; i++) {
            if (executorNames[i].equals(type)) {
                return executors[i];
            }
        }
        return executors[0]; // default to IO-bound
    }
    
    public void shutdownAll() {
        for (ThreadPoolExecutor executor : executors) {
            executor.shutdown();
        }
        
        // Wait for graceful shutdown
        for (ThreadPoolExecutor executor : executors) {
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public void printExecutorStats() {
        for (int i = 0; i < executorNames.length; i++) {
            ThreadPoolExecutor executor = executors[i];
            System.out.printf("Executor %s: Active=%d, Completed=%d, TaskCount=%d, QueueSize=%d%n",
                executorNames[i],
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount(),
                executor.getQueue().size()
            );
        }
    }
}
```

## Monitoring Tools for Microservices

### JVM Monitoring Setup

```java
import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

public class JVMMonitoring {
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    
    public void collectJVMStats() {
        // Memory usage
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        System.out.println("Heap Memory: Used=" + heapUsage.getUsed() / (1024 * 1024) + "MB, " +
                          "Committed=" + heapUsage.getCommitted() / (1024 * 1024) + "MB, " +
                          "Max=" + heapUsage.getMax() / (1024 * 1024) + "MB");
        
        System.out.println("Non-Heap Memory: Used=" + nonHeapUsage.getUsed() / (1024 * 1024) + "MB");
        
        // GC statistics
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            System.out.println("GC " + gcBean.getName() + 
                             ": Collections=" + gcBean.getCollectionCount() + 
                             ", Time=" + gcBean.getCollectionTime() + "ms");
        }
        
        // OS information
        System.out.println("Process CPU Load: " + osBean.getProcessCpuLoad());
        System.out.println("System CPU Load: " + osBean.getSystemCpuLoad());
        System.out.println("Available Processors: " + osBean.getAvailableProcessors());
    }
    
    // JMX-based monitoring setup
    public void setupJMXMonitoring() {
        // This would typically involve setting up JMX connectors
        // and exposing MBeans for external monitoring tools
        System.setProperty("com.sun.management.jmxremote", "true");
        System.setProperty("com.sun.management.jmxremote.port", "9999");
        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
        System.setProperty("com.sun.management.jmxremote.ssl", "false");
    }
}
```

### Application-Level Metrics Collection

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;

public class MicrometerMetricsExample {
    private final MeterRegistry registry;
    private final Timer requestTimer;
    private final Counter errorCounter;
    
    public MicrometerMetricsExample(MeterRegistry registry) {
        this.registry = registry;
        this.requestTimer = Timer.builder("http.requests")
            .description("HTTP request duration")
            .register(registry);
        this.errorCounter = Counter.builder("http.errors")
            .description("HTTP error count")
            .register(registry);
        
        // Register standard JVM metrics
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
    }
    
    public <T> T timeRequest(String endpoint, java.util.function.Supplier<T> operation) {
        return requestTimer.recordCallable(() -> {
            try {
                return operation.get();
            } catch (Exception e) {
                errorCounter.increment();
                throw e;
            }
        });
    }
    
    public void recordCustomGauge(String name, Object obj, java.util.function.ToDoubleFunction<Object> valueFunction) {
        Gauge.builder(name)
            .register(registry, obj, valueFunction);
    }
}
```

### Distributed Tracing Integration

```java
import brave.Tracing;
import brave.Span;
import brave.propagation.TraceContext;
import brave.http.HttpTracing;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class DistributedTracingExample {
    private final Tracing tracing;
    
    public DistributedTracingExample(String serviceName, String zipkinUrl) {
        this.tracing = Tracing.newBuilder()
            .localServiceName(serviceName)
            .spanReporter(AsyncReporter.create(OkHttpSender.create(zipkinUrl)))
            .build();
    }
    
    public <T> T tracedOperation(String operationName, java.util.function.Supplier<T> operation) {
        Span span = tracing.tracer().nextSpan().name(operationName).start();
        
        try (Tracing.SpanInScope ws = tracing.tracer().withSpanInScope(span)) {
            return operation.get();
        } catch (Exception e) {
            span.tag("error", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
    
    public void tracedAsyncOperation(String operationName, Runnable operation) {
        Span span = tracing.tracer().nextSpan().name(operationName).start();
        
        // In a real scenario, you'd need to properly propagate the context
        // to the async execution
        new Thread(() -> {
            try (Tracing.SpanInScope ws = tracing.tracer().withSpanInScope(span)) {
                operation.run();
            } finally {
                span.end();
            }
        }).start();
    }
}
```

## Conclusion

Java concurrency is a vast and complex topic that requires deep understanding of the underlying JVM mechanics, memory model, and performance characteristics. This guide covers the most advanced patterns and techniques, but real-world applications often require combining multiple approaches and continuously monitoring performance.

Key takeaways:
- Always consider the trade-offs between different concurrency approaches
- Monitor your applications in production to identify bottlenecks
- Use appropriate tools for debugging and performance analysis
- Consider the impact of microservice architecture on concurrency patterns
- Test under realistic load conditions

Remember that with great concurrency power comes great responsibility for correctness and performance!