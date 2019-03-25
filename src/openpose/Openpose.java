package openpose;

import cvimage.CVImage;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

public class Openpose {
    private PApplet parent;
    private CVImage img;
    private Net net;

    public Openpose(PApplet parent, String prototxt, String caffeModel) {
        this.parent = parent;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        net = Dnn.readNetFromCaffe(prototxt, caffeModel);
    }

    public void makeInference(PImage camImg) {
        if (img == null) img = new CVImage(camImg.width, camImg.height);
        img.copy(camImg, 0, 0, camImg.width, camImg.height,
                0,0, img.width, img.height);
        img.copyTo();
        /*Mat img = new Mat(camImg.width, camImg.height, CvType.CV_32SC1);
        img.put(0,0, camImg.pixels);*/

        Mat inputBlob = Dnn.blobFromImage(img.getBGR(), 1.f / 255,
                new Size(camImg.width, camImg.height), new Scalar(0,0,0),
                false, false);
        net.setInput(inputBlob);
        Mat result = net.forward().reshape(1,19);

//        System.out.println(result);
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = 0; i < result.rows() - 1; i++) {
            Mat heatmap = result.row(i).reshape(1,40);
            Core.MinMaxLocResult mm = Core.minMaxLoc(heatmap);
            Point p = new Point();
            if (mm.maxVal > .1f) {
                p = mm.maxLoc;
            }
            heatmap.release();
            points.add(p);
//            System.out.println(i + " " + p + " " + heatmap);
        }

        int pairs[][] = {
                {1, 2}, // left shoulder
                {1, 5}, // right shoulder
                {2, 3}, // left arm
                {3, 4}, // left forearm
                {5, 6}, // right arm
                {6, 7}, // right forearm
                {1, 8}, // left body
                {8, 9}, // left thigh
                {9, 10}, // left calf
                {1, 11}, // right body
                {11, 12}, // right thigh
                {12, 13}, // right calf
                {1, 0}, // neck
                {0, 14}, // left nose
                {14, 16}, // left eye
                {0, 15}, // right nose
                {15, 17}  // right eye
        };

        float SX = (float)(img.width) / 40.f;
        float SY = (float)(img.height) / 40.f;

        parent.pushStyle();
        parent.noFill();
        parent.strokeWeight(3);
        parent.fill(255,0,0);
        parent.stroke(255,0,0);
        for (int n = 0; n < points.size() - 1; n++) {
            Point a = points.get(pairs[n][0]).clone();
            Point b = points.get(pairs[n][1]).clone();

            if (a.x <= 0 || a.y <= 0 || b.x <= 0 || b.y <= 0) continue;

            a.x *= SX;
            a.y *= SY;
            b.x *= SX;
            b.y *= SY;
            parent.line((float)a.x, (float)a.y, (float)b.x, (float)b.y);
            //parent.point((float)a.x, (float)a.y);
        }
        parent.popStyle();
        inputBlob.release();
        result.release();
    }
}
