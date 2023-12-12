package ruby.camera;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ruby.Window;

public class Camera {

    private final Matrix4f transformMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;
    private final Vector3f position;
    private final Vector3f scale;
    private final Vector3f eye;
    private final Vector3f center;
    private final Vector3f up;
    private final float fov = 65.0f;
    private final float nearPlane = 0.01f;
    private final float farPlane = 10000f;
    private Vector2f angle;
    private float rotation = 0;

    public Camera(Vector3f position) {
        this.position = position;
        this.scale = new Vector3f(1, 1, 1);
        this.transformMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();
        this.eye = new Vector3f(0, 0, 20);
        this.center = new Vector3f(0, 0, 0);
        this.up = new Vector3f(0, 1, 0);
        this.angle = new Vector2f(0, 0);
        adjustProjection();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public Vector2f getAngle() {
        return angle;
    }

    public void setAngle(Vector2f angle) {
        this.angle = angle;
    }

    public void updateAngle(Vector2f mouse) {
        angle.add(mouse.mul(0.5F));

        if (Math.abs(angle.y) >= 90) {
            angle.y = Math.signum(angle.y) * 90;
        }

        angle.x = angle.x % 360;
        angle.y = angle.y % 360;

//        System.out.println("X: " + angle.x + " | Y: " + angle.y);
    }

    public Matrix4f getViewMatrix() {
        viewMatrix.identity().rotateX((float) Math.toRadians(-angle.y)).rotateY((float) Math.toRadians(-angle.x)).translate(0, -5, 0).lookAt(eye, center, up);

        return viewMatrix;
    }

    public Matrix4f getTransformationMatrix() {
        transformMatrix.identity();
        transformMatrix.scale(scale).translate(position);

        return transformMatrix;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void translatePosition(Vector3f vector) {

        if (vector.z != 0) {
            position.x -= (float) Math.sin(Math.toRadians(-angle.x)) * vector.z;
            position.z += (float) Math.cos(Math.toRadians(-angle.x)) * vector.z;
        }

        if (vector.x != 0) {
            position.x -= (float) Math.sin(Math.toRadians(-angle.x - 90)) * vector.x;
            position.z += (float) Math.cos(Math.toRadians(-angle.x - 90)) * vector.x;
        }

        position.y += vector.y;

    }

    public void adjustProjection() {
        projectionMatrix.identity();

        float windowAspect = ((float) Window.getWidth() / (float) Window.getHeight());
        projectionMatrix.perspective((float) Math.toRadians(fov), windowAspect, nearPlane, farPlane);
    }
}
