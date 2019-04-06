package kinect;

import kinect4WinSDK.SkeletonData;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Kinect {
    private PApplet parent;

    private kinect4WinSDK.Kinect kinect;
    private ArrayList<SkeletonData> bodies;
    private PImage img;

    private PVector pos;
    private Float scale;
    private Float[] skeletonRGB;
    private int handRadius = 15;

    private boolean doSkeleton;
    private Map<KinectAnathomy, PVector> skelPositions;

    private final int xOffset = -15;
    private final int yOffset = 30;

    public Kinect(PApplet parent, PVector pos, Float scale, Float[] skeletonRGB) {
        this.parent = parent;
        kinect = new kinect4WinSDK.Kinect(this.parent);
        bodies = new ArrayList<SkeletonData>();
        this.pos = pos;
        this.scale = scale;
        this.skeletonRGB = skeletonRGB;

        if (this.pos == null) this.pos = new PVector(0,0,0);
        if (this.scale == null) this.scale = 1.f;
        if (this.skeletonRGB == null) this.skeletonRGB = new Float[]{255.f, 255.f, .0f};

        skelPositions = new HashMap<>();
        doSkeleton = false;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public void setScale(Float scale) {
        this.scale = scale;
    }

    public PImage getImg() {
        return img;
    }

    public void setHandRadius(int handRadius) {
        this.handRadius = handRadius;
    }

    public int getHandRadius() {
        return handRadius;
    }

    public PVector getSkelPos(KinectAnathomy ka) {
        for (int i = 0; i < bodies.size(); i++) {
            PVector v = skelPositions.get(ka);

            if (v != null) return v;
        }
        return null;
    }

    public void refresh(KinectSelector selector, boolean onScreen) {
        switch (selector) {
            case RGB:
                img = kinect.GetImage();

                if (onScreen) {
                    parent.image(img,
                            pos.x, pos.y,
                            640 * scale, 480 * scale);
                }
                break;
            case DEPTH:
                img = kinect.GetDepth();

                if (onScreen) {
                    parent.image(img,
                            pos.x, pos.y,
                            640 * scale, 480 * scale);
                }
                break;
            case MASK:
                img = kinect.GetMask();

                if (onScreen) {
                    parent.image(img,
                            pos.x, pos.y,
                            640 * scale, 480 * scale);
                }
                break;
            default:
                break;
        }

        // Calibrates skeleton to Cam
        if (KinectSelector.RGB.equals(selector)) {
            parent.pushMatrix();
            parent.translate(xOffset, yOffset);
        }

        if(doSkeleton) bodyTracking();

        if (KinectSelector.RGB.equals(selector)) parent.popMatrix();
    }

    private void bodyTracking() {
        for (int i = 0; i < bodies.size(); i++) {
            drawSkeleton(bodies.get(i));
        }
    }

    private void drawSkeleton(SkeletonData _s) {
        collectPoints(_s);

        // Body
        drawBody();

        // Left Arm
        drawLeftArm();

        // Right Arm
        drawRightArm();

        // Left Leg
        drawLeftLeg();

        // Right Leg
        drawRightLeg();

        drawPosition(_s);
    }

    private void drawPosition(SkeletonData _s) {
        parent.pushStyle();
        parent.noStroke();
        parent.fill(0, 100, 255);

        PVector posLabel = skelPositions.get(KinectAnathomy.LABEL);

        String s1 = parent.str(_s.dwTrackingID);

        if (posLabel != null) {
            parent.pushMatrix();
            parent.translate(0, 0, posLabel.z);
            parent.text(s1, posLabel.x, posLabel.y);
            parent.popMatrix();
            parent.popStyle();
        }
    }

    private void collectPoints(SkeletonData _s) {
        PImage depthImg = kinect.GetDepth();
        for (KinectAnathomy ka :
                KinectAnathomy.values()) {
            skelPositions.put(ka, ka.getJointPos(_s, depthImg, parent.width, parent.height, xOffset, yOffset, skelPositions.get(ka)));
        }
    }

    private void drawRightLeg() {
        DrawBone(KinectAnathomy.HIP_RIGHT,
                KinectAnathomy.KNEE_RIGHT);
        DrawBone(KinectAnathomy.KNEE_RIGHT,
                KinectAnathomy.ANKLE_RIGHT);
        DrawBone(KinectAnathomy.ANKLE_RIGHT,
                KinectAnathomy.FOOT_RIGHT);
    }

    private void drawLeftLeg() {
        DrawBone(KinectAnathomy.HIP_LEFT,
                KinectAnathomy.KNEE_LEFT);
        DrawBone(KinectAnathomy.KNEE_LEFT,
                KinectAnathomy.ANKLE_LEFT);
        DrawBone(KinectAnathomy.ANKLE_LEFT,
                KinectAnathomy.FOOT_LEFT);
    }

    private void drawRightArm() {
        DrawBone(KinectAnathomy.SHOULDER_RIGHT,
                KinectAnathomy.ELBOW_RIGHT);
        DrawBone(KinectAnathomy.ELBOW_RIGHT,
                KinectAnathomy.WRIST_RIGHT);
        DrawBone(KinectAnathomy.WRIST_RIGHT,
                KinectAnathomy.HAND_RIGHT);
    }

    private void drawLeftArm() {
        DrawBone(KinectAnathomy.SHOULDER_LEFT,
                KinectAnathomy.ELBOW_LEFT);
        DrawBone(KinectAnathomy.ELBOW_LEFT,
                KinectAnathomy.WRIST_LEFT);
        DrawBone(KinectAnathomy.WRIST_LEFT,
                KinectAnathomy.HAND_LEFT);
    }

    private void drawBody() {
        DrawBone(KinectAnathomy.HEAD,
                KinectAnathomy.SHOULDER_CENTER);
        DrawBone(KinectAnathomy.SHOULDER_CENTER,
                KinectAnathomy.SHOULDER_LEFT);
        DrawBone(KinectAnathomy.SHOULDER_CENTER,
                KinectAnathomy.SHOULDER_RIGHT);
        DrawBone(KinectAnathomy.SHOULDER_CENTER,
                KinectAnathomy.SPINE);
        DrawBone(KinectAnathomy.SHOULDER_LEFT,
                KinectAnathomy.SPINE);
        DrawBone(KinectAnathomy.SHOULDER_RIGHT,
                KinectAnathomy.SPINE);
        DrawBone(KinectAnathomy.SPINE,
                KinectAnathomy.HIP_CENTER);
        DrawBone(KinectAnathomy.HIP_CENTER,
                KinectAnathomy.HIP_LEFT);
        DrawBone(KinectAnathomy.HIP_CENTER,
                KinectAnathomy.HIP_RIGHT);
        DrawBone(KinectAnathomy.HIP_LEFT,
                KinectAnathomy.HIP_RIGHT);
    }

    private void DrawBone(KinectAnathomy _j1, KinectAnathomy _j2) {
        parent.pushStyle();
        parent.noFill();
        parent.stroke(skeletonRGB[0], skeletonRGB[1], skeletonRGB[2]);

        int i = 0;
        if (KinectAnathomy.HIP_CENTER.equals(_j1) && KinectAnathomy.HIP_LEFT.equals(_j2)) {
            i = 1;
        }

        PVector joint1 = skelPositions.get(_j1);
        PVector joint2 = skelPositions.get(_j2);

        if (joint1 != null && joint2 != null) {
            parent.line(joint1.x, joint1.y, joint1.z,
                    joint2.x, joint2.y, joint2.z);

            if ((KinectAnathomy.HAND_LEFT.equals(_j2) || KinectAnathomy.HAND_RIGHT.equals(_j2))
                    && handRadius > 0) {
                parent.pushStyle();
                parent.fill(255,0,0,50);
                parent.pushMatrix();
                parent.translate(joint2.x, joint2.y, joint2.z);
                parent.sphereDetail(15);
                parent.sphere(handRadius);
                parent.popMatrix();
                parent.popStyle();
            }
        }

        parent.popStyle();
    }

    public void appearEvent(SkeletonData _s) {
        if (_s.trackingState == kinect4WinSDK.Kinect.NUI_SKELETON_NOT_TRACKED) {
            return;
        }
        synchronized(bodies) {
            bodies.add(_s);
        }
    }

    public void disappearEvent(SkeletonData skel) {
        synchronized(bodies) {
            for (int i=bodies.size ()-1; i>=0; i--) {
                if (skel.dwTrackingID == bodies.get(i).dwTrackingID) {
                    bodies.remove(i);
                }
            }
        }
    }

    public void moveEvent(SkeletonData _b, SkeletonData _a) {
        if (_a.trackingState == kinect4WinSDK.Kinect.NUI_SKELETON_NOT_TRACKED) {
            return;
        }
        synchronized(bodies) {
            for (int i=bodies.size ()-1; i>=0; i--) {
                if (_b.dwTrackingID == bodies.get(i).dwTrackingID) {
                    bodies.get(i).copy(_a);
                    break;
                }
            }
        }
    }

    public void doSkeleton(boolean b) {
        doSkeleton = b;
    }
}
