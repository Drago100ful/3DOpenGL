package ruby.scene;

import components.Block;
import components.BlockSheet;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ruby.Chunk;
import ruby.ChunkManager;
import ruby.GameObject;
import ruby.Window;
import ruby.camera.Camera;
import ruby.listener.KeyListener;
import ruby.listener.MouseListener;
import ruby.util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class GameScene extends Scene {

    private final Vector3f movementVector = new Vector3f(0);

    public GameScene() {
        System.out.println("GameScene");
        Window.get().rgba = new Vector4f(1);
    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector3f(0, 0, 0));
        this.camera.setAngle(new Vector2f(0, 0));

        ChunkManager.init();

        MouseListener.endFrame();
        Window.setCursor(false);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addBlockSheet("assets/uv-test.png", new BlockSheet(AssetPool.getTexture("assets/uv-test.png"), 32, 32, 24, 0));
    }

    @Override
    public void update(float deltaTime) {
        Window.setTile(String.valueOf((1.0f / deltaTime)));
        movementVector.zero();
//        System.out.println("Chunk X: " + (int) (camera.getPosition().x / (Chunk.CHUNK_X * Block.BLOCK_SIZE)) + " Chunk Z: " + (int) (camera.getPosition().z / (Chunk.CHUNK_Z * Block.BLOCK_SIZE)));

        Window.setTile("POS XZ: " + ((int) (camera.getPosition().x / (Block.BLOCK_SIZE))) + " | " + ((int) (camera.getPosition().z / (Block.BLOCK_SIZE))));

        ChunkManager.update(camera.getPosition());
        ChunkManager.render();


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

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }

//        this.renderer.render();
        MouseListener.endFrame();
    }
}
