package ruby.scene;

import components.BlockSheet;
import components.World;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ruby.Chunk;
import ruby.GameObject;
import ruby.Window;
import ruby.camera.Camera;
import ruby.listener.KeyListener;
import ruby.listener.MouseListener;
import ruby.util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class GameScene extends Scene {

    private final Vector3f movementVector = new Vector3f(0);
    private BlockSheet blockSheet;
    private Chunk chunk;

    public GameScene() {
        System.out.println("GameScene");
        Window.get().rgba = new Vector4f(1);
    }

    @Override
    public void init() {
        loadResources();
        MouseListener.endFrame();

        Window.setCursor(false);
        this.camera = new Camera(new Vector3f(48, -10, -16));
        this.camera.setAngle(new Vector2f(0, 0));

        chunk = new Chunk(true);
        chunk.start();


    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addBlockSheet("assets/uv-test.png", new BlockSheet(AssetPool.getTexture("assets/uv-test.png"), 32, 32, 24, 0));

        blockSheet = AssetPool.getBlockSheet("assets/uv-test.png");
    }

    @Override
    public void update(float deltaTime) {
        Window.setTile(String.valueOf((1.0f / deltaTime)));
        movementVector.zero();

        if (KeyListener.isKeyDown(GLFW_KEY_DELETE)) {
            chunk.setBlock(null, 0,0,0);
        }

        if (KeyListener.isKeyDown(GLFW_KEY_W)) {
            movementVector.z += 1;
        }
        if (KeyListener.isKeyDown(GLFW_KEY_S)) {
            movementVector.z -= 1;
        }
        if (KeyListener.isKeyDown(GLFW_KEY_D)) {
            movementVector.x -= 1;
        }

        if (KeyListener.isKeyDown(GLFW_KEY_A)) {
            movementVector.x += 1;
        }

        if (KeyListener.isKeyDown(GLFW_KEY_SPACE)) {
            movementVector.y -= 1;
        }

        if (KeyListener.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            movementVector.y += 1;
        }

        camera.updateAngle(MouseListener.getDxDy());

        if ((movementVector.x != 0) || (movementVector.y != 0) || (movementVector.z != 0)) {
            movementVector.normalize();
        }

        camera.translatePosition(movementVector);

        chunk.render();

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }

//        this.renderer.render();
        MouseListener.endFrame();
    }
}
