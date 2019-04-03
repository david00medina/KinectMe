package main;

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

        guitar.setPos(new PVector(70, 420, 50));
        guitar.setRotation(new PVector(radians(0), radians(0), radians(-140)));
        guitar.scale(55.f);

        addGuitarInteraction("NECK", -25, -77, 4,
                radians(0), radians(0), radians(5),
                50, 4, 4);

        addGuitarInteraction("STRINGS", 25, -30, 4,
                radians(0), radians(0), radians(5),
                20, 4, 6);

        // TODO: Set true to debug
        guitar.doDrawInteractionArea(DEBUG_AREAS);
    }

    private void addGuitarInteraction(String id, float xOffset, float yOffset, float zOffset,
                                      float xRotationOffset, float yRotationOffset, float zRotationOffset,
                                      float width, float height, float depth) {
        InteractiveVolume volume = new InteractiveVolume(this, id, width, height, depth);

        volume.setTranslationOffset(xOffset, yOffset, zOffset);
        volume.setRotationOffset(xRotationOffset, yRotationOffset, zRotationOffset);

        volume.setPos(guitar.getPos().x,
                guitar.getPos().y,
                guitar.getPos().z);
        volume.setRotation(guitar.getRotation().x,
                guitar.getRotation().y,
                guitar.getRotation().z);

        guitar.getInteractions().add(volume);

        volume.setVisualizeVertices(DEBUG_VERTICES);
    }

    @Override
    public void draw() {
        background(0);

        rotateX(radians((mouseY * 1f / height - .5f) * 180.f));
        rotateY(radians((mouseX * 1f / width - .5f) * 180.f));

        kinect.doSkeleton(true);
        kinect.refresh(KinectSelector.NONE, true);

        lights();

        guitarInteraction();
    }

    private void guitarInteraction() {
        PVector leftHandPos = kinect.getSkelPos(KinectAnathomy.HAND_LEFT);
        PVector rightHandPos = kinect.getSkelPos(KinectAnathomy.HAND_RIGHT);

        guitar.touched(KinectAnathomy.HAND_LEFT.getSkelId(),
                leftHandPos,
                kinect.getHandRadius());

        guitar.touched(KinectAnathomy.HAND_RIGHT.getSkelId(),
                rightHandPos,
                kinect.getHandRadius());

        guitar.refresh();
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
