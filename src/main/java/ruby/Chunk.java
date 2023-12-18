package ruby;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30C.glVertexAttribIPointer;

import components.Block;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import ruby.renderer.Shader;
import ruby.renderer.Texture;
import ruby.util.AssetPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Chunk {

    public static final int CHUNK_X = 16;
    public static final int CHUNK_Y = 128;
    public static final int CHUNK_Z = 16;
    public static final int CHUNK_SIZE = CHUNK_X * CHUNK_Y * CHUNK_Z;

    private static final int POS_SIZE = 1;
    private static final int COLOR_SIZE = 1;
    private static final int UV_SIZE = 2;
    private static final int TEXTURE_SIZE = 1;

    private static final int POS_OFFSET = 0;
    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private static final int UV_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private static final int TEXTURE_OFFSET = UV_OFFSET + UV_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + UV_SIZE + TEXTURE_SIZE;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private static final int PACKLIMIT = 1024; // 2^10 / 10 bit
    private static final int PACKFACTOR_COLOR = 255; // 2^8 / 8 bit
    private static final int PACKFACTOR_X = PACKLIMIT / (CHUNK_X + 1);
    private static final int PACKFACTOR_Y = PACKLIMIT / (CHUNK_Y + 1);
    private static final int PACKFACTOR_Z = PACKLIMIT / (CHUNK_Z + 1);

    private final Block[][][] blocks;
    private final List<Vector3f> dirtyBlocks = new ArrayList<>();
    private final float[] vertices = new float[CHUNK_SIZE * 24 * VERTEX_SIZE];
    private final int[] texSlots = IntStream.range(0, 8).toArray();
    private final ArrayList<Integer> changedVoxels = new ArrayList<>();
    private final ArrayList<Texture> textures = new ArrayList<>();
    private boolean isDirty = true;
    private boolean isInitialized = false;
    private int xPos;
    private int zPos;
    private Shader shader;
    private int vaoId;
    private int vboId;

    public Chunk() {

        xPos = 0;
        zPos = 0;

        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.blocks = new Block[CHUNK_X][CHUNK_Y][CHUNK_Z];
    }

    public Chunk(int xPos, int zPos) {

        this.xPos = xPos;
        this.zPos = zPos;

        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.blocks = new Block[CHUNK_X][CHUNK_Y][CHUNK_Z];
    }

    public Chunk(boolean ignored) {
        this();

        Block stone = AssetPool.getBlockSheet("assets/uv-test.png").getBlock(1);
        textures.add(stone.getTexture());

        for (int x = 0; x < CHUNK_X; x++) {
            for (int y = 0; y < CHUNK_Y / 32; y++) {
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

    private static int generateBlockPosition(int x, int y, int z) {
        return y * 2 * CHUNK_Y + x * CHUNK_X + z;
    }

    public void setX(int xPos) {
        this.xPos = xPos;
    }

    public void setZ(int zPos) {
        this.zPos = zPos;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public void setBlock(Block block, int x, int y, int z) {
        blocks[x][y][z] = block;

        if (block != null) {
            Texture texture = block.getTexture();

            if ((texture != null) && (!textures.contains(texture))) {
                textures.add(texture);
            }
        }

        dirtyBlocks.add(new Vector3f(x, y, z));
        this.isDirty = true;
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
            return;
        }

        Vector2f[] uv = block.getUvCoordinates();

        if ((x < (CHUNK_X - 1)) && (blocks[x + 1][y][z] != null)) {
            right = false;
        }

        if ((x > 0) && (blocks[x - 1][y][z] != null)) {
            left = false;
        }

        if ((y > 0) && (blocks[x][y - 1][z] != null)) {
            top = false;
        }

        if ((y < (CHUNK_Y - 1)) && (blocks[x][y + 1][z] != null)) {
            bottom = false;
        }

        if ((z < (CHUNK_Z - 1)) && (blocks[x][y][z + 1] != null)) {
            front = false;
        }

        if ((z > 0) && (blocks[x][y][z - 1] != null)) {
            back = false;
        }

        int xAdd;
        int yAdd;
        int zAdd;

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
                i += 4;
            }

            if (i == 24) {
                continue;
            }

            switch (i) {
                    //   FL
                case 0, 21, 7 -> {
                    xAdd = 1;
                    yAdd = 1;
                    zAdd = 1;
                }
                case 1, 16, 6 -> {
                    xAdd = 1;
                    yAdd = 0;
                    zAdd = 1;
                }
                case 2, 19, 9 -> {
                    xAdd = 0;
                    yAdd = 0;
                    zAdd = 1;
                }
                case 3, 22, 8 -> {
                    xAdd = 0;
                    yAdd = 1;
                    zAdd = 1;
                }
                case 4, 20, 15 -> {
                    xAdd = 1;
                    yAdd = 1;
                    zAdd = 0;
                }
                case 5, 17, 14 -> {
                    xAdd = 1;
                    yAdd = 0;
                    zAdd = 0;
                }
                case 10, 18, 13 -> {
                    xAdd = 0;
                    yAdd = 0;
                    zAdd = 0;
                }
                case 11, 23, 12 -> {
                    xAdd = 0;
                    yAdd = 1;
                    zAdd = 0;
                }
                default -> throw new RuntimeException("Unexpected vertex index: " + i);
            }

            int packedX = (x + xAdd) * PACKFACTOR_X;
            int packedY = (y + yAdd) * PACKFACTOR_Y;
            int packedZ = (z + zAdd) * PACKFACTOR_Z;

            int pos = (packedX << 20) | (packedY << 10) | packedZ;

            boolean hasTexture = block.hasTexture();
            int textureId = 0;

            if (hasTexture) {
                Texture texture = block.getTexture();
                for (int t = 0; t < textures.size(); t++) {
                    if (textures.get(t) == texture) {
                        textureId = t + 1;
                        break;
                    }
                }
            }

            Vector4f blockColor = block.getColor();

            int color = (((int)(blockColor.x * PACKFACTOR_COLOR)) << 24) | (((int)(blockColor.y * PACKFACTOR_COLOR)) << 16) | (((int)(blockColor.z * PACKFACTOR_COLOR)) << 8) | (int) (blockColor.w * PACKFACTOR_COLOR);

            // Load Position
            vertices[offset] = Float.intBitsToFloat(pos);
            // Load Color
            vertices[offset + 1] = Float.intBitsToFloat(color);

            // Load UV
            vertices[offset + 2] = hasTexture ? uv[i].x : 0;
            vertices[offset + 3] = hasTexture ? uv[i].y : 0;

            // Load TextureId
            vertices[offset + 4] = textureId;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per Quad (3 per Tri)
        int[] elements = new int[36 * CHUNK_SIZE];
        for (int i = 0; i < CHUNK_SIZE; ++i) {
            generateBlockIndices(elements, i);
        }

        return elements;
    }

    private ArrayList<Integer> regenerateVoxel(Vector3f pos) {
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;
        int offset = 24 * VERTEX_SIZE;

        int currentBlock = generateBlockPosition(x, y, z);
        changedVoxels.add(currentBlock * offset);

        generateVoxel(x, y, z, currentBlock, true);

        if (x < (CHUNK_X - 1)) {
            currentBlock = generateBlockPosition(x + 1, y, z);
            changedVoxels.add(currentBlock * offset);
            generateVoxel(x + 1, y, z, currentBlock, true);
        }

        if (x > 0) {
            currentBlock = generateBlockPosition(x - 1, y, z);
            changedVoxels.add(currentBlock * offset);
            generateVoxel(x - 1, y, z, currentBlock, true);
        }

        if (y < (CHUNK_Y - 1)) {
            currentBlock = generateBlockPosition(x, y + 1, z);
            changedVoxels.add(currentBlock * offset);
            generateVoxel(x, y + 1, z, currentBlock, true);
        }

        if (y > 0) {
            currentBlock = generateBlockPosition(x, y - 1, z);
            changedVoxels.add(currentBlock * offset);
            generateVoxel(x, y - 1, z, currentBlock, true);
        }

        if (z < (CHUNK_Z - 1)) {
            currentBlock = generateBlockPosition(x, y, z + 1);
            changedVoxels.add(currentBlock * offset);
            generateVoxel(x, y, z + 1, currentBlock, true);
        }

        if (z > 0) {
            currentBlock = generateBlockPosition(x, y, z - 1);
            changedVoxels.add(currentBlock * offset);
            generateVoxel(x, y, z - 1, currentBlock, true);
        }

        return changedVoxels;
    }

    public void generateVertexData() {
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
        glVertexAttribIPointer(0, POS_SIZE, GL_UNSIGNED_INT, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribIPointer(1, COLOR_SIZE, GL_UNSIGNED_INT, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, UV_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, UV_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_OFFSET);
        glEnableVertexAttribArray(3);
        isInitialized = true;
    }

    public void unbind() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);

        glBindVertexArray(0);

        shader.detach();
    }

    public void render() {
        if (isDirty) {
            if (!dirtyBlocks.isEmpty()) {
                for (Vector3f dirtyBlock : dirtyBlocks) {
                    changedVoxels.addAll(regenerateVoxel(dirtyBlock));
                }
            }


            long start = System.nanoTime();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);

            if (!changedVoxels.isEmpty()) {
                for (Integer offset : changedVoxels) {
                    glBufferSubData(
                            GL_ARRAY_BUFFER,
                            ((long) offset * Float.BYTES),
                            Arrays.copyOfRange(vertices, offset, offset + (24 * VERTEX_SIZE)));
                }

            } else {
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            }
            System.out.printf("Buffering took: %fs%n", (float) ((System.nanoTime() - start) * 1E-9));

            dirtyBlocks.clear();
            changedVoxels.clear();
            isDirty = false;
        }

        // Use shader
        shader.use();

        shader.uploadMat4f(
                Window.getCurrentScene()
                        .getCamera()
                        .getTransformationMatrix()
                        .scale(Block.BLOCK_SIZE)
                        .translate((xPos * CHUNK_X), 0, (zPos * CHUNK_Z))
                        .scale(0.99f),
                "uTransform");
        shader.uploadMat4f(
                Window.getCurrentScene().getCamera().getProjectionMatrix(), "uProjection");
        shader.uploadMat4f(Window.getCurrentScene().getCamera().getViewMatrix(), "uView");

        for (int i = 0; i < textures.size(); ++i) {
            glActiveTexture(GL_TEXTURE0 + 1 + i);
            textures.get(i).bind();
        }

        shader.uploadIntArray(texSlots, "uTextures");
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, CHUNK_SIZE * 36, GL_UNSIGNED_INT, 0);
        for (int i = 0; i < textures.size(); ++i) {
            glActiveTexture(GL_TEXTURE0 + 1 + i);
            textures.get(i).unbind();
        }

        shader.detach();
    }
}
