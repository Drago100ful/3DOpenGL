package ruby;

public class TerrainBuilder {

    private static TerrainBuilder singleton = null;

    public TerrainBuilder() {}

    public static TerrainBuilder get() {
        if (singleton == null) {
            singleton = new TerrainBuilder();
        }

        return singleton;
    }

    public static Chunk generateChunk(int x, int z) {
        long timeStarted = System.nanoTime();
        Chunk chunk = new Chunk(true);
        chunk.setX(x);
        chunk.setZ(z);
        System.out.printf("Chunk creation took: %fs%n", (float) ((System.nanoTime()-timeStarted)*1E-9));
        return chunk;
    }
}
