package ruby.util;

import components.BlockSheet;
import ruby.renderer.Shader;
import ruby.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static final Map<String, Shader> shaderMap = new HashMap<>();
    private static final Map<String, Texture> textureMap = new HashMap<>();
    private static final Map<String, BlockSheet> blockSheetMap = new HashMap<>();

    public static Shader getShader(String resourcePath) {
        File shaderFile = new File(resourcePath);

        if (!shaderFile.exists()) {
            System.out.println("Shader at '" + shaderFile.getAbsolutePath() + "' does not exist");
            return null;
        }

        if (!shaderMap.containsKey(shaderFile.getAbsolutePath())) {
            Shader shader = new Shader(resourcePath);
            shader.compile();
            shaderMap.put(shaderFile.getAbsolutePath(), shader);

            return shader;
        }

        return shaderMap.get(shaderFile.getAbsolutePath());
    }

    public static Texture getTexture(String resourcePath) {
        File textureFile = new File(resourcePath);

        if (!textureFile.exists()) {
            System.out.println("Texture at '" + textureFile.getAbsolutePath() + "' does not exist");
            return null;
        }

        if (!shaderMap.containsKey(textureFile.getAbsolutePath())) {
            Texture texture = new Texture(resourcePath);
            textureMap.put(textureFile.getAbsolutePath(), texture);

            return texture;
        }

        return textureMap.get(textureFile.getAbsolutePath());
    }

    public static void addBlockSheet(String resourcePath, BlockSheet blockSheet) {
        File spritesheet = new File(resourcePath);

        if (!spritesheet.exists()) {
            System.out.println("Texture (Spritesheet) at '" + spritesheet.getAbsolutePath() + "' does not exist");
        }

        if (!blockSheetMap.containsKey(spritesheet.getAbsolutePath())) {
            blockSheetMap.put(spritesheet.getAbsolutePath(), blockSheet);
        }

    }

    public static BlockSheet getBlockSheet(String resourcePath) {
        File spritesheet = new File(resourcePath);

        if (!spritesheet.exists()) {
            System.out.println("Texture (Spritesheet) at '" + spritesheet.getAbsolutePath() + "' does not exist");
        }

        if (!blockSheetMap.containsKey(spritesheet.getAbsolutePath())) {
            throw new RuntimeException("Tried to access uninitialized blocksheet: " + resourcePath);
        }

        return blockSheetMap.getOrDefault(spritesheet.getAbsolutePath(), null);
    }

}
