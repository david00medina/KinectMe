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

        int xOffset = 5;
        int yOffset = -80;
        int zOffset = 0;
        float xRotationOffset = radians(0);
        float yRotationOffset = radians(0);
        float zRotationOffset = radians(5);
        InteractiveVolume ia = new InteractiveVolume(this, 50, 4, 8);
        ia.setPos((int) guitar.getPos().x + xOffset,
                (int) guitar.getPos().y + yOffset,
                (int) guitar.getPos().z + zOffset);
        ia.setRotation(guitar.getRotation().x + xRotationOffset,
                guitar.getRotation().y + yRotationOffset,
                guitar.getRotation().z + zRotationOffset);
        guitar.getInteractions().add(ia);

        // TODO: Set true to debug
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
        guitar.setPos(v);
        guitar.refresh();
        guitar.touched(KinectAnathomy.HAND_LEFT.getSkelId(),
                kinect.getSkelPos(KinectAnathomy.HAND_LEFT),
                kinect.getHandRadius());
        guitar.touched(KinectAnathomy.HAND_RIGHT.getSkelId(),
                kinect.getSkelPos(KinectAnathomy.HAND_RIGHT),
                kinect.getHandRadius());

        /*if (headPos != null) {
            pushMatrix();
            translate(0, 0, -1);
            PShape hatFrame = createShape(RECT, headPos.x - 80, headPos.y - 65, 144, 96);
            hatFrame.setTexture(hat);
            shape(hatFrame);
            popMatrix();
        }*/
        //if (headPos != null) image(hatFrame, headPos.x - 80, headPos.y - 65,144,96);
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
