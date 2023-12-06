package ruby.scene;

import components.BlockRenderer;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import ruby.GameObject;
import ruby.Window;
import ruby.camera.Camera;
import ruby.camera.Transform;
import ruby.listener.KeyListener;
import ruby.renderer.Shader;
import ruby.renderer.Texture;
import ruby.shapes.Cube;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class BlankScene extends Scene {

    GameObject testGo;
    private boolean changingScene = false;
    private float changeTime = 2f;
    private Shader shader;
    private Cube cube;
    private int vaoId, vboId, eboId;
    private Texture texture;

    public BlankScene() {
        System.out.println("Blank scene");
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f(0, 0, 0));


        int offsetX = 0;
        int offsetY = 0;

        float totalWidth = (640 - offsetX * 2);
        float totalHeight = (640 - offsetX * 2);
        float size = totalWidth / 100;

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                float xPos = offsetX + i * size;
                float yPos = offsetY + j * size;

                GameObject go = new GameObject("Obj " + i + "" + j, new Transform(new Vector3f(xPos, yPos, 1), new Vector3f(size*0.75f)));
                go.add(new BlockRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }

//
//        shader = new Shader("assets/shaders/default.glsl");
//        shader.compile();
//
//        texture = new Texture("assets/uv-test.png");
//
//
//        cube = new Cube(new Vector3f(5, 5, 5));
//
//
//        // VAO BUFFER (VERTEX ARRAY OBJECT)
//        vaoId = glGenVertexArrays();
//        glBindVertexArray(vaoId);
//
//        // Create float buffer of vertices
//        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(cube.getVertexArray().length);
//        vertexBuffer.put(cube.getVertexArray()).flip();
//
//        // Create VBO upload the vertex buffer
//        vboId = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, vboId);
//        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
//
//        // Create indices and upload
//        IntBuffer elementBuffer = BufferUtils.createIntBuffer(cube.getElementArray().length);
//        elementBuffer.put(cube.getElementArray()).flip();
//
//        eboId = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
//
//        // Vertex attribute pointers
//        int positionSize = 3; //XYZ
//        int colorSize = 4; //RGBA
//        int uvSize = 2; // UV
//        int vertexSizeInBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
//
//        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeInBytes, 0);
//        glEnableVertexAttribArray(0);
//        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, (positionSize * Float.BYTES));
//        glEnableVertexAttribArray(1);
//        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeInBytes, ((positionSize + colorSize) * Float.BYTES));
//        glEnableVertexAttribArray(2);


    }

    @Override
    public void update(float deltaTime) {
        if (!changingScene && KeyListener.isKeyDown(GLFW_KEY_C)) {
            changingScene = true;
        }

        if (changingScene && changeTime > 0) {
            Window.get().rgba.sub(new Vector4f((1 / changeTime) * deltaTime));
            changeTime -= deltaTime;
        } else if (changingScene) {
            Window.changeScene(1);
        }

        if (KeyListener.isKeyDown(GLFW_KEY_W)) {
            camera.setPosition(camera.getPosition().add(0, 0, 25 * deltaTime));
        } else if (KeyListener.isKeyDown(GLFW_KEY_S)) {
            camera.setPosition(camera.getPosition().sub(0, 0, 25 * deltaTime));
        } else if (KeyListener.isKeyDown(GLFW_KEY_D)) {
            camera.setPosition(camera.getPosition().add(25 * deltaTime, 0, 0));
        } else if (KeyListener.isKeyDown(GLFW_KEY_A)) {
            camera.setPosition(camera.getPosition().sub(25 * deltaTime, 0, 0));
        } else if (KeyListener.isKeyDown(GLFW_KEY_LEFT)) {
            camera.setRotation(camera.getRotation() - 1 * deltaTime);
        } else if (KeyListener.isKeyDown(GLFW_KEY_RIGHT)) {
            camera.setRotation(camera.getRotation() + 1 * deltaTime);
        } else if (KeyListener.isKeyDown(GLFW_KEY_UP)) {
            camera.setPosition(camera.getPosition().sub(0, 25 * deltaTime, 0));
        } else if (KeyListener.isKeyDown(GLFW_KEY_DOWN)) {
            camera.setPosition(camera.getPosition().add(0, 25 * deltaTime, 0));
        }
//
//        shader.use();
//
//        // Upload texture
//        glActiveTexture(GL_TEXTURE0);
//        shader.uploadTexture2D(0, "TEX_SAMPLER");
//        texture.bind();
//
//        shader.uploadMat4f(camera.getTransformationMatrix(), "uTransform");
//        shader.uploadMat4f(camera.getProjectionMatrix(), "uProjection");
//        shader.uploadMat4f(camera.getViewMatrix(), "uView");
//
//        // Bind VAO
//        glBindVertexArray(vaoId);
//        // Enable Pointers
//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);
//        glEnableVertexAttribArray(2);
//
//        glDrawElements(GL_TRIANGLES, cube.getElementArray().length, GL_UNSIGNED_INT, 0);
//
//        // Unbind
//        glDisableVertexAttribArray(0);
//        glDisableVertexAttribArray(1);
//        glDisableVertexAttribArray(2);
//        glBindVertexArray(0);
//        shader.detach();

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }

        this.renderer.render();
    }
}
