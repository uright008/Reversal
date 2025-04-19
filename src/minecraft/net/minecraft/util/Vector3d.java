package net.minecraft.util;

public class Vector3d
{
    public double x;
    public double y;
    public double z;

    public Vector3d() {
        this.x = this.y = this.z = 0.0;
    }

    public Vector3d(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3d(final Vec3 vec3) {
        this.x = vec3.xCoord;
        this.y = vec3.yCoord;
        this.z = vec3.zCoord;
    }

    public Vector3d add(final double x, final double y, final double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d add(final Vector3d vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public Vector3d subtract(final double x, final double y, final double z) {
        return add(-x, -y, -z);
    }

    public Vector3d subtract(final Vector3d vector) {
        return add(-vector.x, -vector.y, -vector.z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3d multiply(final double v) {
        return new Vector3d(x * v, y * v, z * v);
    }
}
