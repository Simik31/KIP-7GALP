package cz.osu;

public class Point3D {

    public double x, y, z;

    public Point3D(){
        this(0, 0, 0);
    }

    public Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D clone(){
        return new Point3D(x, y, z);
    }

    public Point3D getRoundedPoint(){
        return new Point3D((int)Math.round(x), (int)Math.round(y), (int)Math.round(z));
    }
}
