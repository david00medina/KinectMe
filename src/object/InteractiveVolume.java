package object;

import algorithms.Transformation;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class InteractiveVolume {
    private PApplet parent;
    private PVector pos;
    private PVector rotation;

    public float width;
    public float height;
    public float depth;
    private float radius;

    private PShape volume;

    public InteractiveVolume(PApplet parent, int x, int y, int z, float width, float height, float depth) {
        this.parent = parent;
        pos = new PVector(x, y, z);
        rotation = new PVector(0,0,0);

        this.width = width;
        this.height = height;
        this.depth = depth;
        generateBoxVolume();
    }

    public InteractiveVolume(PApplet parent, int x, int y, int z, float radius) {
        this.parent = parent;
        pos = new PVector(x, y, z);
        rotation = new PVector(0,0,0);
        this.radius = radius;
        generateBoxVolume();
    }

    public PVector getCentroid() {
        float x = 0.f, y = .0f, z = .0f;
        List<PVector> faceMassCenter = new ArrayList<>();
        int vertexCount = volume.getVertexCount();
        for (int i = 0; i < volume.getVertexCount(); i++) {
            PVector v = volume.getVertex(i);
            if ((i+1) % 4 != 0) {
                x += v.x;
                y += v.y;
                z += v.z;
            } else {
                x += v.x;
                y += v.y;
                z += v.z;

                if (i != 0) faceMassCenter.add(new PVector(x / 4, y / 4, z / 4));

                x = v.x;
                y = v.y;
                z = v.z;
            }
        }

        x = 0.f;
        y = .0f;
        z = .0f;
        for (PVector v :
                faceMassCenter) {
            x += v.x;
            y += v.y;
            z += v.z;
        }
        parent.strokeWeight(10);
        parent.point(x / faceMassCenter.size(), y / faceMassCenter.size(), z / faceMassCenter.size());
        return new PVector(x / faceMassCenter.size(), y / faceMassCenter.size(), z / faceMassCenter.size());
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

    public PShape generateBoxVolume() {
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

        return volume;
    }

    public PShape translate(int x, int y, int z) {
        pos.x = x;
        pos.y = y;
        pos.z = z;

        for (int i = 0; i < volume.getVertexCount(); i++) {
            PVector vTranslated = Transformation.translate(volume.getVertex(i), pos.x, pos.y, pos.z);
            volume.setVertex(i, vTranslated);
        }
        return volume;
    }

    public PShape rotateX(float theta) {
        rotation.x = theta;

        for (int i = 0; i < volume.getVertexCount(); i++) {
            PVector vRotated = Transformation.rotateX(volume.getVertex(i), rotation.x);
            pos.x = vRotated.x;
            pos.y = vRotated.y;
            pos.z = vRotated.z;
            volume.setVertex(i, vRotated);
        }
        return volume;
    }

    public PShape rotateY(float theta) {
        rotation.y = theta;

        for (int i = 0; i < volume.getVertexCount(); i++) {
            PVector vRotated = Transformation.rotateY(volume.getVertex(i), rotation.y);
            pos.x = vRotated.x;
            pos.y = vRotated.y;
            pos.z = vRotated.z;
            volume.setVertex(i, vRotated);
        }
        return volume;
    }

    public PShape rotateZ(float theta) {
        rotation.z = theta;
        for (int i = 0; i < volume.getVertexCount(); i++) {
            PVector vRotated = Transformation.rotateZ(volume.getVertex(i), rotation.z);
            pos.x = vRotated.x;
            pos.y = vRotated.y;
            pos.z = vRotated.z;
            volume.setVertex(i, vRotated);
        }
        return volume;
    }

    public PShape rotate(float thetaX, float thetaY, float thetaZ) {
        volume = rotateX(thetaX);
        volume = rotateY(thetaY);
        volume = rotateZ(thetaZ);
        return volume;
    }

    public void drawInteractionVolume() {
        parent.pushMatrix();
        parent.translate(pos.x, pos.y, pos.z);
        translate((int) pos.x, (int)  pos.y, (int)  pos.z);
        parent.rotateX(rotation.x);
//        rotateX(rotation.x);
        parent.rotateY(rotation.y);
//        rotateY(rotation.y);
        parent.rotateZ(rotation.z);
        rotateZ(rotation.z);
        parent.shape(volume);
        parent.popMatrix();
    }
}
