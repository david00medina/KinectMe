package object.instrument;

import object.InteractiveVolume;
import object.Material;
import object.Object;
import object.Texture;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Guitar extends Object {
    private boolean doDrawInteractions;
    private boolean take = false;
    private boolean play = false;

    public Guitar(PApplet parent, PShape model, Texture texture, Material material) {
        super(parent, model, texture, material);
    }

    public void touched(int id, PVector joint, int inRadius) {
        for (InteractiveVolume iv :
                interactions) {
            if (iv.isColliding(id, joint, inRadius)) {

                if ("NECK".equals(iv.getId())) {
                    if (iv.isTouchedLeft())
                        iv.setContactPoint(joint.copy());
                    else if (iv.isTouchedRight())
                        iv.setContactPoint(joint.copy());

                    take = true;
                }

            } else {
                if ("NECK".equals(iv.getId()) && joint != null) {
                    if (take && !iv.isTouchedLeft()) {
                        moveToJoint(iv, joint);
                        take = false;

                    } else if (take && !iv.isTouchedRight()) {
                        moveToJoint(iv, joint);
                        take = false;
                    }
                }
            }
        }
    }

    public void moveToJoint(InteractiveVolume iv, PVector joint) {
        System.out.println("HAND : " + joint);
        System.out.println("CONTACT PREV : " + iv.getContactPoint());
        PVector dist = joint.copy().sub(iv.getContactPoint());
        System.out.println("DIST : " + dist);
        this.setPos(pos.add(dist));
        System.out.println("GUITAR : " + pos);
        iv.setContactPoint(joint);
        System.out.println("CONTACT POST : " + iv.getContactPoint());
    }

    @Override
    public void drawInteractionVolume() {
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

    public boolean isTake() {
        return take;
    }

    public boolean isPlay() {
        return play;
    }
}
