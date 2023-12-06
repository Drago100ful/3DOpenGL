package ruby.scene;

import org.joml.Vector4f;
import ruby.Window;

public class GameScene extends Scene {

    public GameScene() {
        System.out.println("GameScene");
        Window.get().rgba = new Vector4f(1);
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float deltaTime) {

    }
}
