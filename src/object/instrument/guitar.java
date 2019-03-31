package object.instrument;

import algorithms.Transformation;
import kinect4WinSDK.SkeletonData;
import object.InteractiveVolume;
import object.Material;
import object.Object;
import object.Texture;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Guitar extends Object {
    private boolean doDrawInteractions;
    private boolean istouchedRight = false;
    private boolean istouchedLeft = false;

    public Guitar(PApplet parent, PShape model, Texture texture, Material material) {
        super(parent, model, texture, material);
    }

    public boolean touched(int id, PVector joint, int inRadius) {
        for (InteractiveVolume iv :
                interactions) {
            iv.setRGB(255, 0, 0);

            if (iv.isColliding(joint, inRadius)) {
                if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT) {
                    istouchedLeft = true;
                } else if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT) {
                    istouchedRight = true;
                }
            } else {
                if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT) {
                    istouchedLeft = false;
                } else if (id == SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT) {
                    istouchedRight = false;
                }
            }

            if (istouchedLeft || istouchedRight) {
                iv.setRGB(0, 255, 0);
                return true;
            }
        }

        return false;
    }

    // TODO: Temporal method
    private void visualizeVertexAndHand(PVector joint, PVector v) {
        parent.pushStyle();
        parent.strokeWeight(10);
        parent.stroke(0, 0, 255);
        // Vertices
        parent.point(v.x, v.y, v.z);
        // Hands
        if (joint != null) parent.point(joint.x, joint.y, joint.z);
        parent.popStyle();
    }

    @Override
    public void drawInteractionVolume() {
        if (doDrawInteractions) {
            for (InteractiveVolume iv :
                    interactions) {
//                iv.setRotation(PApplet.radians(0), PApplet.radians(0), PApplet.map(parent.mouseX, 0, parent.width, 0, PConstants.TWO_PI));
                iv.drawInteractionVolume();
            }
        }
    }

    @Override
    public void doDrawInteractionArea(boolean b) {
        doDrawInteractions = b;
    }
}
