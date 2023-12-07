package ruby.util;

import ruby.renderer.Shader;
import ruby.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static Map<String, Shader> shaderMap = new HashMap<>();
    private static Map<String, Texture> textureMap = new HashMap<>();

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


}
