package main;

import algorithms.Transformation;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import kinect.Kinect;
import kinect.KinectAnathomy;
import kinect.KinectSelector;
import kinect4WinSDK.SkeletonData;
import object.InteractiveVolume;
import object.instrument.Guitar;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.MouseEvent;

public class KinectMe extends PApplet {
    private static final boolean DEBUG_AREAS = true;
    private static final boolean DEBUG_VERTICES = true;
    private Kinect kinect;
    private Guitar guitar;
    private int mouseWheel;

    @Override
    public void settings() {
        super.settings();
        size(640, 480, P3D);
    }

    @Override
    public void setup() {
        super.setup();
        smooth();
        stroke(255);

        kinect = new Kinect(this, null, null, null);

        spawnGuitar();
    }

    private void spawnGuitar() {
        PShape guitarModel = loadShape("../../data/models/guitar/guitar.obj");
        guitar = new Guitar(this, guitarModel, null, null);

        guitar.setPos(new PVector(70, 420, 15));
        guitar.setRotation(new PVector(radians(0), radians(0), radians(-120)));
        guitar.scale(55.f);

        float xOffsetNeck = 5;
        float yOffsetNeck = -80;
        float zOffsetNeck = 4;
        float xRotationOffsetNeck = radians(0);
        float yRotationOffsetNeck = radians(0);
        float zRotationOffsetNeck = radians(5);
        InteractiveVolume neck = new InteractiveVolume(this, "NECK", 50, 4, 4);
        neck.setTranslationOffset(xOffsetNeck, yOffsetNeck, zOffsetNeck);
        neck.setRotationOffset(xRotationOffsetNeck, yRotationOffsetNeck, zRotationOffsetNeck);
        neck.setPos(guitar.getPos().x + xOffsetNeck,
                guitar.getPos().y + yOffsetNeck,
                guitar.getPos().z + zOffsetNeck);
        neck.setRotation(guitar.getRotation().x + xRotationOffsetNeck,
                guitar.getRotation().y + yRotationOffsetNeck,
                guitar.getRotation().z + zRotationOffsetNeck);

        float xOffsetStrings = 35;
        float yOffsetStrings = -15;
        float zOffsetStrings = 4;
        float xRotationOffsetStrings = radians(0);
        float yRotationOffsetStrings = radians(0);
        float zRotationOffsetStrings = radians(5);
        InteractiveVolume strings = new InteractiveVolume(this, "STRINGS", 20,4,6);
        strings.setTranslationOffset(xOffsetStrings, yOffsetStrings, zOffsetStrings);
        strings.setPos(guitar.getPos().x + xOffsetStrings,
                guitar.getPos().y + yOffsetStrings,
                guitar.getPos().z + zOffsetStrings);
        strings.setRotation(guitar.getRotation().x + xRotationOffsetStrings,
                guitar.getRotation().y + yRotationOffsetStrings,
                guitar.getRotation().z + zRotationOffsetStrings);

        guitar.getInteractions().add(neck);
        guitar.getInteractions().add(strings);

        // TODO: Set true to debug
        neck.setVisualizeVertices(DEBUG_VERTICES);
        guitar.doDrawInteractionArea(DEBUG_AREAS);
    }

    @Override
    public void draw() {
        background(0);

        rotateX(radians((mouseY * 1f / height - .5f) * 180.f));
        rotateY(radians((mouseX * 1f / width - .5f) * 180.f));

        kinect.doSkeleton(true);
        kinect.refresh(KinectSelector.NONE, true);

        lights();

        PVector v = guitar.getPos();

        PVector leftHandPos = kinect.getSkelPos(KinectAnathomy.HAND_LEFT);
        PVector rightHandPos = kinect.getSkelPos(KinectAnathomy.HAND_RIGHT);
        boolean isLeftHandTouched = guitar.touched(KinectAnathomy.HAND_LEFT.getSkelId(),
                leftHandPos,
                kinect.getHandRadius());
        boolean isRightHandTouched = guitar.touched(KinectAnathomy.HAND_RIGHT.getSkelId(),
                rightHandPos,
                kinect.getHandRadius());

        if (isLeftHandTouched || isRightHandTouched) guitar.setTake(true);

        if (guitar.isTake() && leftHandPos != null) {
            PVector dist = leftHandPos.copy();
            dist.sub(guitar.getLeftContactPoint());
            guitar.setLeftContactPoint(guitar.getLeftContactPoint().add(dist));
            guitar.setPos(guitar.getPos().add(dist));

            moveGuitarInteractionVolume();

            System.out.println("dP = " + dist);
            System.out.println("CONTACT : " + guitar.getLeftContactPoint());
            System.out.println("HAND : " + leftHandPos);
            System.out.println("GUITAR = " + guitar.getPos());
        } else if (guitar.isTake() && rightHandPos != null) {
            PVector dist = rightHandPos.copy();
            dist.sub(guitar.getRightContactPoint());
            guitar.setRightContactPoint(guitar.getRightContactPoint().add(dist));
            guitar.setPos(guitar.getPos().add(dist));

            moveGuitarInteractionVolume();
        }
        guitar.refresh();
    }

    private void moveGuitarInteractionVolume() {
        for (InteractiveVolume iv :
                guitar.getInteractions()) {
            iv.setPos(guitar.getPos().x, guitar.getPos().y, guitar.getPos().z);
            PVector t = iv.getTranslationOffset();
            iv.setTranslationOffset(t.x, t.y, t.z);
        }
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);
        mouseWheel += event.getCount();
        System.out.println("WHEEL : " + mouseWheel);
    }

    public void appearEvent(SkeletonData _s) {
        kinect.appearEvent(_s);
    }

    public void disappearEvent(SkeletonData skel) {
        kinect.disappearEvent(skel);
    }

    public void moveEvent(SkeletonData _b, SkeletonData _a) {
        kinect.moveEvent(_b, _a);
    }

    public static void main(String[] args) {
        PApplet.main("main.KinectMe");
    }
}
