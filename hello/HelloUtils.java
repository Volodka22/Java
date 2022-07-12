package info.kgeorgiy.ja.smaglii.hello;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class HelloUtils {

    final public static int TERMINATION_TIMEOUT = 60;
    final public static int SOCKET_TIMEOUT = 39;
    final public static String HELLO = "Hello, ";

    public static String getMessage(final DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    public static String getMessage(final ByteBuffer buffer) {
        return new String(buffer.array(), buffer.arrayOffset(), buffer.limit(), StandardCharsets.UTF_8);
    }

    public static boolean closePool(final ExecutorService pool, final int timeout) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            return false;
        }
        return true;
    }

    private static int checkNumber(final String data, final int begin, final String number) {
        int current = skipUntil(begin, data, Predicate.not(Character::isDigit));
        if (current == data.length()) {
            return -1;
        }

        final int l = current;
        current = skipUntil(current, data, Character::isDigit);

        if (number.equals(data.substring(l, current))) {
            return current;
        }

        return -1;
    }

    public static boolean verify(final String data, final int threadNumber, final int requestNumber) {
        int answer = skipUntil(0, data, Predicate.not(Character::isDigit));
        if (answer == 0) {
            return false;
        }
        answer = checkNumber(data, answer, Integer.toString(threadNumber));
        if (answer == -1) {
            return false;
        }
        answer = checkNumber(data, answer, Integer.toString(requestNumber));
        if (answer == -1) {
            return false;
        }
        return skipUntil(answer, data, Predicate.not(Character::isDigit)) == data.length();
    }

    private static int skipUntil(int start, final String data, final Predicate<Character> predicate) {
        while (start < data.length() && predicate.test(data.charAt(start))) {
            start++;
        }
        return start;
    }

}
