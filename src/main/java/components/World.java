package components;

public class World {

    private final Block[][][] blockRenderers;

    public World(int size) {
        this.blockRenderers = new Block[size][128][size];
    }

    public void setBlock(Block block, int x, int y, int z) {
        blockRenderers[x][y][z] = block;
    }

    public Block getBlock(int x, int y, int z) {
        return blockRenderers[x][y][z];
    }
}
