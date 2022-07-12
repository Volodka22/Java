package info.kgeorgiy.ja.smaglii.walk;

public class Walk {
    public static void main(String[] args) {
        try {
            ArgsChecker.checkArgs(args);
            WalkUtils.walk(args[0], args[1], 0);
        } catch (WalkException | IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
