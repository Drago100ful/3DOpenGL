package ruby.listener;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static KeyListener instance;
    private final boolean[] keyPressed = new boolean[350];

    private KeyListener() {

    }

    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }

        return KeyListener.instance;
    }

    public static boolean isKeyDown(int key) {
        if (key >= get().keyPressed.length) {
            assert false : "Key index " + key + " is larger than" + get().keyPressed.length;
            return false;
        }

        return get().keyPressed[key];
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int modifier) {
        if (key >= get().keyPressed.length) {
            assert false : "Key index " + key + " is larger than" + get().keyPressed.length;
            return;
        }

        switch (action) {
            case GLFW_PRESS -> get().keyPressed[key] = true;
            case GLFW_RELEASE -> get().keyPressed[key] = false;
            default -> System.out.println("Unhandled key action: " + action);
        }
    }
}
