package info.kgeorgiy.ja.smaglii.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class HelloUDPClient implements HelloClient {

    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.err.println("Expected exactly five arguments");
            return;
        }

        for (String arg : args) {
            if (arg == null) {
                System.out.println("Arguments can't be null");
                return;
            }
        }

        try {
            final int port = Integer.parseInt(args[1]);
            final int threads = Integer.parseInt(args[3]);
            final int requests = Integer.parseInt(args[4]);
            new HelloUDPClient().run(args[0], port, args[2], threads, requests);
        } catch (NumberFormatException e) {
            System.err.println("Expected five integers: " + e.getMessage());
        }
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        final InetSocketAddress address = new InetSocketAddress(host, port);
        IntStream.range(0, threads).forEach(numberOfThread -> pool.submit(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(HelloUtils.SOCKET_TIMEOUT);
                final int size = socket.getSendBufferSize();
                final DatagramPacket response = new DatagramPacket(new byte[size], size, address);
                final DatagramPacket receiver = new DatagramPacket(new byte[size], size);
                for (int numberOfRequest = 0; numberOfRequest < requests; numberOfRequest++) {
                    final String message = String.format("%s%d_%d", prefix, numberOfThread, numberOfRequest);
                    while (true) {
                        response.setData(message.getBytes(StandardCharsets.UTF_8));
                        try {
                            socket.send(response);
                        } catch (final IOException e) {
                            System.err.println("Cannot send packet: " + e.getMessage());
                            if (socket.isClosed()) {
                                return;
                            }
                        }

                        try {
                            socket.receive(receiver);
                        } catch (IOException e) {
                            System.err.println("Cannot receiver packet: " + e.getMessage());
                            if (socket.isClosed()) {
                                return;
                            }
                        }

                        final String answer = HelloUtils.getMessage(receiver);
                        if (HelloUtils.verify(answer, numberOfThread, numberOfRequest)) {
                            // System.out.println(answer);
                            break;
                        }
                    }

                }
            } catch (final SocketException e) {
                System.err.println("Cannot open port: " + e.getMessage());
            }
        }));
        HelloUtils.closePool(pool, threads * requests * HelloUtils.TERMINATION_TIMEOUT);
    }
}
