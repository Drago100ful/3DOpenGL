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
import ruby.util.noise.PerlinNoise;


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
        this.camera = new Camera(new Vector3f(0, -10, -40));

        loadResources();

        int size = 5;


//        PerlinNoise perlinNoise = new PerlinNoise();
//        float[] seed = new float[256*256];
//
//        for (int i = 0; i < seed.length; i++) {
//            seed[i] = ThreadLocalRandom.current().nextFloat(0, 1);
//        }
//
//        float[] noise = perlinNoise.noise2D(256, 256, seed, 6, 0.75f);
//
//        perlinNoise.visualize(noise, 256, 2);
//
//        for (int x = 0; x < 128; ++x) {
//            for (int z = 0; z < 128; ++z) {
//                GameObject block = new GameObject("Block " + x + z, new Transform(new Vector3f(x*size, (int)( Math.floor(noise[x * 256 + z] * 15) * size), z*size), new Vector3f(size)));
//                block.add(new BlockRenderer(test.getBlock(1)));
//                this.addGameObjectToScene(block);
//            }
//        }

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; ++z) {
                    GameObject block = new GameObject("Block " + x + y + z, new Transform(new Vector3f(x*size, y*size, z*size), new Vector3f(size)));
                    block.add(new BlockRenderer(test.getBlock(1)));
                    this.addGameObjectToScene(block);
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
        Window.setTile(String.valueOf((1.0f / deltaTime)));
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
