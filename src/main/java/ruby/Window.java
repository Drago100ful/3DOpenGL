package ruby;

import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import ruby.listener.KeyListener;
import ruby.listener.MouseListener;
import ruby.scene.BlankScene;
import ruby.scene.GameScene;
import ruby.scene.Scene;
import ruby.util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

public class Window {

    private static Window window = null;
    private static Scene currentScene = null;
    private final String title;
    private final int width;
    private final int height;
    public Vector4f rgba = new Vector4f(1);
    private long glfwWindow;
    private boolean cursorEnabled;

    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "Default";
        this.cursorEnabled = true;
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }


    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static void changeScene(int scene) {
        switch (scene) {
            case 0 -> {
                currentScene = new BlankScene();
                currentScene.init();
                currentScene.start();
            }
            case 1 -> {
                currentScene = new GameScene();
                currentScene.init();
                currentScene.start();
            }
            default -> throw new RuntimeException("Unknown scene index: " + scene);
        }
    }

    public static Scene getCurrentScene() {
        return Window.currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void loop() {
        float frameBegin = Time.getTime();
        float frameEnd = Time.getTime();
        float deltaTime = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();


            if (KeyListener.isKeyDown(GLFW_KEY_Q) && KeyListener.isModifierDown(GLFW_MOD_CONTROL, GLFW_MOD_SHIFT)) {
                System.exit(0);
            }

            glClearColor(rgba.x, rgba.y, rgba.z, rgba.w);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (deltaTime >= 0) {
                currentScene.update(deltaTime);
            }

            glfwSwapBuffers(glfwWindow);

            // Calculate Delta Time; Required to be at end!
            frameEnd = Time.getTime();
            deltaTime = frameEnd - frameBegin;
            frameBegin = frameEnd;
        }
    }

    public static void setCursor(boolean mode) {
        get().cursorEnabled = mode;

        if (mode) {
            glfwSetInputMode(get().glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            return;
        }

        glfwSetInputMode(get().glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public static void flipCursorMode() {
        setCursor(!get().cursorEnabled);
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Could not init GLFW");
        }

        // Set Window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, MemoryUtil.NULL, MemoryUtil.NULL);

        if (glfwWindow == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to create glfwWindow");
        }

        // Setup Callbacks //
        // Mouse
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePositionCallBack);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        // Keyboard
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // OpenGL current
        glfwMakeContextCurrent(glfwWindow);
        // Enable VSYNC
        glfwSwapInterval(1);

        // Show window
        glfwShowWindow(glfwWindow);

        // Create Buffers
        GL.createCapabilities();


        // Blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //DEPTH
        glEnable(GL_DEPTH_TEST);

        // Backface-Culling
        glEnable(GL_CULL_FACE);

        // Change to blankScene
        Window.changeScene(0);

    }

}
