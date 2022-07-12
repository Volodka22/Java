package info.kgeorgiy.ja.smaglii.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class WebCrawler implements AdvancedCrawler {

    private static final int DEFAULT_DEPTH = 1;
    private static final int DEFAULT_DOWNLOADERS = 10;
    private static final int DEFAULT_PERHOST = 10;
    private static final int DEFAULT_EXTRACTORS = 10;

    private final int perHost;
    private final Set<String> cached = ConcurrentHashMap.newKeySet();
    private final Phaser phaser = new Phaser(1);
    private final Downloader downloader;
    private final ExecutorService downloadService;
    private final ExecutorService extractorService;

    private final Map<String, IOException> errors = new ConcurrentHashMap<>();
    private final Set<String> downloaded = ConcurrentHashMap.newKeySet();
    private final Map<String, HostTaskController> hostMap = new ConcurrentHashMap<>();


    private Set<String> blockedHosts;


    public WebCrawler(final Downloader downloader, final int downloaders, final int extractors, final int perHost) {
        this.downloader = downloader;
        this.perHost = perHost;
        this.downloadService = Executors.newFixedThreadPool(downloaders);
        this.extractorService = Executors.newFixedThreadPool(extractors);
    }

    @Override
    public Result download(String url, int depth) {
        return download(url, depth, Collections.emptyList());
    }

    @Override
    public Result download(String url, int depth, List<String> hosts) {
        blockedHosts = ConcurrentHashMap.newKeySet();
        blockedHosts.addAll(hosts);
        cached.add(url);
        final Set<String> prevQueue = ConcurrentHashMap.newKeySet();
        prevQueue.add(url);
        final Queue<String> curQueue = new ConcurrentLinkedDeque<>();
        IntStream.range(0, depth).forEach(i -> {
            for (final String curUrl : prevQueue) {
                try {
                    addHostTask(curUrl, curQueue, i + 1 != depth);
                } catch (MalformedURLException e) {
                    errors.put(curUrl, e);
                }
            }
            phaser.arriveAndAwaitAdvance();
            prevQueue.clear();
            prevQueue.addAll(curQueue);
            curQueue.clear();
        });
        return new Result(new ArrayList<>(downloaded), errors);
    }

    @Override
    public void close() {
        if (!(shutdown(downloadService) && shutdown(extractorService))) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean shutdown(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            return false;
        }
        return true;
    }

    private boolean isNoVisited(String link) {
        return !cached.contains(link) && !blockedHosts.contains(link);
    }

    private void addHostTask(final String url, final Queue<String> queue, final boolean needLinks) throws MalformedURLException {
        final HostTaskController hostsWorker = hostMap.computeIfAbsent(URLUtils.getHost(url),
                name -> new HostTaskController(perHost, downloadService));
        phaser.register();
        hostsWorker.add(() -> {
            try {
                final Document doc = downloader.download(url);
                downloaded.add(url);
                if (needLinks) {
                    phaser.register();
                    extractorService.submit(() -> {
                        try {
                            doc.extractLinks().stream().filter(this::isNoVisited).forEach(link -> {
                                queue.add(link);
                                cached.add(link);
                            });
                        } catch (final IOException e) {
                            System.err.println("Extracting was failed. URL: " + url);
                        } finally {
                            phaser.arriveAndDeregister();
                        }
                    });
                }
            } catch (final IOException e) {
                errors.put(url, e);
            } finally {
                phaser.arriveAndDeregister();
                hostsWorker.run();
            }
        });
    }


    private final static class HostTaskController {
        private final Semaphore semaphore;
        private final ExecutorService service;
        private final Queue<Runnable> waitingQueue = new ConcurrentLinkedDeque<>();

        public HostTaskController(final int perHost, final ExecutorService service) {
            this.semaphore = new Semaphore(perHost);
            this.service = service;
        }

        private void add(final Runnable task) {
            if (semaphore.tryAcquire()) {
                service.submit(task);
            } else {
                waitingQueue.add(task);
            }
        }

        private void run() {
            final Runnable task = waitingQueue.poll();
            if (task == null) {
                semaphore.release();
            } else {
                service.submit(task);
            }
        }
    }

    private static int getArg(final String[] args, final int i, final int defaultVal) {
        if (args.length > i && args[i] != null) {
            return Integer.parseInt(args[i]);
        }
        return defaultVal;
    }

    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 5) {
            System.out.println("number of arguments: should be from 1 to 5");
            return;
        }
        for (String arg : args) {
            Objects.requireNonNull(arg);
        }
        try {
            int depth = getArg(args, 1, DEFAULT_DEPTH);
            int downloaders = getArg(args, 2, DEFAULT_DOWNLOADERS);
            int extractors = getArg(args, 3, DEFAULT_EXTRACTORS);
            int perHost = getArg(args, 4, DEFAULT_PERHOST);
            try (Crawler crawler = new WebCrawler(new CachingDownloader(), downloaders, extractors, perHost)) {
                crawler.download(args[0], depth);
            } catch (IOException e) {
                System.out.println("Can't create CashingDownloader");
            }
        } catch (NumberFormatException e) {
            System.out.println("Incorrect arguments type");
        }
    }

}
