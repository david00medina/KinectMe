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

    public static PVector superRotation(float x, float y, float z, float a, float b, float c, float u, float v, float w, float theta) {
        PVector o = new PVector();

        o.x = (a * (v*v + w*w) - u * (b*v + c*w - u*x - v*y - w*z)) * (1 - PApplet.cos(theta)) + x*PApplet.cos(theta) + (-c*v + b*w - w*y + v*z) * PApplet.sin(theta);
        o.y = (b * (u*u + w*w) - v * (a*u + c*w - u*x - v*y - w*z)) * (1 - PApplet.cos(theta)) + y*PApplet.cos(theta) + (c*u - a*w + w*x - u*z) * PApplet.sin(theta);
        o.z = (c * (u*u + v*v) - w * (a*u + b*v - u*x - v*y - w*z)) * (1 - PApplet.cos(theta)) + z*PApplet.cos(theta) + (-b*u + a*v - v*x + u*y) * PApplet.sin(theta);

        return o;
    }
}
