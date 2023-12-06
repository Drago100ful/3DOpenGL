package ruby.scene;

import ruby.GameObject;
import ruby.camera.Camera;
import ruby.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Renderer renderer = new Renderer();
    protected Camera camera;

    private boolean isRunning = false;

    public Scene() {

    }

    public abstract void update(float deltaTime);

    public void init() {}

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }

        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjects.add(go);

        if (isRunning) {
            go.start();
            renderer.add(go);
        }
    }

    public Camera getCamera() {
        return camera;
    }
}
