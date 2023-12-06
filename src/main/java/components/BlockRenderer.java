package components;

import org.joml.Vector4f;
import ruby.Component;

public class BlockRenderer extends Component {

    private Vector4f color;

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

    }
}
