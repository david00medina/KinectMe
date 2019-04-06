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

public class KinectMe extends PApplet {
    private static final boolean DEBUG_AREAS = true;
    private static final boolean DEBUG_VERTICES = true;
    private static final int SCALE = 60;
    private static final int COLS = 60;
    private static final int ROWS = 60;

    private Kinect kinect;
    private Guitar guitar;
    private PShape floor;

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
        kinect.setHandRadius(10);

        spawnGuitar();

        createFloor();
    }

    private void spawnGuitar() {
        PShape guitarModel = loadShape("../../data/models/guitar/guitar.obj");
        guitar = new Guitar(this, guitarModel, null, null);

        guitar.setPos(new PVector(width / 2.f, 4.f * height / 6.f, 150));
        guitar.setRotation(new PVector(radians(0), radians(0), radians(-140)));
        guitar.scale(55.f);

        addGuitarInteraction("NECK", -25, -77, 4,
                radians(0), radians(0), radians(5),
                50, 4, 4);

        addGuitarInteraction("STRINGS", 25, -30, 4,
                radians(0), radians(0), radians(5),
                20, 4, 6);

        guitar.doDrawInteractionVolume(DEBUG_AREAS);
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

        setCamera();

        kinect.doSkeleton(true);
        kinect.refresh(KinectSelector.NONE, true);

        lights();

        guitarInteraction();

        makeFloor();
    }

    private void setCamera() {
        PVector spine = kinect.getSkelPos(KinectAnathomy.SPINE);

        if (spine != null) {
            PVector camPos = new PVector(width/2.f,
                    height/2.f,
                    (height/2.f) / tan(PI * 30.f / 180.f));

            camera(camPos.x, camPos.y, camPos.z,
                    spine.x, spine.y, spine.z,
                    0,1, 0);
        }
    }

    private void makeFloor() {
        pushMatrix();
        translate(-ROWS * SCALE / 2.f, 400, -COLS * SCALE /2.f);
        shape(floor);
        popMatrix();
    }

    private void createFloor() {
        stroke(255);
        noFill();

        floor = createShape();
        for (int z = 0; z < COLS; z++) {
            floor.beginShape(QUAD_STRIP);
            for (int x = 0; x < ROWS; x++) {
                floor.vertex(x * SCALE, 0, z * SCALE);
                floor.vertex(x * SCALE, 0, (z+1) * SCALE);
            }
            floor.endShape();
        }
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
