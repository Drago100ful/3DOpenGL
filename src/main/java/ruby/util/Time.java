package ruby.util;

public class Time {

    public static final long timeStarted = System.nanoTime();

    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * 1E-9);
    }
}
