package info.kgeorgiy.ja.smaglii.walk;

public class RecursiveWalk {
    public static void main(String[] args) {
        try {
            ArgsChecker.checkArgs(args);
            WalkUtils.walk(args[0], args[1], Integer.MAX_VALUE);
        } catch (WalkException | IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
