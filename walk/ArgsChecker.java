package info.kgeorgiy.ja.smaglii.walk;

public class ArgsChecker {
    public static void checkArgs(final String[] args) throws IllegalArgumentException {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("must be exactly two arguments");
        }
    }
}
