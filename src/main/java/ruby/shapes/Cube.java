package ruby.shapes;

import org.joml.Vector3f;

public class Cube {

    private final Vector3f size;


    private float[] vertexArray;

    private final int[] elementArray = {
            // Back
            5, 6, 4,
            7, 5, 4,
            // Bottom
            15, 14, 13,
            15, 13, 12,
            // Left
            23, 22, 20,
            23, 20, 21,
            // Right
            19, 18, 16,
            19, 16, 17,
            // Top
            9, 10, 11,
            9, 11, 8,
            // Front
            3, 2, 0,
            0, 2, 1

    };

    public Cube() {
        size = new Vector3f(1, 1, 1);
        createVertexArray();
    }

    public Cube(Vector3f size) {
        this.size = size;
        createVertexArray();
    }

    public int[] getElementArray() {
        return elementArray;
    }

    public float[] getVertexArray() {
        return vertexArray;
    }

    private void createVertexArray() {
        vertexArray = new float[] {
                // POS  XYZ             // COLOR                               //UV
                size.x / 2, size.y / 2,   size.z / 2,   0.0f, 0.0f, 1.0f, 1.0f, 1/3f,  1.00f, //FTR
                size.x / 2, -size.y / 2,  size.z / 2,   1.0f, 0.0f, 0.0f, 1.0f, 1/3f,  2/3f, //FBR
                -size.x / 2, -size.y / 2, size.z / 2,   1.0f, 1.0f, 0.0f, 1.0f, 0.00f, 2/3f, //FBL
                -size.x / 2, size.y / 2,  size.z / 2,   0.0f, 1.0f, 0.0f, 1.0f, 0.00f, 1.00f, //FTL

                size.x / 2, size.y / 2,  -size.z / 2,   0.0f, 0.0f, 1.0f, 1.0f, 2/3f,  1.00f,  //BTR
                size.x / 2, -size.y / 2, -size.z / 2,   1.0f, 0.0f, 0.0f, 1.0f, 2/3f,  2/3f, //BBR
                -size.x / 2, -size.y / 2,-size.z / 2,   1.0f, 1.0f, 0.0f, 1.0f, 1/3f,  2/3f,  //BBL
                -size.x / 2, size.y / 2, -size.z / 2,   0.0f, 1.0f, 0.0f, 1.0f, 1/3f,  1.00f, //BTL

                size.x / 2, size.y / 2,  -size.z / 2,   0.0f, 0.0f, 1.0f, 1.0f, 2/3f,  2/3f,  //TTR
                size.x / 2, size.y / 2,   size.z / 2,   0.0f, 0.0f, 1.0f, 1.0f, 2/3f,  1/3f,  //TBR
                -size.x / 2, size.y / 2,  size.z / 2,   0.0f, 1.0f, 0.0f, 1.0f, 1/3f,  1/3f, //TBL
                -size.x / 2, size.y / 2, -size.z / 2,   0.0f, 1.0f, 0.0f, 1.0f, 1/3f,  2/3f,//TTL

                size.x / 2,  -size.y / 2, -size.z / 2,  0.0f, 0.0f, 1.0f, 1.0f, 1.0f,  2/3f,  //DTR
                size.x / 2,  -size.y / 2,  size.z / 2,  0.0f, 0.0f, 1.0f, 1.0f, 1.0f,  1/3f,  //DBR
                -size.x / 2, -size.y / 2,  size.z / 2,  0.0f, 1.0f, 0.0f, 1.0f, 2/3f,  1/3f, //DBL
                -size.x / 2, -size.y / 2, -size.z / 2,  0.0f, 1.0f, 0.0f, 1.0f, 2/3f,  2/3f, //DTL

                size.x / 2, size.y / 2, -size.z / 2,   0.0f, 0.0f, 1.0f, 1.0f, 1/3f,   2/3f,  //RTR
                size.x / 2, -size.y / 2,-size.z / 2,   1.0f, 0.0f, 0.0f, 1.0f, 1/3f,   1/3f, //RBR
                size.x / 2, -size.y / 2, size.z / 2,   1.0f, 0.0f, 0.0f, 1.0f, 0.00f,  1/3f,  //RBL
                size.x / 2, size.y / 2,  size.z / 2,   0.0f, 0.0f, 1.0f, 1.0f, 0.00f,  2/3f,  //RTL

                -size.x / 2, size.y / 2, size.z / 2,   0.0f, 1.0f, 0.0f, 1.0f, 1.00f,  1.00f, //LTR
                -size.x / 2, -size.y / 2, size.z / 2,  1.0f, 1.0f, 0.0f, 1.0f, 1.00f,  2/3f,//LBR
                -size.x / 2, -size.y / 2, -size.z / 2, 1.0f, 1.0f, 0.0f, 1.0f, 2/3f,   2/3f, //LBL
                -size.x / 2, size.y / 2, -size.z / 2,  0.0f, 1.0f, 0.0f, 1.0f, 2/3f,   1.00f,  //LTL
        };
    }


}
