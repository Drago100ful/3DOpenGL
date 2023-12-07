package ruby.renderer;

import components.BlockRenderer;
import org.joml.Vector4f;
import ruby.Window;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {

    /*
        VERTEX:
        POS: 3 Floats
        Color: 4 Floats
        UV: 2 Floats
     */

    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int UV_SIZE = 2;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int UV_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + UV_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private final BlockRenderer[] blockRenderers;
    private final Shader shader;
    private final float[] vertices;
    private final int maxBatchSize;
    private int numBlocks;
    private boolean hasRoom;
    private int vaoId, vboId, eboId;

    public RenderBatch(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        this.shader = new Shader("assets/shaders/default.glsl");
        shader.compile();

        this.blockRenderers = new BlockRenderer[maxBatchSize];

        // 1 Block = 4 Quads * 6 Faces = 24
        vertices = new float[maxBatchSize * 24 * VERTEX_SIZE];

        this.numBlocks = 0;
        this.hasRoom = true;
    }

    public void start() {
        // Generate and bind a Vertex Array
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Allocate memory for vertices
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        eboId = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, UV_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, UV_OFFSET);
        glEnableVertexAttribArray(2);


    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public void render() {
        // For now: Rebuffer Data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Use shader
        shader.use();

        shader.uploadMat4f(Window.getCurrentScene().getCamera().getTransformationMatrix(), "uTransform");
        shader.uploadMat4f(Window.getCurrentScene().getCamera().getProjectionMatrix(), "uProjection");
        shader.uploadMat4f(Window.getCurrentScene().getCamera().getViewMatrix(), "uView");

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, numBlocks * 36, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glBindVertexArray(0);

        shader.detach();
    }


    public void loadVertexProperties(int index) {
        BlockRenderer block = blockRenderers[index];
        // Find offset within array => 4 vertices per face * 6 faces = 24
        int offset = index * 24 * VERTEX_SIZE;

        Vector4f color = block.getColor();

        float xAdd, yAdd, zAdd;

        /*    x---x
          3 x---x 0
              x---x
          2 x---x 1
         */

        for (int i = 0; i < 24; ++i) {
            switch (i) {
                //   FL
                case 0, 21, 7 -> {
                    xAdd = 1f;
                    yAdd = 1f;
                    zAdd = 0f;
                }
                case 1, 16, 6 -> {
                    xAdd = 1f;
                    yAdd = 0f;
                    zAdd = 0f;
                }
                case 2, 19, 9 -> {
                    xAdd = 0f;
                    yAdd = 0f;
                    zAdd = 0f;
                }
                case 3, 22, 8 -> {
                    xAdd = 0f;
                    yAdd = 1f;
                    zAdd = 0f;
                }
                case 4, 20, 15 -> {
                    xAdd = 1f;
                    yAdd = 1f;
                    zAdd = 1f;
                }
                case 5, 17, 14 -> {
                    xAdd = 1f;
                    yAdd = 0f;
                    zAdd = 1f;
                }
                case 10, 18, 13 -> {
                    xAdd = 0f;
                    yAdd = 0f;
                    zAdd = 1f;
                }
                case 11, 23, 12 -> {
                    xAdd = 0f;
                    yAdd = 1f;
                    zAdd = 1f;
                }
                default -> throw new RuntimeException("Unexpected vertex case");
            }

            // Load Position
            vertices[offset] = block.gameObject.transform.position.x + (xAdd * block.gameObject.transform.scale.x);
            vertices[offset + 1] = block.gameObject.transform.position.y + (yAdd * block.gameObject.transform.scale.y);
            vertices[offset + 2] = block.gameObject.transform.position.z - (zAdd * block.gameObject.transform.scale.z);

            // Load Color
            vertices[offset + 3] = color.x;
            vertices[offset + 4] = color.y;
            vertices[offset + 5] = color.z;
            vertices[offset + 6] = color.w;

            // Load UV
            vertices[offset + 7] = 1;
            vertices[offset + 8] = 1;

            offset += VERTEX_SIZE;
        }

    }


    public void addBlock(BlockRenderer block) {
        // Get index and add RenderObject
        int index = numBlocks;
        blockRenderers[numBlocks] = block;
        ++numBlocks;

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (numBlocks >= maxBatchSize) {
            hasRoom = false;
        }
    }

    private int[] generateIndices() {
        // 6 indices per Quad (3 per Tri)
        int[] elements = new int[36 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; ++i) {
            generateBlockIndices(elements, i);
        }

        return elements;
    }

    private void generateBlockIndices(int[] elements, int index) {
        int posArray = index * 36;
        int offset = 24 * index;

        for (int i = 0; i < 36; i += 6) {
            elements[posArray + i] = offset + 3;
            elements[posArray + i + 1] = offset + 2;
            elements[posArray + i + 2] = offset;

            elements[posArray + i + 3] = offset;
            elements[posArray + i + 4] = offset + 2;
            elements[posArray + i + 5] = offset + 1;

            offset += 4;
        }
    }


}
