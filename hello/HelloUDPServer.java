package info.kgeorgiy.ja.smaglii.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer implements HelloServer {

    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.out.println("Expected exactly two arguments");
        } else {
            try (final HelloUDPServer server = new HelloUDPServer()) {
                final int numberOfPort = Integer.parseInt(args[0]);
                final int countOfThread = Integer.parseInt(args[1]);
                server.start(numberOfPort, countOfThread);
            } catch (final NumberFormatException e) {
                System.err.println("Incorrect arguments: expected two integers:" + e.getMessage());
            }
        }
    }

    // :NOTE: утечка ресурсов при повторных вызовах
    @Override
    public void start(int port, int threads) {
        try {
            socket = new DatagramSocket(port);
        } catch (final SocketException e) {
            System.err.println("Cannot create socket");
            return;
        }

        receiver = Executors.newSingleThreadExecutor();
        sender = Executors.newFixedThreadPool(threads);

        receiver.submit(() -> {
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    final int size = socket.getReceiveBufferSize();
                    final DatagramPacket packet = new DatagramPacket(new byte[size], size);
                    socket.receive(packet);
                    // :NOTE: Несмотря на то, что текущий способ получения ответа по запросу очень прост,
                    // сервер должен быть рассчитан на ситуацию,
                    // когда этот процесс может требовать много ресурсов и времени.
                    sender.submit(() -> {
                        final byte[] bytes = (HelloUtils.HELLO + HelloUtils.getMessage(packet)).getBytes(StandardCharsets.UTF_8);
                        packet.setData(bytes);
                        try {
                            socket.send(packet);
                        } catch (final IOException e) {
                            System.err.println("Message don't send: " + e.getMessage());
                        }
                    });
                } catch (final IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Trouble with socket: " + e.getMessage());
                    }
                }
            }
        });

    }

    @Override
    public void close() {
        socket.close();
        if (!(HelloUtils.closePool(sender, HelloUtils.TERMINATION_TIMEOUT) |
                HelloUtils.closePool(receiver, HelloUtils.TERMINATION_TIMEOUT))) {
            Thread.currentThread().interrupt();
        }
    }

    private ExecutorService receiver;
    private ExecutorService sender;
    private DatagramSocket socket;
}
