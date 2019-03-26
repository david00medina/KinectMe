package main;

import kinect.Kinect;
import kinect.KinectEnum;
import kinect4WinSDK.SkeletonData;
import object.InteractiveVolume;
import object.instrument.Guitar;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.MouseEvent;

public class KinectMe extends PApplet {
    private Kinect kinect;
    private PImage hat;
    private PShape guitarModel;
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
        hat = loadImage("../../res/images/hat.png");

        spawnGuitar();
    }

    private void spawnGuitar() {
        guitarModel = loadShape("../../data/models/guitar/guitar.obj");
        guitar = new Guitar(this, new PVector(70, 420, -2),
                new PVector(radians(0), radians(0), radians(-120)),
                guitarModel, null, null);
        guitar.scale(55.f);

        int xOffset = 70;
        int yOffset = -30;
        int zOffset = -1;
        InteractiveVolume ia = new InteractiveVolume(this,
                (int) guitar.getPos().x + xOffset,
                (int) guitar.getPos().y + yOffset,
                (int) guitar.getPos().z + zOffset,
                100, 20, 20);
        ia.setRotation(radians(0), radians(0), radians(-120));
        guitar.getInteractions().add(ia);

        // TODO: Set true to debug
        guitar.doDrawInteractionArea(true);
    }

    @Override
    public void draw() {
        background(0);

        PImage img = getDepthMap();

        kinect.doSkeleton(true);
        kinect.refresh(KinectEnum.RGB, true);

        PVector headPos = kinect.getJointPos(SkeletonData.NUI_SKELETON_POSITION_HEAD);
//        System.out.println(headPos);
        if (headPos != null) {
            int x = (int)headPos.x;
            int y = (int)headPos.y;

            if (x >= 0 && y >= 0) {
                int depthData = img.pixels[x * y];
                PVector depthPoint = new PVector(depthData & 0xFF, (depthData >> 8) & 0xFF, (depthData >> 8) & 0xFF);
//                System.out.println(img.pixels[x * y]);
//                System.out.println("DEPTH POINT : " + depthPoint);
            }
        }

        lights();

        PVector v = new PVector(guitar.getPos().x, guitar.getPos().y, guitar.getPos().z);
        guitar.setPos(v);
        /*for (InteractiveVolume ia :
                guitar.getInteractions()) {
            ia.z = (int) guitar.getPos().z - mouseY;
        }*/
//        System.out.println(v);
        guitar.drawInteractionVolume();
        guitar.refresh();
        guitar.touched(SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT, kinect.getJointPos(SkeletonData.NUI_SKELETON_POSITION_HAND_LEFT));
        guitar.touched(SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT, kinect.getJointPos(SkeletonData.NUI_SKELETON_POSITION_HAND_RIGHT));
//        shape(guitarModel,width,height);

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

    private PImage getDepthMap() {
        kinect.doSkeleton(false);
        kinect.refresh(KinectEnum.DEPTH, false);
        return kinect.getImg();
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
