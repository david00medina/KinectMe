package object;

import algorithms.Transformation;
import kinect4WinSDK.SkeletonData;
import processing.core.*;

public class InteractiveVolume {
    private PApplet parent;
    private String id;
    private PVector pos;
    private PVector rotation;
    private PVector translationOffset;
    private PVector rotationOffset;

    private PVector centroid;
    public float width;
    public float height;
    private float depth;
    private float radius;


    private boolean isTouchedLeft = false;
    private boolean isTouchedRight = false;
    private PVector contactPoint;


    private PShape volume;
    private int[] rgb;
    private boolean watchVertices = false;

    public InteractiveVolume(PApplet parent, String id, float width, float height, float depth) {
        this.parent = parent;
        this.id = id;

        pos = new PVector();
        rotation = new PVector();

        this.width = width;
        this.height = height;
        this.depth = depth;

        translationOffset = new PVector();
        rotationOffset = new PVector();

        generateBoxVolume();
        getCentroid();

        contactPoint = new PVector();
    }

    public String getId() {
        return id;
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(float x, float y, float z) {
        pos = new PVector(x + translationOffset.x,
                y + translationOffset.y,
                z + translationOffset.z);
    }

    public PVector getRotation() {
        return rotation;
    }

    public void setRotation(float thetaX, float thetaY, float thetaZ) {
        rotation = new PVector(thetaX + rotationOffset.x,
                thetaY + rotationOffset.y,
                thetaZ + rotationOffset.z);
    }

    public PVector getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(PVector contactPoint) {
        this.contactPoint = contactPoint;
    }

    public boolean isTouchedLeft() {
        return isTouchedLeft;
    }

    public boolean isTouchedRight() {
        return isTouchedRight;
    }

    private void generateBoxVolume() {
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
    }

    public void refresh() {
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

    private PVector getCentroid() {
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

        Transformation.translate(centroid, pos.x, pos.y, pos.z);
        return centroid;
    }

    public boolean isColliding(int id, PVector joint, int inRadius) {
        if (joint == null || volume.getVertexCount() <= 0) return isTouched(id, false);

        int totalVertices = volume.getVertexCount();
        float[] x = new float[totalVertices];
        float[] y = new float[totalVertices];
        float[] z = new float[totalVertices];

        float xmax, xmin, ymax, ymin, zmax, zmin;

        for (int i = 0; i < totalVertices; i++) {
            PVector v = volume.getVertex(i).copy();

            centroid = getCentroid();

            v = Transformation.translate(v, pos.x, pos.y, pos.z);
            v = Transformation.superRotation(v.x, v.y, v.z, centroid.x, centroid.y, centroid.z, 0, 0, 1, rotation.z);

            parent.pushStyle();
            parent.strokeWeight(10);
            parent.stroke(255, 0, 0);
            parent.point(centroid.x, centroid.y, centroid.z);

            if (watchVertices) visualizeVertexAndHand(joint, v);

            parent.popStyle();

            x[i] = v.x;
            y[i] = v.y;
            z[i] = v.z;
        }

        xmin = PApplet.min(x);
        xmax = PApplet.max(x);
        ymin = PApplet.min(y);
        ymax = PApplet.max(y);
        zmin = PApplet.min(z);
        zmax = PApplet.max(z);

        return isTouched(id,
                (joint.x >= xmin && joint.x <= xmax) &&
                (joint.y >= ymin && joint.y <= ymax) &&
                (joint.z >= zmin && joint.z <= zmax));
    }

    private boolean isTouched(int id, boolean isColliding) {
        switch (id) {
            case SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT:
                isTouchedLeft = isColliding;
                break;
            case SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT:
                isTouchedRight = isColliding;
                break;
            default:
                break;
        }

        if (isTouchedLeft || isTouchedRight) rgb = new int[]{ 0, 255, 0 };
        else rgb = new int[]{ 255, 0, 0 };

        return isColliding;
    }

    private void visualizeVertexAndHand(PVector joint, PVector v) {
        parent.stroke(0, 0, 255);
        parent.point(v.x, v.y, v.z);
        parent.point(centroid.x, centroid.y, centroid.z);
        // Hands
        if (joint != null) parent.point(joint.x, joint.y, joint.z);
    }

    public void setVisualizeVertices(boolean b) {
        this.watchVertices = b;
    }

    public void setTranslationOffset(float x, float y, float z) {
        translationOffset = new PVector(x, y, z);
    }

    public PVector getTranslationOffset() {
        return translationOffset;
    }

    public  void setRotationOffset(float x, float y, float z) {
        rotationOffset = new PVector(x, y, z);
    }

    public PVector getRotationOffset() {
        return rotationOffset;
    }
}
