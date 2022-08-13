package SnowFlake;

import Engine2D.Dot;
import Engine2D.Scene;
import Engine2D.ShapesObject;
import UnityMath.Vector2;

import java.awt.*;
import java.util.Random;

public class TriangleFractalThread extends Thread{
    private final Vector2 P0;
    private final Vector2 P1;
    private final Vector2 P2;
    private final int start;
    private final int end;
    private final ShapesObject object;

    TriangleFractalThread(Vector2 P0, Vector2 P1, Vector2 P2, int s, int e, ShapesObject o){
        this.P0 = new Vector2(P0);
        this.P1 = new Vector2(P1);
        this.P2 = new Vector2(P2);
        this.object = o;
        this.start = s;
        this.end = e;
    }

    @Override
    public void run() {
        super.run();
        Random rand = new Random();
        Vector2 d1 = new Vector2(rand.nextInt(Scene.WIDTH), rand.nextInt(Scene.HEIGHT));
        Vector2 d2 = P0;
        Scene.fromSceneCoord(d1);
        for(int i = start; i < end; i++){
            switch (rand.nextInt(6)+1){
                case 1, 2 -> d2 = P0;
                case 3, 4 -> d2 = P1;
                case 5, 6 -> d2 = P2;
            }
            d1 = new Vector2((d1.x + d2.x)/2,(d1.y + d2.y)/2);
            synchronized (object) {
                object.add(new Dot(new Vector2(d1), 2, Color.BLUE));
            }
        }
    }
}
