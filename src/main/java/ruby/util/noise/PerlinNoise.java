package ruby.util.noise;

import javax.swing.*;
import java.awt.*;

public class PerlinNoise {

    public float[] noise1D(int count, float[] seed, int octaves) {
        float[] output = new float[count];

        for (int x = 0; x < count; ++x) {
            float noise = 0.0f;
            float scale = 1.0f;
            float scaledBy = 0.0f;

            for (int o = 0; o < octaves; ++o) {
                int pitch = count >> o;

                int sample1 = (x / pitch) * pitch;
                int sample2 = (sample1 + pitch) % count;

                float blend = (float) (x - sample1) / (float) pitch;
                float sample = (1.0f - blend) * seed[sample1] + blend * seed[sample2];

                noise += (sample * scale);

                scaledBy += scale;
                scale /= 2.0f;
            }

            output[x] = (noise / scaledBy);
        }

        return output;
    }

    public float[] noise2D(int width, int height, float[] seed, int octaves, float bias) {
        float[] output = new float[width * height];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float noise = 0.0f;
                float scale = 1.0f;
                float scaledBy = 0.0f;

                for (int o = 0; o < octaves; ++o) {
                    int pitch = width >> o;

                    int sampleX1 = (x / pitch) * pitch;
                    int sampleY1 = (y / pitch) * pitch;

                    int sampleX2 = (sampleX1 + pitch) % width;
                    int sampleY2 = (sampleY1 + pitch) % width;

                    float blendX = (float) (x - sampleX1) / (float) pitch;
                    float blendY = (float) (y - sampleY1) / (float) pitch;


                    float sampleX = (1.0f - blendX) * seed[sampleY1 * width + sampleX1] + blendX * seed[sampleY1 * width + sampleX2];
                    float sampleY = (1.0f - blendX) * seed[sampleY2 * width + sampleX1] + blendX * seed[sampleY2 * width + sampleX2];

                    noise += ((blendY * (sampleY - sampleX) + sampleX) * scale);

                    scaledBy += scale;
                    scale = scale / bias;
                }

                output[y * width + x] = (noise / scaledBy);
            }
        }

        return output;
    }

    public void visualize(float[] arr, int size, int dim) {
        TestWindow testWindow = new TestWindow(arr, size, dim);
    }

}


class TestWindow extends JFrame {
    private final float[] arr;
    private final int dimension;
    private final int size;

    public TestWindow(float[] arr, int size, int dimension) {
        this.setPreferredSize(new Dimension(size, size));
        this.pack();
        this.arr = arr;
        this.dimension = dimension;
        this.size = size;
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        switch (dimension) {
            case 1 -> {
                for (int x = 0; x < size; ++x) {
                    g.drawLine(x, this.getHeight() / 2, x, (int) (this.getHeight() / 2.0f - (this.getHeight() / 2.0f) * arr[x]));
                }
            }
            case 2 -> {
                for (int x = 0; x < size; ++x) {
                    for (int y = 0; y < size; ++y) {
                        g.setColor(new Color(arr[y * 256 + x], arr[y * 256 + x], arr[y * 256 + x]));
                        g.drawLine(x, y, x, y);
                    }
                }
            }
            default -> throw new RuntimeException("Unsupported dimension");
        }
    }
}

