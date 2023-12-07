package components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import ruby.Component;

public class BlockRenderer extends Component {

    private Vector4f color;

    private boolean asc = true;

    public BlockRenderer(Vector4f color) {
        this.color = color;
    }

    public Vector4f getColor() {
        return color;
    }

    @Override
    public void start() {
    }

    @Override
    public void update(float deltaTime) {
        if (color.x > 0 && color.x < 1 && asc) {
            color.add(deltaTime / 2, 0, 0, 0);

        }else if (color.x > 0 && color.x < 1 && !asc){
            color.sub(deltaTime / 2, 0, 0, 0);

        }

        else if (color.x >= 1) {
            asc = false;
            color.sub(deltaTime / 2, 0, 0, 0);
        } else if (color.x <= 0) {
            asc= true;
            color.add(deltaTime / 2, 0, 0, 0);

        }
    }
}
