package object;

import processing.core.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Object {
    protected PApplet parent;

    protected PVector pos;
    protected PVector rotation;
    protected PShape model;
    protected Texture texture;
    protected Material material;
    protected List<InteractiveVolume> interactions;

    public Object(PApplet parent, PShape model, Texture texture, Material material) {
        this.parent = parent;

        this.pos = new PVector(0,0,0);
        this.rotation = new PVector(0,0,0);
        this.model = model;
        this.texture = texture;
        this.material = material;
        interactions = new ArrayList<>();
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public PVector getRotation() {
        return rotation;
    }

    public void setRotation(PVector rotation) {
        this.rotation = rotation;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public PShape getModel() {
        return model;
    }

    public void setModel(PShape model) {
        this.model = model;
    }

    public List<InteractiveVolume> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<InteractiveVolume> interactions) {
        this.interactions = interactions;
    }

    public void scale(float s) {
        model.scale(s);
    }

    public void refresh() {
        if (material != null) material.refresh();
        if (texture != null) model.setTexture(texture.getTexture());
        setTransformations();
        drawInteractionVolume();
    }

    private void setTransformations() {
        parent.pushMatrix();
        parent.translate(pos.x, pos.y, pos.z);
        parent.rotateX(rotation.x);
        parent.rotateY(rotation.y);
        parent.rotateZ(rotation.z);
        parent.shape(model, 0, 0);
        parent.popMatrix();
    }

    public abstract void drawInteractionVolume();

    public abstract boolean touched(int id, PVector joint, int inRadius);

    public abstract void doDrawInteractionArea(boolean b);
}
