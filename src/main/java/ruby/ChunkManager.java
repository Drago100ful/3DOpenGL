package ruby;

import static ruby.Chunk.CHUNK_X;
import static ruby.Chunk.CHUNK_Z;

import components.Block;

import org.joml.Vector3f;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ChunkManager {

    private static ChunkManager singleton = null;
    private final ConcurrentHashMap<String, Chunk> chunks;
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final ConcurrentLinkedQueue<String> jobs = new ConcurrentLinkedQueue<>();

    public ChunkManager() {
        this.chunks = new ConcurrentHashMap<>();
        for(int i = 0; i <= 4; i++) {
            executor.submit(() -> {
                int pos;
                int x;
                int z;
                while(!executor.isShutdown()) {
                    String job = jobs.poll();
                    while(job == null) {
                        job = jobs.poll();
                    }

                    pos = job.indexOf(",");
                    x = Integer.parseInt(job.substring(0, pos));
                    z = Integer.parseInt(job.substring(pos+1));

                    getChunk(x, z, true);
                }
            });
        }
    }

    public static ChunkManager get() {
        if (singleton == null) {
            singleton = new ChunkManager();
        }

        return singleton;
    }

    public static Chunk getChunk(int x, int z, boolean isWorldPos) {
        int posX = x;
        int posZ = z;
        if (isWorldPos) {
            posX /= (CHUNK_X * Block.BLOCK_SIZE);
            posZ /= (CHUNK_Z * Block.BLOCK_SIZE);

            if (x < 0) {
                posX--;
            }
            if (z < 0) {
                posZ--;
            }
        }

        String xz = posX + "," + posZ;

        if (!get().chunks.containsKey(xz)) {
            Chunk chunk = TerrainBuilder.generateChunk(posX, posZ);
            get().chunks.put(xz, chunk);
        }

        return get().chunks.get(xz);
    }

    public static void update(Vector3f playerPosition) {
        get().jobs.add((int) (-playerPosition.x) + "," + ((int) -playerPosition.z));
    }

    public static void init() {
        // Get a 3x3 area around player
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                get().jobs.add((x * CHUNK_X) + "," + (z*CHUNK_Z));
            }
        }
    }

    public static void render() {
        for (Chunk chunk : get().chunks.values()) {
            if (!chunk.isInitialized()) {
                chunk.start();
            }

            chunk.render();
        }
    }
}
