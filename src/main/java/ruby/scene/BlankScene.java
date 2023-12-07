package ruby.scene;

import components.Block;
import components.BlockRenderer;
import components.BlockSheet;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ruby.GameObject;
import ruby.Window;
import ruby.camera.Camera;
import ruby.camera.Transform;
import ruby.listener.KeyListener;
import ruby.renderer.Texture;
import ruby.util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class BlankScene extends Scene {

    GameObject testGo;
    private boolean changingScene = false;
    private float changeTime = 2f;

    public BlankScene() {
        System.out.println("Blank scene");
    }

    private BlockSheet test;

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f(0, 0, 0));

        loadResources();

        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;

        float totalWidth = (640 - offsetX * 2);
        float size = totalWidth / 100;

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                for (int z = 0; z < 3; ++z) {
                    float xPos = offsetX + x * size;
                    float yPos = offsetY + y * size;
                    float zPos = offsetZ + z * size;

                    GameObject go = new GameObject("Obj " + x + " " + y + " " + z, new Transform(new Vector3f(xPos, yPos, zPos), new Vector3f(size * 0.75f)));
                    go.add(new BlockRenderer(test.getBlock(0)));
                    this.addGameObjectToScene(go);
                }
            }
        }

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addBlockSheet("assets/uv-test.png",
                new BlockSheet(AssetPool.getTexture("assets/uv-test.png"),
                        32, 32, 6, 0)
        );

        test = AssetPool.getBlockSheet("assets/uv-test.png");
    }

    @Override
    public void update(float deltaTime) {
//        System.out.println("FPS: " + (1 / deltaTime));

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

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }

        this.renderer.render();
    }
}
