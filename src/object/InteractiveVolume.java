package object;

import algorithms.Transformation;
import kinect4WinSDK.SkeletonData;
import processing.core.*;

public class InteractiveVolume {
    private PApplet parent;
    private PVector pos;
    private PVector rotation;

    public PVector centroid;
    public float width;
    public float height;
    public float depth;
    private float radius;

    private PShape volume;
    private int[] rgb;

    public InteractiveVolume(PApplet parent, float width, float height, float depth) {
        this.parent = parent;
        rotation = new PVector(0,0,0);

        this.width = width;
        this.height = height;
        this.depth = depth;
        generateBoxVolume();
        getCentroid();
    }

    public PShape getVolume() {
        return volume;
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(int x, int y, int z) {
        pos = new PVector(x, y, z);
    }

    public PVector getRotation() {
        return rotation;
    }

    public void setRotation(float thetaX, float thetaY, float thetaZ) {
        rotation = new PVector(thetaX, thetaY, thetaZ);
    }

    public int[] getRGB() {
        return rgb;
    }

    public void setRGB(int r, int g, int b) {
        this.rgb = new int[]{ r, g, b };
    }

    public PShape generateBoxVolume() {
        parent.rectMode(PConstants.CENTER);
        volume = parent.createShape();

        volume.beginShape(PConstants.QUAD);
        volume.vertex(-1 * width,  1 * height,  1 * depth);
        volume.vertex(1 * width,  1 * height,  1 * depth);
        volume.vertex(1 * width, -1 * height,  1 * depth);
        volume.vertex(-1 * width, -1 * height,  1 * depth);

        volume.vertex(1 * width,  1 * height,  1 * depth);
        volume.vertex(1 * width,  1 * height, -1 * depth);
        volume.vertex(1 * width, -1 * height, -1 * depth);
        volume.vertex(1 * width, -1 * height,  1 * depth);

        volume.vertex(1 * width,  1 * height, -1 * depth);
        volume.vertex(-1 * width,  1 * height, -1 * depth);
        volume.vertex(-1 * width, -1 * height, -1 * depth);
        volume.vertex( 1 * width, -1 * height, -1 * depth);

        volume.vertex(-1 * width,  1 * height, -1 * depth);
        volume.vertex(-1 * width,  1 * height,  1 * depth);
        volume.vertex(-1 * width, -1 * height,  1 * depth);
        volume.vertex(-1 * width, -1 * height, -1 * depth);

        volume.vertex(-1 * width,  1 * height, -1 * depth);
        volume.vertex(1 * width,  1 * height, -1 * depth);
        volume.vertex(1 * width,  1 * height,  1 * depth);
        volume.vertex(-1 * width,  1 * height,  1 * depth);

        volume.vertex(-1 * width, -1 * height, -1 * depth);
        volume.vertex(1 * width, -1 * height, -1 * depth);
        volume.vertex(1 * width, -1 * height,  1 * depth);
        volume.vertex(-1 * width, -1 * height,  1 * depth);

        volume.endShape();

        parent.rectMode(PConstants.CORNER);

        return volume;
    }

    public void drawInteractionVolume() {
        parent.pushMatrix();
        parent.translate(pos.x, pos.y, pos.z);
        parent.rotateX(rotation.x);
        parent.rotateY(rotation.y);
        parent.rotateZ(rotation.z);

        if (rgb == null) rgb = new int[]{ 255, 0, 0 };
        volume.setFill(parent.color(rgb[0], rgb[1], rgb[2], 100));
        parent.shape(volume);
        parent.popMatrix();
    }

    public PVector getCentroid() {
        float x = 0;
        float y = 0;
        float z = 0;

        int total = volume.getVertexCount();
        for (int i = 0; i < total; i++) {
            PVector v = volume.getVertex(i);
            x += v.x;
            y += v.y;
            z += v.z;
        }
        centroid = new PVector(x / total, y / total, z / total);
        return centroid;
    }

    public boolean isColliding(PVector joint, int inRadius) {
        for (int i = 0; i < volume.getVertexCount(); i++) {
            PVector v = volume.getVertex(i);

            PVector centroid = this.centroid.copy();
            centroid.add(pos.x, pos.y, pos.z);
            v = Transformation.translate(v, pos.x, pos.y, pos.z);
            v = Transformation.superRotation(v.x, v.y, v.z, centroid.x, centroid.y, centroid.z, 0, 0, 1, rotation.z/*parent.map(parent.mouseX, 0, parent.width, 0, PConstants.TWO_PI)*/);

            parent.pushStyle();
            parent.strokeWeight(10);
            parent.stroke(255, 0, 0);
            parent.point(centroid.x, centroid.y, centroid.z);

            // TODO: Temporal Method
//            visualizeVertexAndHand(joint, v);
            parent.popStyle();

            if (v != null && joint != null && PApplet.dist(v.x, v.y, v.z, joint.x, joint.y, joint.z) <= inRadius) {
                return true;
            }
        }
        return false;
    }

    private void visualizeVertexAndHand(PVector joint, PVector v) {
        parent.stroke(0, 0, 255);
        // Vertices
        parent.point(v.x, v.y, v.z);
        // Hands
        if (joint != null) parent.point(joint.x, joint.y, joint.z);
    }
}
