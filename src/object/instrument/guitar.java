package object.instrument;

import algorithms.Transformation;
import kinect4WinSDK.SkeletonData;
import object.InteractiveVolume;
import object.Material;
import object.Object;
import object.Texture;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PConstants.BOX;

public class Guitar extends Object {
    private boolean doDrawInteractions;
    private List<PShape> iVolumes;
    private boolean istouchedRight = false;
    private boolean istouchedLeft = false;
    private int[] rgb = { 255, 0, 0 };

    public Guitar(PApplet parent, PVector pos, PVector rotation, PShape model, Texture texture, Material material) {
        super(parent, pos, rotation, model, texture, material);
        iVolumes = new ArrayList<>();
    }

    public Guitar(PApplet parent, PVector pos, PShape model, Material material) {
        super(parent, pos, model, material);
        iVolumes = new ArrayList<>();
    }

    public boolean touched(int id, PVector joint) {
        for (PShape s :
                iVolumes) {
            for (int i = 0; i < s.getVertexCount(); i++) {
                PVector v = s.getVertex(i);

                // TODO: Temporal Method
                //visualizeVertexAndHand(joint, v);

                if (v != null && joint != null && PApplet.dist(v.x, v.y, joint.x, joint.y) <= 30) {
                    if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT) {
                        istouchedLeft = true;
                    } else if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT) {
                        istouchedRight = true;
                    }

                    if (istouchedLeft || istouchedRight) rgb = new int[]{ 0, 255, 0 };

                    return true;
                }
            }
        }
        if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT) {
            istouchedLeft = false;
        } else if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT) {
            istouchedRight = false;
        }

        if (!istouchedLeft && !istouchedRight) rgb = new int[]{ 255, 0, 0 };

        return false;
    }

    // TODO: Temporal method
    private void visualizeVertexAndHand(PVector joint, PVector v) {
        parent.pushStyle();
        parent.strokeWeight(15);
        parent.stroke(0, 0, 255);
        // Vertices
        parent.point(v.x, v.y, v.z);
        // Hands
        if (joint != null) parent.point(joint.x, joint.y, joint.z);
        parent.popStyle();
    }

    @Override
    public void drawInteractionVolume() {
//        parent.pushMatrix();
        if (doDrawInteractions) {
            for (InteractiveVolume iv :
                    interactions) {
//                parent.translate(iv.x, iv.y, iv.z);

                parent.pushStyle();
                parent.stroke(0, 0, 0);
                parent.fill(rgb[0], rgb[1], rgb[2], 128);
//                parent.box(iv.width, iv.height, iv.depth);

//                PShape iVolume = parent.createShape(BOX, iv.width, iv.height, iv.depth);

                /*for (int i = 0; i < iVolume.getVertexCount(); i++) {
                    PVector v = Transformation.translate(iVolume.getVertex(i), iv.x, iv.y, iv.z);
                    v = Transformation.rotateX(v, rotation.x);
                    v = Transformation.rotateY(v, rotation.y);
                    v = Transformation.rotateZ(v, rotation.z);
                    iVolume.setVertex(i, v);
                    // TODO: Temporal Method
                    visualizeVertexAndHand(null, v);
                }*/
                parent.strokeWeight(20);
                iv.setRotation(PApplet.radians(0), PApplet.radians(0), PApplet.map(parent.mouseX, 0, parent.width, 0, PConstants.TWO_PI));
                parent.line(0,0,0,
                        iv.getPos().x,
                        iv.getPos().y,
                        iv.getPos().z);

                PShape iVolume = iv.generateBoxVolume();
//                iv.translate((int) pos.x, (int) pos.y, (int) pos.z);
//                iv.translate((int) pos.x, (int) pos.y, (int) pos.z);
//                iv.rotateX(PApplet.map(parent.mouseX, .0f, parent.width, 0, PApplet.TWO_PI));

//                iv.rotate((int) rotation.x, (int) rotation.y, (int) rotation.z);

                iVolumes.add(iVolume);

                /*iVolume.rotateX(rotation.x);
                iVolume.rotateY(rotation.y);
                iVolume.rotateZ(rotation.z);*/
                iv.drawInteractionVolume();

                parent.popStyle();
            }
        }
//        parent.popMatrix();
    }

    @Override
    public void doDrawInteractionArea(boolean b) {
        doDrawInteractions = b;
    }
}
