package ruby.listener;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;
    private final boolean[] mouseDown = new boolean[3];
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = this.scrollY = 0.0f;
        this.xPos = this.yPos = 0.0f;
        this.lastX = this.lastY = 0.0f;
        this.isDragging = false;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    public static void mousePositionCallBack(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseDown[0] || get().mouseDown[1] || get().mouseDown[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int modifier) {
        if (button >= get().mouseDown.length) {
            return;
        }

        switch (action) {
            case GLFW_PRESS -> get().mouseDown[button] = true;
            case GLFW_RELEASE -> {
                get().mouseDown[button] = false;
                get().isDragging = false;
            }
            default -> System.out.println("Unhandled mouse input: " + action);
        }

    }

    public static void mouseScrollCallback(long window, double scrollX, double scrollY) {
        get().scrollX = scrollX;
        get().scrollY = scrollY;
    }

    public static Vector2f getDxDy() {
        return new Vector2f(getDx(), getDy());
    }

    public static void endFrame() {
        get().scrollX = get().scrollY = 0.0f;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getxPos() {
        return (float) get().xPos;
    }

    public static float getyPos() {
        return (float) get().yPos;
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public boolean isMouseButtonDown(int button) {
        if (button >= get().mouseDown.length) {
            return false;
        }

        return get().mouseDown[button];
    }

    public boolean isDragging() {
        return get().isDragging;
    }
}
