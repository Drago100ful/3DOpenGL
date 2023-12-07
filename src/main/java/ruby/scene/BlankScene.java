package ruby.scene;

import components.BlockRenderer;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ruby.GameObject;
import ruby.Window;
import ruby.camera.Camera;
import ruby.camera.Transform;
import ruby.listener.KeyListener;

import static org.lwjgl.glfw.GLFW.*;

public class BlankScene extends Scene {

    GameObject testGo;
    private boolean changingScene = false;
    private float changeTime = 2f;

    public BlankScene() {
        System.out.println("Blank scene");
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f(0, 0, 0));


        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;

        float totalWidth = (640 - offsetX * 2);
        float size = totalWidth / 100;

        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 256; ++y) {
                for (int z = 0; z < 8; ++z) {
                    float xPos = offsetX + x * size;
                    float yPos = offsetY + y * size;
                    float zPos = offsetZ + z * size;

                    GameObject go = new GameObject("Obj " + x + " " + y + " " + z, new Transform(new Vector3f(xPos, yPos, zPos), new Vector3f(size * 0.75f)));
                    go.add(new BlockRenderer(new Vector4f(xPos / 35, yPos / 35, zPos / 35, 1)));
                    this.addGameObjectToScene(go);
                }
            }
        }

    }

    @Override
    public void update(float deltaTime) {
//        System.out.println("FPS: " + (1 / deltaTime));


        if (KeyListener.isKeyDown(GLFW_KEY_Q) && KeyListener.isModifierDown(GLFW_MOD_CONTROL)) {
            System.exit(0);
        }


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
