package object;

import processing.core.*;

public class Object {
    private PApplet parent;

    private PVector pos;
    private PShape model;
    private Texture texture;
    private Material material;

    public Object(PApplet parent, PVector pos, PShape model, Texture texture, Material material) {
        this.parent = parent;

        this.pos = pos;
        this.model = model;
        this.texture = texture;
        this.material = material;
    }

    public Object(PApplet parent, PVector pos, PShape model, Material material) {
        this.parent = parent;
        this.pos = pos;
        this.model = model;
        this.material = material;
    }

    public PVector getPos() {
        return pos;
    }

    public Material getMaterial() {
        return material;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public void setModel(PShape model) {
        this.model = model;
    }

    public PShape getModel() {
        return model;
    }

    public void refresh() {
        if (material != null) material.refresh();
        if (texture != null) model.setTexture(texture.getTexture());
        refreshPosition();
    }

    private void refreshPosition() {
        parent.pushMatrix();
        parent.translate(pos.x, pos.y, pos.z);
        parent.shape(model);
        parent.popMatrix();
    }
}
