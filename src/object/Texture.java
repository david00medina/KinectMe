package object;

import processing.core.PImage;

public class Texture {
    private PImage texture;

    public Texture(PImage texture) {
        this.texture = texture;
    }

    public void setTexture(PImage texture) {
        this.texture = texture;
    }

    public PImage getTexture() {
        return texture;
    }
}