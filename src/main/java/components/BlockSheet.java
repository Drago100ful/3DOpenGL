package components;

import org.joml.Vector2f;
import ruby.renderer.Texture;

import java.util.ArrayList;
import java.util.List;

public class BlockSheet {

    private Texture texture;
    private List<Block> blocks;


    public BlockSheet(Texture texture, int blockWidth, int blockHeight, int numBlocksFaces, int blockSpacing) {
        this.blocks = new ArrayList<>();
        this.texture = texture;

        int currentX = 0;
        int currentY = texture.getHeight() - blockHeight; // BL Corner of TL Block

        // Generate UV NDC
        for (int i = 0; i < numBlocksFaces / 6; i += 6) {
            Vector2f[] uv = new Vector2f[24];

            for (int f = 0; f < 6; ++f) {
                float topY = (currentY + blockHeight) / (float) texture.getHeight();
                float rightX = (currentX + blockWidth) / (float) texture.getWidth();
                float leftX = currentX / (float) texture.getWidth();
                float bottomY = currentY / (float) texture.getHeight();

                uv[4 * f] = new Vector2f(rightX, topY);
                uv[4 * f + 1] = new Vector2f(rightX, bottomY);
                uv[4 * f + 2] = new Vector2f(leftX, bottomY);
                uv[4 * f + 3] = new Vector2f(leftX, topY);

                currentX += blockWidth + blockSpacing;
                if (currentX >= texture.getWidth()) {
                    currentX = 0;
                    currentY -= blockHeight + blockSpacing;
                }
            }
            blocks.add(new Block(texture, uv));
        }
    }

    public Block getBlock(int index) {
        return blocks.get(index);
    }
}
