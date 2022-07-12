package info.kgeorgiy.ja.smaglii.concurrent;

import info.kgeorgiy.java.advanced.concurrent.AdvancedIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements AdvancedIP {

    private final ParallelMapper parallelMapper;

    public IterativeParallelism() {
        parallelMapper = null;
    }

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }


    @Override
    public <T> T reduce(int threads, List<T> values, Monoid<T> monoid) throws InterruptedException {
        final Function<Stream<? extends T>, ? extends T> fun =
                s -> s.reduce(monoid.getIdentity(), monoid.getOperator(), monoid.getOperator());
        return apply(threads, values, fun, fun);
    }

    @Override
    public <T, R> R mapReduce(int threads, List<T> values, Function<T, R> lift, Monoid<R> monoid) throws InterruptedException {
        return reduce(threads, map(threads, values, lift), monoid);
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return apply(threads,
                values,
                s -> s.map(Object::toString).collect(Collectors.joining()),
                s -> s.collect(Collectors.joining())
        );
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return apply(threads,
                values,
                s -> s.filter(predicate).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList())
        );
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f) throws InterruptedException {
        return apply(threads,
                values,
                s -> s.map(f).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList())
        );
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return minimum(threads, values, comparator.reversed());
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return apply(threads,
                values,
                s -> s.min(comparator).orElse(null),
                s -> s.min(comparator).orElse(null)
        );
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return apply(threads,
                values,
                s -> s.allMatch(predicate),
                s -> s.allMatch(Boolean::booleanValue)
        );
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }

    private <T, U> List<U> classicParallel(final List<Stream<? extends T>> splits,
                                           final Function<Stream<? extends T>, ? extends U> function
    ) throws InterruptedException {
        final List<Thread> listOfThreads = new ArrayList<>();
        final List<U> result = new ArrayList<>(Collections.nCopies(splits.size(), null));
        for (int i = 0; i < splits.size(); i++) {
            final int tmp = i;
            listOfThreads.add(new Thread(() -> result.set(tmp, function.apply(splits.get(tmp)))));
            listOfThreads.get(listOfThreads.size() - 1).start();
        }

        InterruptedException interruptedException = null;

        for (final Thread thread : listOfThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                if (interruptedException == null) {
                    interruptedException = new InterruptedException("Tread interrupted during work");
                }
                interruptedException.addSuppressed(e);
            }
        }

        if (interruptedException != null) {
            throw interruptedException;
        }
        return result;
    }



    private <T, U> U apply(final int threads,
                           final List<T> values,
                           final Function<Stream<? extends T>, ? extends U> function,
                           final Function<Stream<? extends U>, ? extends U> merge
    ) throws InterruptedException {
        final List<Stream<? extends T>> splits = new ArrayList<>();
        int cur_step = values.size() / threads + 1;
        int counter = values.size() % threads;
        for (int i = 0; i < values.size(); i += cur_step) {
            if (counter == 0) {
                cur_step--;
            }
            splits.add(values.subList(i, i + cur_step).stream());
            counter--;
        }
        var result = parallelMapper == null ? classicParallel(splits, function)
                : parallelMapper.map(function, splits);
        return merge.apply(result.stream());
    }

}
