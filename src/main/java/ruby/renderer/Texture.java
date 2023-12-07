package ruby.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int textureId;
    private final String filepath;
    private final int textureWidth;
    private final int textureHeight;


    public Texture(String filepath) {
        this.filepath = filepath;

        // Generate Texture
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Set texture parameters

        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // When stretching: pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking: pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);

        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image == null) {
            throw new RuntimeException("Could not load texture: " + filepath);
        }

        textureHeight = height.get(0);
        textureWidth = width.get(0);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

        stbi_image_free(image);
    }

    public int getWidth() {
        return textureWidth;
    }

    public int getHeight() {
        return textureHeight;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }


}
