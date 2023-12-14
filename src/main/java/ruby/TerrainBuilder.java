package ruby;

public class TerrainBuilder {

    private static TerrainBuilder singleton = null;

    public TerrainBuilder() {

    }

    public static TerrainBuilder get() {
        if (singleton == null) {
            singleton = new TerrainBuilder();
        }

        return singleton;
    }

    public static Chunk generateChunk(int x, int z) {
        Chunk chunk = new Chunk(true);
        chunk.setX(x);
        chunk.setX(z);
        return chunk;
    }

}
