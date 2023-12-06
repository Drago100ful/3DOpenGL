package ruby.renderer;


import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import ruby.renderer.Texture;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private final String shaderString;
    private final String fragmentShader;
    private final String vertexShader;
    private final String filepath;
    private int shaderProgramId;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            shaderString = Files.readString(Path.of(this.filepath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String v = "#type vertex";
        String f = "#type fragment";

        int a = shaderString.indexOf(v);
        int b = shaderString.indexOf(f);

        if (a < b) {
            vertexShader = shaderString.substring(a + v.length(), b - 1);
            fragmentShader = shaderString.substring(b + f.length());
        } else {
            vertexShader = shaderString.substring(a + a + v.length());
            fragmentShader = shaderString.substring(b + f.length(), a - 1);
        }

    }

    public int getShaderProgramId() {
        return shaderProgramId;
    }

    public void uploadMat4f(Matrix4f matrix, String location) {
        int varLocation = glGetUniformLocation(shaderProgramId, location);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);
        use();
        glUniformMatrix4fv(varLocation, false, matrixBuffer);
    }

    public void uploadTexture2D(int slot, String location) {
        int varLocation = glGetUniformLocation(shaderProgramId, location);
        use();
        glUniform1i(varLocation, slot);
    }

    public void compile() {
        int vertexId, fragmentId;

        // Compile Vertex Shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        // Pass shader to GPU
        glShaderSource(vertexId, vertexShader);
        glCompileShader(vertexId);

        // Check for error during compilation
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == GLFW_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Shader failed to compile: \n\t" + glGetShaderInfoLog(vertexId, len));
        }

        // Compile Vertex Shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass shader to GPU
        glShaderSource(fragmentId, fragmentShader);
        glCompileShader(fragmentId);

        // Check for error during compilation
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GLFW_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Shader at '" + filepath + "' failed to compile: \n\t" + glGetShaderInfoLog(fragmentId, len));
        }

        // Link shaders and check for errors
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexId);
        glAttachShader(shaderProgramId, fragmentId);
        glLinkProgram(shaderProgramId);

        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if (success == GLFW_FALSE) {
            int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Program failed to link: \n\t" + glGetProgramInfoLog(shaderProgramId, len));
        }
    }

    public void use() {
        // Bind shader
        glUseProgram(shaderProgramId);
    }

    public void detach() {
        // Unbind shader
        glUseProgram(0);
    }

}
