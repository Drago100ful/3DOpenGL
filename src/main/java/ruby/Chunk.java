package ruby;

import components.Block;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ruby.renderer.Shader;
import ruby.util.AssetPool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Chunk {

    public static final int CHUNK_X = 16;
    public static final int CHUNK_Y = 128;
    public static final int CHUNK_Z = 16;
    public static final int CHUNK_SIZE = CHUNK_X * CHUNK_Y * CHUNK_Z;


    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int UV_SIZE = 2;
    private final int TEXTURE_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int UV_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_OFFSET = UV_OFFSET + UV_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + UV_SIZE + TEXTURE_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    private final int[] elements = new int[36 * CHUNK_SIZE * 24 * VERTEX_SIZE];
    private final Block[][][] blocks;
    private final int[] texSlots = IntStream.range(0, 8).toArray();
    private final float[] vertices = new float[CHUNK_SIZE * 24 * VERTEX_SIZE];
    private int xPos;
    private int zPos;
    private Shader shader;
    private int vaoId, vboId;

    private boolean isDirty = true;
    private List<Vector3f> dirtyBlocks = new ArrayList<>();


    public Chunk() {

        xPos = 0;
        zPos = 0;

        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.blocks = new Block[CHUNK_X][CHUNK_Y][CHUNK_Z];
    }

    public Chunk(boolean genStone) {
        this();

        Block stone = AssetPool.getBlockSheet("assets/uv-test.png").getBlock(1);

        for (int x = 0; x < CHUNK_X; x++) {
            for (int y = 0; y < CHUNK_Y; y++) {
                for (int z = 0; z < CHUNK_Z; z++) {
                    blocks[x][y][z] = stone;
                }
            }
        }
        generateVertexData();
    }

    public Chunk(Block[][][] blocks) {
        this.blocks = blocks;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public void setBlock(Block block, int x, int y, int z) {
        blocks[x][y][z] = block;
        dirtyBlocks.add(new Vector3f(x, y, z));
        this.isDirty = true;
    }

    public int[] generateIndices() {
        // 6 indices per Quad (3 per Tri)
        int[] elements = new int[36 * CHUNK_SIZE];
        for (int i = 0; i < CHUNK_SIZE; ++i) {
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

    private void generateVoxel(int x, int y, int z, int index, boolean regen) {
        Block block = blocks[x][y][z];

        if (block == null && !regen) {
            return;
        }

        int offset = index * 24 * VERTEX_SIZE;
        boolean top = true;
        boolean bottom = true;
        boolean left = true;
        boolean right = true;
        boolean front = true;
        boolean back = true;

        if (block == null) {
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < VERTEX_SIZE; j++) {
                    vertices[offset + j] = 0;
                }
                offset += VERTEX_SIZE;
            }

            if (x < CHUNK_X) {
                regenerateVoxel(new Vector3f((x + 1),y,z));
            }

            if (x > 0) {
                regenerateVoxel(new Vector3f((x - 1),y,z));
            }

            if (y < CHUNK_Y) {
                regenerateVoxel(new Vector3f(x,(y + 1),z));
            }

            if (y > 0) {
                regenerateVoxel(new Vector3f(x,(y - 1),z));

            }

            if (z < CHUNK_Z) {
                regenerateVoxel(new Vector3f(x,y,(z + 1)));
            }

            if (z > 0) {
                regenerateVoxel(new Vector3f(x,y,(z - 1)));
            }


            return;
        }


        Vector2f[] uv = block.getUvCoordinates();

        if ((x < 15) && (blocks[x + 1][y][z] != null)) {
            right = false;
        }

        if ((x > 0) && (blocks[x - 1][y][z] != null)) {
            left = false;
        }

        if ((y > 0) && (blocks[x][y - 1][z] != null)) {
            top = false;
        }

        if ((y < 127) && (blocks[x][y + 1][z] != null)) {
            bottom = false;
        }

        if ((z < 15) && (blocks[x][y][z + 1] != null)) {
            front = false;
        }

        if ((z > 0) && (blocks[x][y][z - 1] != null)) {
            back = false;
        }

        float xAdd, yAdd, zAdd;


        for (int i = 0; i < 24; i++) {
            if (i == 0 && !front) {
                i += 4;
            }
            if (i == 4 && !right) {
                i += 4;
            }

            if (i == 8 && !left) {
                i += 4;
            }

            if (i == 12 && !back) {
                i += 4;
            }

            if (i == 16 && !top) {
                i += 4;
            }

            if (i == 20 && !bottom) {
                i+=4;
            }

            if (i == 24) {
                break;
            }

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
                default -> throw new RuntimeException("Unexpected vertex case: " + i);
            }


            // Load Position
            vertices[offset] = (xPos + x * 5) + (xAdd * 5);
            vertices[offset + 1] = y * 5 + (yAdd * 5);
            vertices[offset + 2] = (zPos + z * 5) - (zAdd * 5);

            // Load Color
            vertices[offset + 3] = 0.5f;
            vertices[offset + 4] = 0.5f;
            vertices[offset + 5] = 0.5f;
            vertices[offset + 6] = 1f;

            // Load UV
            vertices[offset + 7] = uv[i].x;
            vertices[offset + 8] = uv[i].y;

            // Load TextureId
            vertices[offset + 9] = 0;

            offset += VERTEX_SIZE;
        }
    }

    private void generateVertexData() {
        int index = 0;

        for (int y = 0; y < CHUNK_Y; y++) {
            for (int x = 0; x < CHUNK_X; x++) {
                for (int z = 0; z < CHUNK_Z; z++) {
                    generateVoxel(x, y, z, index, false);
                    index++;
                }
            }
        }
    }

    private int regenerateVoxel(Vector3f pos) {
        System.out.println(pos);
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        int blockPosition = y*2*CHUNK_Y + x * CHUNK_X + z;

        generateVoxel(x, y, z, blockPosition, true);

        return blockPosition * 24 * VERTEX_SIZE;
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
        int eboId = glGenBuffers();
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

        glVertexAttribPointer(3, TEXTURE_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_OFFSET);
        glEnableVertexAttribArray(3);


    }

    public void destroy() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);

//        for (int i = 0; i < textures.size(); ++i) {
//            glActiveTexture(GL_TEXTURE0 + 1 + i);
//            textures.get(i).unbind();
//        }


        glBindVertexArray(0);

        shader.detach();
    }


    public void render() {
        // For now: Rebuffer Data every frame
        if (isDirty) {
            int[] offsets = new int[dirtyBlocks.size()];
            for (Vector3f pos : dirtyBlocks) {
                regenerateVoxel(pos);
            }

            long start = System.nanoTime();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            System.out.println("Buffering took: " + (float) ((System.nanoTime()-start)*1E-9));
            dirtyBlocks.clear();
            isDirty = false;
        }

        // Use shader
        shader.use();

        shader.uploadMat4f(Window.getCurrentScene().getCamera().getTransformationMatrix(), "uTransform");
        shader.uploadMat4f(Window.getCurrentScene().getCamera().getProjectionMatrix(), "uProjection");
        shader.uploadMat4f(Window.getCurrentScene().getCamera().getViewMatrix(), "uView");
//
//        for (int i = 0; i < textures.size(); ++i) {
//            glActiveTexture(GL_TEXTURE0 + 1 + i);
//            textures.get(i).bind();
//        }

        shader.uploadIntArray(texSlots, "uTextures");
        glBindVertexArray(vaoId);

        glDrawElements(GL_TRIANGLES, CHUNK_SIZE * 36, GL_UNSIGNED_INT, 0);

        shader.detach();
    }

}