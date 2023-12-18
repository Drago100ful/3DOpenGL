package components;

import org.joml.Vector2f;

import org.joml.Vector4f;
import ruby.renderer.Texture;

public class Block {

    public static final int BLOCK_SIZE = 10;

    private final Texture texture;
    private final Vector2f[] uvCoordinates;
    private final Vector4f color = new Vector4f(1);;

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

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
    }

    public Vector2f[] getUvCoordinates() {
        return uvCoordinates;
    }
}
