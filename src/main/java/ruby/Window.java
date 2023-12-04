package ruby;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import ruby.listener.KeyListener;
import ruby.listener.MouseListener;
import ruby.util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.Key;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Window {

    private static Window window = null;
    private int width;
    private int height;
    private String title;
    private long glfwWindow;

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "Default";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
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
        float deltaTime = -1;

        Shader shader = new Shader("assets/shaders/default.glsl");

        shader.compile();

        Camera camera = new Camera(new Vector3f(0, 0, 0));

        int vertexId, fragmentId, shaderProgram;

        float[] vertexArray = {
            // POS  XYZ             // COLOR
             5f, -5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f, //FBR
            -5f,  5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f, //FTL
             5f,  5f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f, //FTR
            -5f, -5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,  //FBL

             5f,  5f, -10.0f,     0.0f, 0.0f, 1.0f, 1.0f, //RTR
             5f, -5f, -10.0f,     1.0f, 0.0f, 0.0f, 1.0f, //RBR

            -5f,  5f, -10.0f,     0.0f, 1.0f, 0.0f, 1.0f, //LTL
            -5f, -5f, -10.0f,     1.0f, 1.0f, 0.0f, 1.0f,  //LBL

        };

        // IMPORTANT: CCW ORDER!
        int[] elementArray = {

            // Back
            4, 6, 5,
            5, 6, 7,
            // Left
            1, 6, 3,
            3, 6, 7,
            // Right
            4, 2, 0,
            5, 4, 0,
            // Bottom
            0, 5, 3,
            5, 7, 3,
            // Top
            2, 4, 1,
            4, 6, 1,
            // Front
            2, 1, 0,
            0, 1, 3,


        };


        int vaoId, vboId, eboId;

        // VAO BUFFER (VERTEX ARRAY OBJECT)
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Vertex attribute pointers
        int positionSize = 3; //XYZ
        int colorSize = 4; //RGBA
        int vertexSizeInBytes = (positionSize+colorSize) * Float.BYTES;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, (positionSize * Float.BYTES));
        glEnableVertexAttribArray(1);

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (KeyListener.isKeyDown(GLFW_KEY_W)) {
                camera.setPosition(camera.getPosition().add(0,0, 25*deltaTime));
            } else if (KeyListener.isKeyDown(GLFW_KEY_S)) {
                camera.setPosition(camera.getPosition().sub(0,0, 25*deltaTime));
            } else if (KeyListener.isKeyDown(GLFW_KEY_D)) {
                camera.setPosition(camera.getPosition().add(25*deltaTime,0, 0));
            } else if (KeyListener.isKeyDown(GLFW_KEY_A)) {
                camera.setPosition(camera.getPosition().sub(25*deltaTime,0, 0));
            } else if (KeyListener.isKeyDown(GLFW_KEY_LEFT)) {
                camera.setRotation(camera.getRotation()-1*deltaTime);
            } else if (KeyListener.isKeyDown(GLFW_KEY_RIGHT)) {
                camera.setRotation(camera.getRotation()+1*deltaTime);
            }

            shader.use();

            shader.uploadMat4f(camera.getTransformationMatrix(), "uTransform");
            shader.uploadMat4f(camera.getProjectionMatrix(), "uProjection");
            shader.uploadMat4f(camera.getViewMatrix(), "uView");

            // Bind VAO
            glBindVertexArray(vaoId);
            // Enable Pointers
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

            // Unbind
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
            shader.detach();

            glfwSwapBuffers(glfwWindow);

            // Calculate Delta Time; Required to be at end!
            frameEnd = Time.getTime();
            deltaTime = frameEnd - frameBegin;
            frameBegin = frameEnd;
        }
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

    }

}
