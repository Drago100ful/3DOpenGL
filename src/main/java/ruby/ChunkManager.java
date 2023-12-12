package ruby;

import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

import static ruby.Chunk.CHUNK_X;
import static ruby.Chunk.CHUNK_Z;

public class ChunkManager {

    private static ChunkManager singleton = null;
    private final Map<String, Chunk> chunks;

    public ChunkManager() {
        this.chunks = new HashMap<>();
    }

    public static ChunkManager get() {
        if (singleton == null) {
            singleton = new ChunkManager();
        }

        return singleton;
    }

    /**
     * @param x World X
     * @param z World Z
     * @return Chunk
     */
    public static Chunk getChunk(int x, int z) {
        x /= CHUNK_X;
        z /= CHUNK_Z;

        String xz = x + "," + z;

        if (!get().chunks.containsKey(xz)) {
            get().chunks.put(xz, TerrainBuilder.generateChunk(x, z));
        }

        return get().chunks.get(xz);
    }


    public static void update(Vector3f position) {
        getChunk((int) position.x, (int) position.z);
    }
}
