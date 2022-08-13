package SnowFlake;

import Engine2D.Dot;
import Engine2D.Scene;
import Engine2D.ShapesObject;
import UnityMath.Vector2;

import java.awt.*;
import java.util.Random;

public class LeafFractalThread extends Thread{
    private final int start;
    private final int end;
    private final ShapesObject object;

    LeafFractalThread(int s, int e, ShapesObject o){
        this.start = s;
        this.end = e;
        this.object = o;
    }
    @Override
    public void run() {
        super.run();
            Random rand = new Random();
            Vector2 d = new Vector2(0, 0);
            Scene.fromSceneCoord(d);
            int size = 0;
            for (int i = start; i < end; i++) {
                var r = rand.nextDouble();
                if (r < 0.01) {//f1
                    d = new Vector2(0, (0.16f * d.y) + size);
                } else if (r < 0.86) {//f2
                    d = new Vector2((0.85f * d.x + 0.04f * d.y) + size, (-0.04f * d.x + 0.85f * d.y + 1.6f) + size);
                } else if (r < 0.93) {//f3
                    d = new Vector2((0.2f * d.x - 0.26f * d.y) + size, (0.23f * d.x + 0.22f * d.y + 1.6f) + size);
                } else { //f4
                    d = new Vector2((-0.15f * d.x + 0.28f * d.y) + size, (0.26f * d.x + 0.24f * d.y + 0.44f) + size);
                }
                synchronized (object) {
                    object.add(new Dot(new Vector2(d.x * 65, d.y * 37 - 252), 2, Color.GREEN));
                }
            }
    }
}
