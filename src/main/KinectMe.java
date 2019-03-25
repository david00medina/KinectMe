package main;

import edu.ufl.digitalworlds.j4k.PKinect;
import edu.ufl.digitalworlds.j4k.PSkeleton;
import edu.ufl.digitalworlds.j4k.Skeleton;
import kinect.JKinect;
import kinect.Kinect;
import kinect4WinSDK.SkeletonData;
import object.Material;
import object.Object;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class KinectMe extends PApplet {
    private Kinect kinect;
    private JKinect jKinect;
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

        jKinect = new JKinect(this, PKinect.XYZ|PKinect.COLOR|PKinect.UV|PKinect.SKELETON);
//        jKinect.getPKinect().setNearMode(true);


        ironmanModel = loadShape("res/model/ironman/IronMan.obj");
        ironmanModel.scale(.002f);
        ironmanMat = new Material(this, new PVector(255,0,0), new PVector(255,0,0),
                new PVector(255,0,0), 128.f);
        ironman = new Object(this, new PVector(0,0,0), ironmanModel, null, null);
    }

    @Override
    public void draw() {
        background(0);

        jKinect.getPKinect().setFrustum();

        //Simple rotation of the 3D scene using the mouse
        /*translate(0,0,-2);
        rotateX(radians((mouseY * 1f / height - .5f) * 180));
        rotateY(radians((mouseX * 1f / width - .5f) * 180));
        translate(0,0,2);*/


        jKinect.draw3DSkeleton();

        PVector pos = jointPos(Skeleton.HAND_RIGHT);

        System.out.println(pos);


        /*s = jKinect.getPSkeleton(0);
        System.out.println("X2 : " + s.get3DJointX(Skeleton.HEAD)
                + ", Y2 : " + s.get3DJointY(Skeleton.HEAD)
                + ", Z2 : " + s.get3DJointZ(Skeleton.HEAD));*/

        /*double[] x1 = jKinect.getSkeletons()[PSkeleton.HEAD].get3DJoint(PSkeleton.HEAD);
        float x = jKinect.getSkeletons()[PSkeleton.HIP_LEFT].get3DJointX(0);
        float y = jKinect.getSkeletons()[PSkeleton.HIP_LEFT].get3DJointY(0);
        float z = jKinect.getSkeletons()[PSkeleton.HIP_LEFT].get3DJointZ(0);
        System.out.println("X: " + x + " Y: " + y + " Z: " + z);*/
        if (pos != null) ironman.setPos(new PVector((int)pos.x, (int)pos.y, (int)pos.z));
        line(0.f, height / 2.f, width, -2, height / 2.f, -2);
        ironman.refresh();

    }

    private PVector jointPos(int jointID) {
        PSkeleton[] skeletons = jKinect.getPSkeletons();
        for (int skeleton_id = 0; skeleton_id < jKinect.getPKinect().getMaxNumberOfSkeletons() - 1; skeleton_id++) {
            PSkeleton s = skeletons[skeleton_id];
            if (s.isTracked()) {
                PVector v = new PVector(s.get3DJointX(jointID),
                        s.get3DJointY(jointID),
                        -s.get3DJointZ(jointID));
                return v;
            }
        }
        return null;
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
