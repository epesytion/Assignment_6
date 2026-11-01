package sim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// Runs producers on an ExecutorService and shuts it down gracefully.
// Graceful = stop accepting tasks, let every started task finish, wait, then check for failures.
// Because delivery is synchronous, when this method returns every event has reached its listeners.
public class ProducerPool {
    private final ExecutorService executor;
    private final long timeoutSeconds;

    public ProducerPool(ExecutorService executor, long timeoutSeconds) {
        this.executor = executor;
        this.timeoutSeconds = timeoutSeconds;
    }

    public void runAndShutdown(List<? extends Runnable> producers) throws InterruptedException {
        List<Future<?>> futures = new ArrayList<>();
        for (Runnable producer : producers) {
            futures.add(executor.submit(producer));
        }
        executor.shutdown();
        awaitOrFail();
        checkFailures(futures);
    }

    public boolean isTerminated() {
        return executor.isTerminated();
    }

    private void awaitOrFail() throws InterruptedException {
        try {
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                throw new IllegalStateException("producers did not finish in " + timeoutSeconds + " seconds");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            throw e;
        }
    }

    private void checkFailures(List<Future<?>> futures) throws InterruptedException {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new IllegalStateException("a producer failed", e.getCause());
            }
        }
    }
}
