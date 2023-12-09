package ruby.scene;

import org.joml.Vector3f;
import org.joml.Vector4f;
import ruby.Window;
import ruby.camera.Camera;

public class GameScene extends Scene {

    public GameScene() {
        System.out.println("GameScene");
        Window.get().rgba = new Vector4f(1);
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f(0));
    }

    @Override
    public void update(float deltaTime) {

    }
}
