package kinect;

import algorithms.Transformation;
import kinect4WinSDK.SkeletonData;
import openpose.Openpose;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Kinect {
    private PApplet parent;

    private KinectSelector selector;

    private kinect4WinSDK.Kinect kinect;
    private ArrayList<SkeletonData> bodies;
    private PImage img;

    private PVector pos;
    private Float scale;
    private Float[] skeletonRGB;
    private final int handRadius = 30;

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

    public int getHandRadius() {
        return handRadius;
    }

    public void addOPNetwork(Openpose op) {
        opNetwork.add(op);
    }

    public void refresh(KinectSelector selector, boolean onScreen) {
        this.selector = selector;

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

        if(opNetwork != null && opNetwork.size() != 0) {
            for (Openpose op :
                    opNetwork) {
                op.makeInference(img);
            }
        }

        if (KinectSelector.RGB.equals(selector)) parent.popMatrix();
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
            PVector handPos = getJointPos(jointID);
            parent.pushMatrix();
            parent.translate(handPos.x, handPos.y, handPos.z);
            parent.sphere(handRadius);
            parent.popMatrix();
        }
        parent.popStyle();
    }

    private void drawPosition(SkeletonData _s) {
        parent.pushStyle();
        parent.noStroke();
        parent.fill(0, 100, 255);

        PVector posLabel = getJointPos(SkeletonData.NUI_SKELETON_POSITION_SHOULDER_CENTER);
        String s1 = parent.str(_s.dwTrackingID);
        if (posLabel != null) {
            parent.pushMatrix();
            parent.translate(0, 0, posLabel.z);
            parent.text(s1, posLabel.x, posLabel.y);
            parent.popMatrix();
            parent.popStyle();
        }
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

            PVector joint1 = getJointPos(_j1);
            PVector joint2 = getJointPos(_j2);

            if (joint1 != null && joint2 != null) {
                parent.line(joint1.x, joint1.y, joint1.z,
                        joint2.x, joint2.y, joint2.z);

            }

        }
        parent.popStyle();
    }

    public PVector getJointPos(int jointID) {
        for (int i = 0; i < bodies.size(); i++) {
            if(bodies.get(i).skeletonPositionTrackingState[jointID] != kinect4WinSDK.Kinect.NUI_SKELETON_POSITION_NOT_TRACKED) {
                PVector v = new PVector(bodies.get(i).skeletonPositions[jointID].x * parent.width + xOffset,
                        bodies.get(i).skeletonPositions[jointID].y * parent.height + yOffset,
                        0);
                return getJointDepth(v);
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

    private PVector getJointDepth(PVector joint) {
        PImage depthImg = kinect.GetDepth();

        PVector j = Transformation.translate(joint, -xOffset, -yOffset, 0);

        int x = (int) j.x;
        int y = (int) j.y;
        int arrayPos = x + (y * depthImg.width) - 1;

        if (arrayPos < 0 || arrayPos >= depthImg.pixels.length) return joint;

        int depthData = depthImg.pixels[arrayPos];
        /*PVector depthPixel = new PVector(depthData & 0xFF, (depthData >> 8) & 0xFF, (depthData >> 8) & 0xFF);
        System.out.println(depthPixel);*/
        float depth = parent.map(depthData & 0xFF, 130, 230, 0, 360);
        joint.z = depth;
        return joint;

        /*parent.pushStyle();
        parent.strokeWeight(5);
        parent.stroke(parent.color(0, 255, 0));
        parent.point(x, y);
        parent.popStyle();
        parent.loadPixels();
        parent.pixels[x + (y * parent.width)] = parent.color(0, 255, 0);
        for (int i = y * parent.width-600; i < y * parent.width-600; i++) {
            parent.pixels[x+i] = parent.color(0,255,0);
        }
        parent.updatePixels();*/
    }
}
