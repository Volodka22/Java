package info.kgeorgiy.ja.smaglii.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HelloUDPNonblockingClient implements HelloClient {

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        final List<DatagramChannel> channels = new ArrayList<>();
        final SocketAddress socketAddress = new InetSocketAddress(host, port);
        final ByteBuffer[] buffers = new ByteBuffer[threads];
        final int[] request_counters = new int[threads];
        try (final Selector selector = Selector.open()) {
            for (int id = 0; id < threads; id++) {
                final DatagramChannel channel = DatagramChannel.open();
                buffers[id] = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
                channel.configureBlocking(false);
                channel.connect(socketAddress);
                channel.register(selector, SelectionKey.OP_READ, id);
                channels.add(channel);
            }

            while (!Thread.interrupted() && !selector.keys().isEmpty()) {
                final Set<SelectionKey> selectedKeys = selector.selectedKeys();
                selector.select(HelloUtils.SOCKET_TIMEOUT);
                if (selectedKeys.isEmpty()) {
                    for (final SelectionKey key : selector.keys()) {
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }

                for (final Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final SelectionKey key = i.next();
                    try {
                        if (key.isValid()) {
                            final DatagramChannel channel = (DatagramChannel) key.channel();
                            final int id = (int) key.attachment();
                            if (key.isReadable()) {
                                final ByteBuffer buffer = buffers[id];
                                buffer.clear();
                                channel.receive(buffer);
                                buffer.flip();

                                final String answer = HelloUtils.getMessage(buffer);
                                if (HelloUtils.verify(answer, id, request_counters[id])) {
                                    request_counters[id]++;
                                    // System.out.println(answer);
                                }
                                key.interestOps(SelectionKey.OP_WRITE);

                                if (request_counters[id] == requests) {
                                    channel.close();
                                }

                            } else if (key.isWritable()) {
                                channel.send(ByteBuffer.wrap(
                                        String.format("%s%d_%d", prefix, id, request_counters[id]).getBytes(StandardCharsets.UTF_8)),
                                        socketAddress
                                );
                                key.interestOps(SelectionKey.OP_READ);
                            }
                        }
                    } finally {
                        i.remove();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Exception exception = null;
            for (DatagramChannel channel : channels) {
                try {
                    channel.close();
                } catch (final IOException e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                System.err.println("Exception while closing channel(s): " + exception.getMessage());
            }
        }
    }
}
