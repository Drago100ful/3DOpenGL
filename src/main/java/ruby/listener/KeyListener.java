package ruby.listener;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static KeyListener instance;
    private final boolean[] keyPressed = new boolean[350];
    private final boolean[] modifierPressed = new boolean[96];

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

    public static boolean isModifierDown(int... keys) {
        // Sum up all modifiers to get array index
        int sum = 0;

        for (int key : keys) {
            sum += key;
        }

        if (sum >= get().modifierPressed.length) {
            throw new RuntimeException(get().getClass().getName() + ": Modifier index " + sum + " is larger than" + get().modifierPressed.length);
        }

        return get().modifierPressed[sum];
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int modifier) {
        if (key >= get().keyPressed.length) {
            throw new RuntimeException(get().getClass().getName() + ": Key index" + key + " is larger than key array");
        }

        switch (action) {
            case GLFW_PRESS -> {
                get().modifierPressed[modifier] = true;
                get().keyPressed[key] = true;
            }
            case GLFW_RELEASE -> {
                get().modifierPressed[modifier] = false;
                get().keyPressed[key] = false;

            }
            default -> System.out.println("Unhandled key action: " + action);
        }
    }
}
