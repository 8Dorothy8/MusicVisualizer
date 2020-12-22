import processing.core.PApplet;
import ddf.minim.*;
import ddf.minim.analysis.FFT;
import java.util.Random;
import processing.core.PImage;

public class MusicVisualizer extends PApplet {

    //PImage pic;
    Minim minim;
    AudioPlayer audio;
    FFT fft;
    private boolean isPaused;
    private PImage playButton;
    private PImage pauseButton;
    private int[] pauseCoordinates = {425, 275};


    float xCenter;
    float yCenter;

    public static void main(String[] args) {
        PApplet.main("MusicVisualizer");
    }

    public void settings() {
        size(900, 600);
    }

    public void setup() {

        pauseButton = loadImage("Resources/Icons/pause.png");
        playButton = loadImage("Resources/Icons/play.png");

        xCenter = width / 2;
        yCenter = height / 2;

        minim = new Minim(this);
        audio = minim.loadFile("Resources/piano.mp3");
        background(0, 0, 0);

        isPaused = false;

        fft = new FFT(audio.bufferSize(), audio.sampleRate());

        audio.loop();
    }

    public void draw() {
        background(0, 0, 0);

        fft.forward(audio.mix);

        float[] leftChannel = audio.left.toArray();
        float[] rightChannel = audio.left.toArray();

        for (int i = 0; i < leftChannel.length - 1; i++) {
            drawChannel(leftChannel, i, 1);
            drawChannel(rightChannel, i, -1);
        }
        for (int i = 0; i < fft.specSize(); i++) {
            drawFrequency(i);
        }
        drawButtons();
    }

    public void drawChannel(float[] channel, int index, int direction) {
        Random rng = new Random();
        int red = rng.nextInt(256);
        int green = rng.nextInt(256);
        int blue = rng.nextInt(256);

        strokeWeight(2);
        stroke(red, green, blue);
        line(index,
                yCenter + direction * channel[index] * 1000,
                index + 1,
                yCenter + direction * channel[index + 1] * 1000);
    }

    public void drawFrequency(int index) {

        Random rng = new Random();

        int green = rng.nextInt(256);
        int blue = rng.nextInt(256);

        stroke(green, blue);
        for (int i = 1; i <= 3; i++) {
            fill(12, 242, 242, 100f / sq(i));
            circle(xCenter, yCenter, fft.getBand(index) * 3 * sq(i));
        }
    }

    private void drawButtons() {
        if (mouseOver(pauseCoordinates[0],
                pauseCoordinates[1],
                pauseButton.width,
                pauseButton.height)) {
            tint(225, 175);
        } else {
            tint(255, 255);
        }
        if (isPaused) {
            image(playButton, pauseCoordinates[0], pauseCoordinates[1]);
        } else {
            image(pauseButton, pauseCoordinates[0], pauseCoordinates[1]);
        }
    }

    private boolean mouseOver ( int x, int y, int width, int height){
       if (mouseX > x &&
               mouseX < (x + width) &&
               mouseY > y &&
               mouseY < (y + height)) {
           return true;
       } else {
           return false;
       }
    }

    public void mouseClicked() {
            if(mouseOver (pauseCoordinates[0],
                    pauseCoordinates[1],
                    pauseButton.width,
                    pauseButton.height)) {
                togglePause();
            }
        }

    private void togglePause() {

        isPaused = !isPaused;
        if(isPaused) {
            audio.pause();
        }
        else {
            audio.play();
        }
    }
}