package br.pucpr.cg;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
public class Camera {
    private Vector3f position = new Vector3f(0,0,2);
    private Vector3f direction = new Vector3f(0,-1,-1);
    //private Vector3f target = new Vector3f(0, 0, -1);
    private Vector3f up = new Vector3f(0, 1, 0);

    private float fovy = (float)Math.toRadians(60);
    private float near = 0.1f;
    private float far = 1000.0f;

    public void moveFront(float distance) {
        position.add(new Vector3f(direction).normalize().mul(distance));
    }

    public void strafeLeft(float distance) {
        Vector3f left = new Vector3f(direction);
        left.cross(up).normalize().mul(Math.abs(distance));
        position.sub(left);

    }

    public void strafeRight(float distance) {
        Vector3f right = new Vector3f(direction);
        right.cross(up).normalize().mul(Math.abs(distance));
        position.add(right);

    }

    public void rotateX (float angle) {
        Vector3f vector = new Vector3f(direction).cross(up).normalize().mul(angle);
        new Matrix3f().rotateXYZ(vector.x, vector.y, vector.z).transform(direction);
    }

    public void rotate (float angle) {
        Vector3f vector = new Vector3f(up).mul(angle);
        new Matrix3f().rotateXYZ(vector.x, vector.y, vector.z).transform(direction);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f value) {
        position = value;
    }
    /*public Vector3f getTarget() {
        return target;
    }*/

    public Vector3f getUp() {
        return up;
    }

    public float getFovy() {
        return fovy;
    }

    public void setFovy(float fovy) {
        this.fovy = fovy;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getAspect() {
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);

        long window = glfwGetCurrentContext();
        glfwGetWindowSize(window, w, h);

        return w.get() / (float) h.get();
    }


    public Matrix4f getViewMatrix() {
        Vector3f target = new Vector3f(direction).add(position);
        return new Matrix4f().lookAt(position, target, up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(fovy, getAspect(), near, far);
    }
}
