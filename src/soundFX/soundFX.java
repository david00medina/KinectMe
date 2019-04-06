package soundFX;

import ddf.minim.Minim;
import processing.core.PApplet;
import processing.sound.*;

public class SoundFX {
    private PApplet parent;

    private Pulse pulse;
    private SinOsc sinOsc;
    private SawOsc sawOsc;
    private SqrOsc sqrOsc;
    private TriOsc triOsc;

    private static final int SUSTAIN_TIME = 100;
    private int sustain;

    private OscillatorSelector os;

    public SoundFX(PApplet parent) {
        this.parent = parent;

        pulse = new Pulse(parent);
        sinOsc = new SinOsc(parent);
        sawOsc = new SawOsc(parent);
        sqrOsc = new SqrOsc(parent);
        triOsc = new TriOsc(parent);

        sustain = 0;
    }

    public void play(OscillatorSelector os, float frequency) {
        this.os = os;

        switch (os) {
            case PULSE:
                pulse.freq(frequency);
                pulse.play();
                break;
            case SIN:
                sinOsc.freq(frequency);
                sinOsc.play();
                break;
            case SAW:
                sawOsc.freq(frequency);
                sawOsc.play();
                break;
            case SQUARE:
                sqrOsc.freq(frequency);
                sqrOsc.play();
                break;
            case TRIANGULAR:
                triOsc.freq(frequency);
                triOsc.play();
                break;
            default:
                break;
        }

        sustain = 0;
    }

    public void stop() {
        sustain += 1;
        if (os != null && sustain >= SUSTAIN_TIME) {
            switch (os) {
                case PULSE:
                    if (pulse.isPlaying()) pulse.stop();
                    break;
                case SIN:
                    if (sinOsc.isPlaying()) sinOsc.stop();
                    break;
                case SAW:
                    if (sawOsc.isPlaying()) sawOsc.stop();
                    break;
                case SQUARE:
                    if (sqrOsc.isPlaying()) sqrOsc.stop();
                    break;
                case TRIANGULAR:
                    if (triOsc.isPlaying()) triOsc.stop();
                    break;
                default:
                    break;
            }

            sustain = 0;
        }
    }
}
