package kinect;

import kinect4WinSDK.SkeletonData;
import openpose.Openpose;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Kinect {
    private PApplet parent;

    private kinect4WinSDK.Kinect kinect;
    private ArrayList<SkeletonData> bodies;
    private PImage img;

    private PVector pos;
    private Float scale;
    private Float[] skeletonRGB;

    private boolean doSkeleton;

    private List<Openpose> opNetwork;

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

        opNetwork = new ArrayList<>();
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

    public void addOPNetwork(Openpose op) {
        opNetwork.add(op);
    }

    public void refresh(KinectEnum selector, boolean onScreen) {
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
        if (KinectEnum.RGB.equals(selector)) {
            parent.pushMatrix();
            parent.translate(xOffset, yOffset);
        }

        if(doSkeleton) bodyTracking();

        if(opNetwork != null && opNetwork.size() != 0) {
            for (Openpose op :
                    opNetwork) {
                op.makeInference(img);
            }
        }

        if (KinectEnum.RGB.equals(selector)) parent.popMatrix();
    }

    private void bodyTracking() {
        for (int i = 0; i < bodies.size(); i++) {
            drawSkeleton(bodies.get(i));
            drawHand(bodies.get(i), kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HAND_RIGHT);
            drawHand(bodies.get(i), kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HAND_LEFT);
        }
    }

    private void drawHand(SkeletonData _s, int jointID) {
        parent.pushStyle();
        parent.fill(255,0,0,50);
        //Detectada
        if (_s.skeletonPositionTrackingState[jointID]!= kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_NOT_TRACKED)
        {
            parent.ellipse(_s.skeletonPositions[jointID].x * parent.width,
                    _s.skeletonPositions[jointID].y * parent.height,
                    30,30);
        }
        parent.popStyle();
    }

    private void drawPosition(SkeletonData _s) {
        parent.pushStyle();
        parent.noStroke();
        parent.fill(0, 100, 255);

        String s1 = parent.str(_s.dwTrackingID);
        parent.text(s1, _s.position.x * parent.width, _s.position.y * parent.height);
        parent.popStyle();
    }

    private void drawSkeleton(SkeletonData _s) {
        // Cuerpo
        drawBody(_s);

        // Brazo izquierdo
        drawLeftArm(_s);

        // Brazo derecho
        drawRightArm(_s);

        // Pierna izquierda
        drawLeftLeg(_s);

        // Pierna derecha
        drawRightLeg(_s);

        drawPosition(_s);
    }

    private void drawRightLeg(SkeletonData _s) {
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_RIGHT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_KNEE_RIGHT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_KNEE_RIGHT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ANKLE_RIGHT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ANKLE_RIGHT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_FOOT_RIGHT);
    }

    private void drawLeftLeg(SkeletonData _s) {
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_KNEE_LEFT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_KNEE_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ANKLE_LEFT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ANKLE_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_FOOT_LEFT);
    }

    private void drawRightArm(SkeletonData _s) {
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_RIGHT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ELBOW_RIGHT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ELBOW_RIGHT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_WRIST_RIGHT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_WRIST_RIGHT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HAND_RIGHT);
    }

    private void drawLeftArm(SkeletonData _s) {
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ELBOW_LEFT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_ELBOW_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_WRIST_LEFT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_WRIST_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HAND_LEFT);
    }

    private void drawBody(SkeletonData _s) {
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HEAD,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_CENTER);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_CENTER,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_LEFT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_CENTER,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_RIGHT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_CENTER,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SPINE);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SPINE);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SHOULDER_RIGHT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SPINE);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_SPINE,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_CENTER);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_CENTER,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_LEFT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_CENTER,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_RIGHT);
        DrawBone(_s,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_LEFT,
                kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_HIP_RIGHT);
    }

    private void DrawBone(SkeletonData _s, int _j1, int _j2) {
        parent.pushStyle();
        parent.noFill();
        parent.stroke(skeletonRGB[0], skeletonRGB[1], skeletonRGB[2]);

        //Comprueba validez del dato
        if (_s.skeletonPositionTrackingState[_j1] != kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_NOT_TRACKED &&
                _s.skeletonPositionTrackingState[_j2] != kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_NOT_TRACKED) {

            parent.line(_s.skeletonPositions[_j1].x * parent.width,
                    _s.skeletonPositions[_j1].y * parent.height,
                    _s.skeletonPositions[_j2].x * parent.width,
                    _s.skeletonPositions[_j2].y * parent.height);
        }
        parent.popStyle();
    }

    public PVector getJointPos(int jointID) {
        for (int i = 0; i < bodies.size(); i++) {
            if(bodies.get(i).skeletonPositionTrackingState[jointID] != kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_NOT_TRACKED) {
                PVector v = new PVector(bodies.get(i).skeletonPositions[jointID].x * parent.width + xOffset,
                        bodies.get(i).skeletonPositions[jointID].y * parent.height + yOffset,
                        0);
                return v;
            }
        }
        return null;
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
