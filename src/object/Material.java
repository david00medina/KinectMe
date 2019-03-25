package object;

import processing.core.PApplet;
import processing.core.PVector;

public class Material {
    private PApplet parent;
    private PVector ambient;
    private PVector emissive;
    private PVector specular;
    private float shininess;

    public Material(PApplet parent, PVector ambient, PVector emissive, PVector specular, float shininess) {
        this.parent = parent;
        this.ambient = ambient;
        this.emissive = emissive;
        this.specular = specular;
        this.shininess = shininess;
    }

    public void refresh() {
        parent.pushStyle();

        parent.noStroke();
        parent.ambient(ambient.x, ambient.y, ambient.z);
        parent.emissive(emissive.x, emissive.y, emissive.z);
        parent.specular(specular.x, specular.y, specular.z);
        parent.shininess(shininess);

        parent.popStyle();
    }

    public void setAmbient(PVector ambient) {
        this.ambient = ambient;
    }

    public void setEmissive(PVector emissive) {
        this.emissive = emissive;
    }

    public void setSpecular(PVector specular) {
        this.specular = specular;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public PVector getAmbient() {
        return ambient;
    }

    public PVector getEmissive() {
        return emissive;
    }

    public PVector getSpecular() {
        return specular;
    }

    public float getShininess() {
        return shininess;
    }
}