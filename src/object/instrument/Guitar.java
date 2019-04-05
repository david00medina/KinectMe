package object.instrument;

import kinect4WinSDK.SkeletonData;
import object.InteractiveVolume;
import object.Material;
import object.Object;
import object.Texture;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import soundFX.OscillatorSelector;
import soundFX.SoundFX;

public class Guitar extends Object {
    private boolean doDrawInteractions;
    private PVector joint;
    private boolean takeLeft = false;
    private boolean takeRight = false;
    private SoundFX soundFX;

    public Guitar(PApplet parent, PShape model, Texture texture, Material material) {
        super(parent, model, texture, material);
        soundFX = new SoundFX(parent);
    }

    public void touched(int id, PVector joint, int inRadius) {
        for (InteractiveVolume iv :
                interactions) {
            if (iv.isColliding(id, joint, inRadius)) {

                if ("NECK".equals(iv.getId())) {
                    if (iv.isTouchedLeft() && id == SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT) {
                        iv.setContactPoint(joint.copy());
                        takeLeft = true;
                    }
                    else if (iv.isTouchedRight() && id == SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT) {
                        iv.setContactPoint(joint.copy());
                        takeRight = true;
                    }

                } else if ("STRINGS".equals(iv.getId())) {
                    soundFX.play(OscillatorSelector.TRIANGULAR, joint.y);
                }

            } else {
                if ("NECK".equals(iv.getId()) && joint != null) {

                    if (takeLeft && id == SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT) {
                        if (takeRight)
                            takeRight = false;

                        this.joint = joint;
                        moveToJoint(iv, joint);

                    } else if (takeRight && id == SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT) {
                        if (takeLeft)
                            takeLeft = false;
                        this.joint = joint;
                        moveToJoint(iv, joint);
                    }
                } else if ("STRINGS".equals(iv.getId())) {
                    soundFX.stop();
                }
            }
        }
    }

    private void moveToJoint(InteractiveVolume iv, PVector joint) {
        /*System.out.println("HAND : " + joint);
        System.out.println("CONTACT PREV : " + iv.getContactPoint());*/
        PVector dist = joint.copy().sub(iv.getContactPoint());
//        System.out.println("DIST : " + dist);
        this.setPos(pos.add(dist));
//        System.out.println("GUITAR : " + pos);
        iv.setContactPoint(joint);
//        System.out.println("CONTACT POST : " + iv.getContactPoint());
    }

    @Override
    public void updateGuitarState() {
        if (doDrawInteractions) {
            for (InteractiveVolume iv :
                    interactions) {
                iv.refresh();
            }
        }
    }

    @Override
    public void doDrawInteractionArea(boolean b) {
        doDrawInteractions = b;
    }
}
