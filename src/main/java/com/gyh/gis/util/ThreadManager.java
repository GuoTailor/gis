package com.gyh.gis.util;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.*;

/**
 * Created by GYH on 2018/3/24.
 * 线程池工具
 */
public class ThreadManager {
    public static final int parallelism = Runtime.getRuntime().availableProcessors() * 2;
    private final ExecutorService executorService;
    private static int poolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 私有化构造函数，使用单列模式
     */
    private ThreadManager() {
        this(poolSize, new LinkedBlockingQueue<>(1024));
    }

    private ThreadManager(BlockingQueue<Runnable> workQueue) {
        this(poolSize, workQueue);
    }

    private ThreadManager(int size, BlockingQueue<Runnable> workQueue) {
        if (size < 8) {
            size = 8;
        }
        executorService = new ThreadPoolExecutor(size, size, 0L,
                TimeUnit.MINUTES, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

    /**
     * 初始化线程池的大小，默认为4，注意：初始化须在使用之前调用，否则无效
     *
     * @param poolSize 线程池的大小
     */
    public static void init(int poolSize) {
        ThreadManager.poolSize = poolSize;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    private static final class InstanceHolder {
        static final ThreadManager workPool = new ThreadManager();
        static final Scheduler scheduler = Schedulers.boundedElastic();
    }

    public static Scheduler getScheduler() {
        return InstanceHolder.scheduler;
    }

    /**
     * 获取一个工作线程池实列
     *
     * @return {@link ThreadManager} 的一个实列
     */
    public static ThreadManager getWorkPool() {
        return InstanceHolder.workPool;
    }

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
     * <p>
     * <p>
     * If you would like to immediately block waiting
     * for a task, you can use constructions of the form
     * {@code result = exec.submit(aCallable).get();}
     *
     * <p>Note: The {@link Executors} class includes a set of methods
     * that can convert some other common closure-like objects,
     * for example, {@link java.security.PrivilegedAction} to
     * {@link Callable} form so they can be submitted.
     *
     * @param task the task to submit
     * @param <T>  the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     */
    public void execute(Runnable command) {
        executorService.execute(command);
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     */
    public void shutdown() {
        executorService.shutdown();
    }

}
