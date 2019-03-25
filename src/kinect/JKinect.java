package kinect;

import edu.ufl.digitalworlds.j4k.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class JKinect {
    private PApplet parent;
    private PKinect pKinect;
    private PSkeleton[] _s;

    public JKinect(PApplet parent, int flags) {
        super();
        this.parent = parent;
        pKinect = new PKinect(this.parent);
        pKinect.start(flags);
    }

    public void draw3DSkeleton() {
        /*PImage img = new PImage(640,480, PConstants.ARGB);
        PDepthMap map = pKinect.getPDepthMap();
        pKinect.updatePImage(img);
        map.draw(img);*/

        parent.pushStyle();
        parent.stroke(255,0,0);
        parent.strokeWeight(5);

        _s = pKinect.getPSkeletons();

        for (PSkeleton s :
                _s) {
            s.draw();
        }
        parent.popStyle();
    }

    public PKinect getPKinect() {
        return pKinect;
    }

    public PSkeleton[] getPSkeletons() {
        return _s;
    }

    public PSkeleton getPSkeleton(int i) {
        return _s[i];
    }
}
