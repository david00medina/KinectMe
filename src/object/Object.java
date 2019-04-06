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
        for (InteractiveVolume iv :
                interactions) {
            iv.setPos(pos.x, pos.y, pos.z);
        }
    }

    public PVector getRotation() {
        return rotation;
    }

    public void setRotation(PVector rotation) {
        this.rotation = rotation;
        for (InteractiveVolume iv :
                interactions) {
            iv.setRotation(rotation.x, rotation.y, rotation.z);
        }
    }

    public PShape getModel() {
        return model;
    }

    public void setModel(PShape model) {
        this.model = model;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
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
        updateState();
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

    public abstract void updateState();

    public abstract void touched(int id, PVector joint, int inRadius);

    public abstract void doDrawInteractionVolume(boolean b);
}
