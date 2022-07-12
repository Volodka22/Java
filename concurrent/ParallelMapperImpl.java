package info.kgeorgiy.ja.smaglii.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threadList;
    private final ThreadQueue queue;

    private static class Counter {
        private int counter = 0;
        private final int endCount;

        public Counter(final int endCount) {
            this.endCount = endCount;
        }

        public synchronized void increment() {
            if (++counter >= endCount) {
                notify();
            }
        }

        public synchronized void waitEnd() throws InterruptedException {
            while (counter < endCount){
                wait();
            }
        }
    }

    private static class ThreadQueue {

        private final Queue<Runnable> data = new ArrayDeque<>();
        private final int maxCounter;

        public ThreadQueue(int maxCounter) {
            this.maxCounter = maxCounter;
        }

        public synchronized void set(Runnable data) throws InterruptedException {
            while (this.data.size() == maxCounter) {
                wait();
            }
            this.data.add(data);
            notifyAll();
        }

        public synchronized Runnable get() throws InterruptedException {
            while (this.data.isEmpty()) {
                wait();
            }
            Runnable d = data.poll();
            notifyAll();
            return d;
        }
    }

    /**
     *
     * @param threads count of treads in ParallelMapper
     */
    public ParallelMapperImpl(final int threads) {
        queue = new ThreadQueue(threads * 3);
        threadList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    queue.get().run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        for (int i = 0; i < threads; i++) {
            threadList.add(new Thread(runnable));
            threadList.get(threadList.size() - 1).start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        final Counter counter = new Counter(args.size());
        final List<R> ans = new ArrayList<>(Collections.nCopies(args.size(), null));
        for (int i = 0; i < args.size(); i++) {
            final int finalI = i;
            queue.set(() -> {
                ans.set(finalI, f.apply(args.get(finalI)));
                counter.increment();
            });
        }
        counter.waitEnd();
        return ans;
    }

    @Override
    public void close() {
        for (Thread i : threadList) {
            i.interrupt();
            try {
                i.join();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
