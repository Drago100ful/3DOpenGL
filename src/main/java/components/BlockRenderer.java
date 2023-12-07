package components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import ruby.Component;
import ruby.renderer.Texture;

public class BlockRenderer extends Component {

    private Vector4f color;
    private Block block;


    public BlockRenderer(Vector4f color) {
        this.color = color;
        this.block = new Block(null);
    }

    public BlockRenderer(Block block) {
        this.color = new Vector4f(1);
        this.block = block;
    }

    public Vector2f[] getUvCoordinates() {
        return block.getUvCoordinates();
    }

    public Texture getTexture() {
        return block.getTexture();
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
