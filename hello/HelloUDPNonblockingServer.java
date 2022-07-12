package info.kgeorgiy.ja.smaglii.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPNonblockingServer implements HelloServer {
    private ExecutorService sender;
    private DatagramChannel channel;
    private Selector selector;
    private Queue<Response> responses;

    @Override
    public void start(int port, int threads) {
        sender = Executors.newFixedThreadPool(threads + 1);
        responses = new ConcurrentLinkedDeque<>();

        try {
            selector = Selector.open();

            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            final int size = channel.socket().getReceiveBufferSize();
            channel.register(selector, SelectionKey.OP_READ);
            channel.bind(new InetSocketAddress(port));

            Executors.newSingleThreadExecutor().submit(() -> {
                while (!channel.socket().isClosed() && !Thread.interrupted()) {
                    try {
                        if (selector.select() != 0) {
                            for (final Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                                final SelectionKey key = i.next();
                                try {
                                    if (key.isValid()) {
                                        if (key.isWritable()) {
                                            if (!responses.isEmpty()) {
                                                final Response response = responses.poll();
                                                try {
                                                    channel.send(
                                                            ByteBuffer.wrap(response.message.getBytes(StandardCharsets.UTF_8)),
                                                            response.address
                                                    );
                                                } catch (final IOException e) {
                                                    System.err.println("Cannot write message: " + e.getMessage());
                                                }
                                                key.interestOpsOr(SelectionKey.OP_READ);
                                            } else {
                                                key.interestOps(SelectionKey.OP_READ);
                                            }
                                        } else if (key.isReadable()) {
                                            try {
                                                final ByteBuffer buffer = ByteBuffer.allocate(size);
                                                final SocketAddress address = channel.receive(buffer);
                                                sender.submit(() -> {
                                                    buffer.flip();
                                                    responses.add(new Response(
                                                            HelloUtils.HELLO + StandardCharsets.UTF_8.decode(buffer),
                                                            address
                                                    ));

                                                    key.interestOps(SelectionKey.OP_WRITE);
                                                    selector.wakeup();
                                                });
                                            } catch (IOException e) {
                                                System.err.println("Cannot read message: " + e.getMessage());
                                            }
                                        }
                                    }
                                } finally {
                                    i.remove();
                                }
                            }
                        }
                    } catch (final ClosedSelectorException e) {
                        System.err.println("Selector was closed: " + e.getMessage());
                    } catch (final IOException e) {
                        System.err.println(e.getMessage());
                        close();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            responses.clear();
            selector.close();
            channel.close();
            if (!HelloUtils.closePool(sender, HelloUtils.TERMINATION_TIMEOUT)) {
                Thread.currentThread().interrupt();
            }
        } catch (final IOException e) {
            System.err.println("Cannot close resource: " + e.getMessage());
        }
    }

    private record Response(String message, SocketAddress address) {
    }


}
