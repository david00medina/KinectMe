package algorithms;

import processing.core.PApplet;
import processing.core.PVector;

public class Transformation {
    public static PVector translate(PVector v, float x, float y, float z) {
        v.x += x;
        v.y += y;
        v.z += z;
        return v;
    }

    public static PVector rotateX(PVector v, float theta) {
        v.x = v.x;
        v.y = v.y * PApplet.cos(theta) - v.z * PApplet.sin(theta);
        v.z = v.y * PApplet.sin(theta) + v.z * PApplet.cos(theta);
        return v;
    }

    public static PVector rotateY(PVector v, float theta) {
        v.x = v.x * PApplet.cos(theta) + v.z * PApplet.sin(theta);
        v.y = v.y;
        v.z = -v.x * PApplet.sin(theta) + v.z * PApplet.cos(theta);
        return v;
    }

    public static PVector rotateZ(PVector v, float theta) {
        v.x = v.x * PApplet.cos(theta) - v.y * PApplet.sin(theta);
        v.y = v.x * PApplet.sin(theta) + v.y * PApplet.cos(theta);
        v.z = v.z;
        return v;
    }
}
