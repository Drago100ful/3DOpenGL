package ruby.scene;

import components.BlockRenderer;
import components.BlockSheet;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ruby.GameObject;
import ruby.Window;
import ruby.camera.Camera;
import ruby.camera.Transform;
import ruby.listener.KeyListener;
import ruby.listener.MouseListener;
import ruby.util.AssetPool;

import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.*;

public class BlankScene extends Scene {

    private final Vector3f movementVector = new Vector3f(0);
    private boolean changingScene = false;
    private float changeTime = 2f;
    private BlockSheet test;

    public BlankScene() {
        System.out.println("Blank scene");
    }

    @Override
    public void init() {
        Window.setCursor(false);
        this.camera = new Camera(new Vector3f(0, 0, 0));

        loadResources();

        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;

        float totalWidth = (640 - offsetX * 2);
        float size = totalWidth / 100;

        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                for (int z = 0; z < 5; ++z) {
                    float xPos = offsetX + x * size;
                    float yPos = offsetY + y * size;
                    float zPos = offsetZ + z * size;

                    GameObject go = new GameObject("Obj " + x + " " + y + " " + z, new Transform(new Vector3f(xPos, yPos, zPos), new Vector3f(size * 0.75f)));
                    go.add(new BlockRenderer(test.getBlock(ThreadLocalRandom.current().nextInt(0, 4))));
                    this.addGameObjectToScene(go);
                }
            }
        }

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addBlockSheet("assets/uv-test.png", new BlockSheet(AssetPool.getTexture("assets/uv-test.png"), 32, 32, 24, 0));

        test = AssetPool.getBlockSheet("assets/uv-test.png");
    }


    @Override
    public void update(float deltaTime) {
//        System.out.println("FPS: " + (1 / deltaTime));
        movementVector.zero();

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


        if (KeyListener.isKeyDown(GLFW_KEY_S)) {
            camera.setRotation(10);
        }

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }

        this.renderer.render();
        MouseListener.endFrame();
    }
}
