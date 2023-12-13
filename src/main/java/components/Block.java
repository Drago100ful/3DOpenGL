package components;

import org.joml.Vector2f;
import ruby.renderer.Texture;

public class Block {

    public static final int BLOCK_SIZE = 10;

    private Texture texture;
    private Vector2f[] uvCoordinates;

    public Block(Texture texture) {
        this.texture = texture;
        uvCoordinates = new Vector2f[24];
        for (int i = 0; i < 6; ++i) {
            uvCoordinates[i * 4] = new Vector2f(1, 1);
            uvCoordinates[i * 4 + 1] = new Vector2f(1, 0);
            uvCoordinates[i * 4 + 2] = new Vector2f(0, 0);
            uvCoordinates[i * 4 + 3] = new Vector2f(0, 1);
        }
    }

    public Block(Texture texture, Vector2f[] uv) {
        this.texture = texture;
        this.uvCoordinates = uv;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getUvCoordinates() {
        return uvCoordinates;
    }
}
