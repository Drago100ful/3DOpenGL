package ruby.renderer;

import components.BlockRenderer;
import ruby.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH = 1000;
    private final List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        BlockRenderer block = go.getComponent(BlockRenderer.class);
        if (block != null) {
            add(block);
        }
    }

    private void add(BlockRenderer blockRenderer) {

        boolean added = false;

        for (RenderBatch batch : batches) {
            if (batch.hasRoom()) {
                batch.addBlock(blockRenderer);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH);
            newBatch.addBlock(blockRenderer);
            newBatch.start();
            batches.add(newBatch);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
