package kinect;

import algorithms.Transformation;
import kinect4WinSDK.Kinect;
import kinect4WinSDK.SkeletonData;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public enum KinectAnathomy {
    HEAD(0, Kinect.NUI_SKELETON_POSITION_HEAD),
    SHOULDER_CENTER(1, Kinect.NUI_SKELETON_POSITION_SHOULDER_CENTER),
    SHOULDER_LEFT(2, Kinect.NUI_SKELETON_POSITION_SHOULDER_LEFT),
    SHOULDER_RIGHT(3, Kinect.NUI_SKELETON_POSITION_SHOULDER_RIGHT),
    SPINE(4, Kinect.NUI_SKELETON_POSITION_SPINE),
    HIP_CENTER(5, Kinect.NUI_SKELETON_POSITION_HIP_CENTER),
    HIP_LEFT(6, Kinect.NUI_SKELETON_POSITION_HIP_LEFT),
    HIP_RIGHT(7, Kinect.NUI_SKELETON_POSITION_HIP_RIGHT),
    ELBOW_LEFT(8, Kinect.NUI_SKELETON_POSITION_ELBOW_LEFT),
    WRIST_LEFT(9, Kinect.NUI_SKELETON_POSITION_WRIST_LEFT),
    HAND_LEFT(10, Kinect.NUI_SKELETON_POSITION_HAND_LEFT),
    ELBOW_RIGHT(11, Kinect.NUI_SKELETON_POSITION_ELBOW_RIGHT),
    WRIST_RIGHT(12, Kinect.NUI_SKELETON_POSITION_WRIST_RIGHT),
    HAND_RIGHT(13, Kinect.NUI_SKELETON_POSITION_HAND_RIGHT),
    KNEE_LEFT(14, Kinect.NUI_SKELETON_POSITION_KNEE_LEFT),
    ANKLE_LEFT(15, Kinect.NUI_SKELETON_POSITION_ANKLE_LEFT),
    FOOT_LEFT(16, Kinect.NUI_SKELETON_POSITION_FOOT_LEFT),
    KNEE_RIGHT(17, Kinect.NUI_SKELETON_POSITION_KNEE_RIGHT),
    ANKLE_RIGHT(18, Kinect.NUI_SKELETON_POSITION_ANKLE_RIGHT),
    FOOT_RIGHT(19, Kinect.NUI_SKELETON_POSITION_FOOT_RIGHT),
    LABEL(20, Kinect.NUI_SKELETON_POSITION_SHOULDER_CENTER),
    NOT_TRACKED(21, Kinect.NUI_SKELETON_POSITION_NOT_TRACKED);

    private int id;
    private int skelID;

    KinectAnathomy(int id, int skelID) {
        this.id = id;
        this.skelID = skelID;
    }

    public int getId() {
        return this.id;
    }

    public int getSkelId() {
        return this.skelID;
    }

    public PVector getJointPos(SkeletonData _s, PImage depthImg, float width, float height, float xOffset, float yOffset, PVector original) {
        if (_s.skeletonPositionTrackingState[this.skelID] != KinectAnathomy.NOT_TRACKED.skelID) {
            PVector v = new PVector(_s.skeletonPositions[this.skelID].x * width + xOffset,
                    _s.skeletonPositions[this.skelID].y * height + yOffset,
                    0);
            return getDepth(v, depthImg, width, height, xOffset, yOffset, original);
        }
        return null;
    }

    private PVector getDepth(PVector joint, PImage depthImg, float w, float h, float xOffset, float yOffset, PVector original) {
        PVector j = new PVector(joint.x, joint.y, joint.z);
        j = Transformation.translate(j, -xOffset, -yOffset, 0);

        int x = (int) j.x;
        int y = (int) j.y;

        int arrayPos = x + (y * depthImg.width) - 1;

        if (arrayPos < 0 || arrayPos >= depthImg.pixels.length) return original;

        int depthData = depthImg.pixels[arrayPos];

        float data = depthData & 0xFF;

        if (data >= 100 && data <= 250) {
            float depth = PApplet.map(data, 130, 230, 0, 360);
            joint.z = depth;
            return joint;
        }

        return original;
    }
}
