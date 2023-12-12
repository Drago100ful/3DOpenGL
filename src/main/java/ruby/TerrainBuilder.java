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
        return new Chunk(true );
    }

}
