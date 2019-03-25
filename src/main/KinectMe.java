package main;

import edu.ufl.digitalworlds.j4k.PKinect;
import edu.ufl.digitalworlds.j4k.PSkeleton;
import edu.ufl.digitalworlds.j4k.Skeleton;
import kinect.JKinect;
import kinect.Kinect;
import kinect.KinectEnum;
import kinect4WinSDK.SkeletonData;
import object.Material;
import object.Object;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class KinectMe extends PApplet {
    private Kinect kinect;
    private PShape ironmanModel;
    private Material ironmanMat;
    private Object ironman;

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
        kinect.doSkeleton(true);

        ironmanModel = loadShape("res/model/ironman/IronMan.obj");
        ironmanModel.scale(.002f);
        ironmanMat = new Material(this, new PVector(255,0,0), new PVector(255,0,0),
                new PVector(255,0,0), 128.f);
        ironman = new Object(this, new PVector(0,0,0), ironmanModel, null, null);
    }

    @Override
    public void draw() {
        background(0);

        kinect.refresh(KinectEnum.RGB);
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
