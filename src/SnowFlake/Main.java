package SnowFlake;

import Engine2D.*;
import Engine2D.Rectangle;
import UnityMath.Vector2;
import UnityMath.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 800;
    public static Scene scene;

    public static void main(String[] args) {
        System.out.println("Hello world!");

        scene = new Scene(WIDTH, HEIGHT);
        ShapesObject s = SnowFlakeGenerator(1, 200, Color.MAGENTA);/*Generate Fractal SnowFlake*/
        scene.add(s);

        JFrame frame = new JFrame("2DTest");
        frame.setSize(new Dimension(WIDTH + 100, HEIGHT + 100));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.add(scene);
        frame.setVisible(true);
        scene.setCoordVisible(true);
        scene.repaint();

        JButton b = new JButton("change");
        final int[] d = {3};

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                /*Change num of Fractal vertices*/
                Scene.objects.clear();
                scene.add(SnowFlakeGenerator(d[0], 200, Color.MAGENTA));
                System.out.println(d[0]);
                d[0]++;
                frame.requestFocus();
            }
        });
        b.setPreferredSize(new Dimension(80, 20));
        frame.add(b);

        Timer t = new Timer(10, (e) -> {
            /*Rotate Fractal in Zaxis*/
            Scene.objects.get(0).setAngZ(Scene.objects.get(0).getAngZ() + 1);
            scene.repaint();
        });
        scene.add(TriangleFractal(new Vector2(-200,0), new Vector2(200,0), new Vector2(0,200)));// Generate Fractal Triangles
        scene.add(LeafFractal()); // Generate Fractal Leaf
        //scene.repaint();
        //t.start();
    }

    /**Fractal generator
     *
     * Create new ShapesObject compute where must tobe fractals centers.
     * To do this computes circle with -n vertices and save this values.
     * Use function Fractal() to generate fractal's lines from 0 to n-1 point,
     * and generate one more fractals' line for n-1 to 0 point.
     * Return new Fractal in ShapesObject.*/
    public static ShapesObject SnowFlakeGenerator(int n, int size, Color c){
        ShapesObject newO = new ShapesObject("SnowFlake", 2);

        ShapesObject ttt = new ShapesObject();

        int radius = size/n;
        Vector2[] dots = new Vector2[n+1];
        int j = 0;
        int[] angles = new int[n+1];
        double angleSt = 0.0;
        if(n == 3) angleSt = 0.5;
        for (double angle = angleSt; angle < (2.0 * Math.PI); angle += (2 * Math.PI) / n)
        {
            float PositionX = (float)(((radius * n) * Math.cos(angle + (Math.PI / n)/(n / 2))));
            float PositionY = (float)(((radius * n) * Math.sin(angle + (Math.PI / n)/(n / 2))));

            dots[j] = new Vector2(PositionX, PositionY);

            angles[j] = (int)( angle * 180/Math.PI);
            j++;
        }

        for(int i = 0; i < n-1; ++i){
            ttt.add(new Engine2D.Rectangle(5, dots[i], c));

            var tmp = new Line(dots[i], dots[i+1], new Vector2(0,0), c);

            tmp = LineFix(tmp);
            if(angles[i] < 135) {
                tmp.angZ += 180;
            }

            newO.add(tmp);
        }
        ttt.add(new Rectangle(5, dots[n-1], c));
        var tmp = new Line(dots[n-1], dots[0], new Vector2(0,0), c);
        tmp = LineFix(tmp);
        if(angles[n-1] < 135 || n == 3) {
            tmp.angZ += 180;
        }

        newO.add(tmp);

        Fractal(3, newO);
        newO.center = new Vector2(0,0);

        return newO;
    }

    /**Fix Line if angle >= 180.
     * Do it with those lines which rotated on 180 when build them fractal.*/
    public static Line LineFix(Line tmp){
        return new Line(tmp.start.sub(tmp.center), tmp.end.sub(tmp.center), new Vector2(tmp.center), tmp.color);
    }

    /**Create Fractal from line
     * In recursive obtain dots line's center+line's norm, create 4 lines:
     *   1. from start to left center 1/2 source line's and source lines center's
     *   2. from right center 1/2 source line's and source lines center's to end
     *   3. from left center 1/2 source line's and source lines center's to line's center+line's norm
     *   4. from right center 1/2 source line's and source lines center's to line's center+line's norm
     * Save angle source line's for all new lines.
     * Repeat for all 4 lines in recursive with n-1 step.*/
    public static ArrayList<AbstractShape> Fractal(int n, AbstractShape shape){
        ArrayList<AbstractShape> newL = new ArrayList<>();
        if(n == 0){
            newL.add(shape);
            return newL;
        }

        var M1 = new Vector2(shape.vertices.get(0));
        var M2 = new Vector2(shape.vertices.get(1));
        Vector2 vec = M2.sub(M1);

        var vecN = new Vector2(-vec.y / vec.x, 1);
        var tmp = Math.sqrt(vecN.x*vecN.x + vecN.y*vecN.y);
        vecN.x = (float)(vecN.x * shape.size/3 / tmp);
        vecN.y = (float)(vecN.y * shape.size/3 / tmp);
        vecN.add(shape.center);

        Line tmp1 = new Line(shape.vertices.get(0), shape.center, new Vector2(shape.position), Color.GREEN);
        Line tmp2 = new Line(shape.vertices.get(1), shape.center, new Vector2(shape.position), Color.GREEN);

        Line l1 = new Line(shape.vertices.get(0), tmp1.center, new Vector2(shape.position), shape.color);
        Line l2 = new Line(shape.vertices.get(1), tmp2.center, new Vector2(shape.position), shape.color);
        Line l3 = new Line(tmp1.center, vecN, new Vector2(shape.position), shape.color);
        Line l4 = new Line(tmp2.center, vecN, new Vector2(shape.position), shape.color);

        l1.angZ = shape.angZ;
        l2.angZ = shape.angZ;
        l3.angZ = shape.angZ;
        l4.angZ = shape.angZ;

        l1.angX = shape.angX;
        l2.angX = shape.angX;
        l3.angX = shape.angX;
        l4.angX = shape.angX;

        l1.angY = shape.angY;
        l2.angY = shape.angY;
        l3.angY = shape.angY;
        l4.angY = shape.angY;

        newL.addAll(Fractal(n - 1, l1));
        newL.addAll(Fractal(n - 1, l2));
        newL.addAll(Fractal(n - 1, l3));
        newL.addAll(Fractal(n - 1, l4));

        return newL;
    }

    /**Invoke Fractal create function for all lines in ShapeObject, and update this ShapeObject*/
    public static void Fractal(int n, ShapesObject object){
        ArrayList<AbstractShape> t = new ArrayList<>();
        for(var shape: object.body) {
            t.addAll(Fractal(n, shape));
        }
        object.body.clear();
        object.addAll(t);
    }

    /**Create Triangle Fractal
     * Have Triangle's points P0, P1, P2 and one random point d.
     * When rand = 1,2 add point in center between d and P0, set d = this new point.
     * When rand = 3,4 add point in center between d and P1, set d = this new point.
     * When rand = 5,6 add point in center between d and P2, set d = this new point.
     * Repeat 40000 times, in 4 Threads;
     * */
    public static ShapesObject TriangleFractal(Vector2 P0, Vector2 P1, Vector2 P2){
        ShapesObject object = new ShapesObject("Serpinskii Triangle", 1);
        Random rand = new Random();
        Vector2 d1 = new Vector2(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        Vector2 d2 = P0;
        Scene.fromSceneCoord(d1);
        for(int i = 0; i < 1000; i++){
            switch (rand.nextInt(6)+1){
                case 1, 2 ->{
                    d2 = P0;
                }
                case 3, 4 ->{
                    d2 = P1;
                }
                case 5, 6 ->{
                    d2 = P2;
                }
            }
            d1 = new Vector2((d1.x + d2.x)/2,(d1.y + d2.y)/2);
            object.add(new Dot(new Vector2(d1), 2, Color.BLUE));
        }
        return object;
    }

    /**Leaf
     * Do affines transform with x,y.
     * If rand < 0.01 then use f1 transform.
     * If rand < 0.86 then use f2 transform.
     * If rand < 0.93 then use f3 transform.
     * Else then use f4 transform.
     * add all dots in object.
     * Repeat 40000 times in 4 Threads.
     * */
    public static ShapesObject LeafFractal(){
        ShapesObject object = new ShapesObject("Serpinskii Triangle", 1);
        /*Random rand = new Random();
        Vector2 d = new Vector2(0,0);
        Scene.fromSceneCoord(d);
        int size = 0;*/
        synchronized (object) {//TODO realize multitreading
        var t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                Vector2 d = new Vector2(0,0);
                Scene.fromSceneCoord(d);
                int size = 0;
                for(int i = 0; i < 10000; i++){
                    /*this.center = new Vector2((this.start.x + this.end.x)/2,(this.start.y + this.end.y)/2);*/
                    var r = rand.nextDouble();
                    if(r < 0.01){//f1
                        d = new Vector2(0,(0.16f*d.y)+size);
                    }else if(r<0.86){//f2
                        d = new Vector2((0.85f*d.x + 0.04f*d.y)+size,(-0.04f*d.x+0.85f*d.y+1.6f)+size);
                    }else if(r <0.93){//f3
                        d = new Vector2((0.2f*d.x-0.26f*d.y)+size,(0.23f*d.x+0.22f*d.y+1.6f)+size);
                    }else{ //f4
                        d = new Vector2((-0.15f*d.x+0.28f*d.y)+size,(0.26f*d.x+0.24f*d.y+0.44f)+size);
                    }
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    object.add(new Dot(new Vector2(d.x*65, d.y*37-252), 2, Color.GREEN));
                    object.notifyAll();
                }
            }
        });


            var t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Random rand = new Random();
                    Vector2 d = new Vector2(0, 0);
                    Scene.fromSceneCoord(d);
                    int size = 0;
                    for (int i = 10000; i < 20000; i++) {
                        /*this.center = new Vector2((this.start.x + this.end.x)/2,(this.start.y + this.end.y)/2);*/
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
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        object.add(new Dot(new Vector2(d.x*65, d.y*37-252), 2, Color.GREEN));
                        object.notifyAll();
                    }
                }
            });

            var t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Random rand = new Random();
                    Vector2 d = new Vector2(0, 0);
                    Scene.fromSceneCoord(d);
                    int size = 0;
                    for (int i = 20000; i < 30000; i++) {
                        /*this.center = new Vector2((this.start.x + this.end.x)/2,(this.start.y + this.end.y)/2);*/
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
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        object.add(new Dot(new Vector2(d.x*65, d.y*37-252), 2, Color.GREEN));
                        object.notifyAll();
                    }
                }
            });

            var t4 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Random rand = new Random();
                    Vector2 d = new Vector2(0, 0);
                    Scene.fromSceneCoord(d);
                    int size = 0;
                    for (int i = 30000; i < 40000; i++) {
                        /*this.center = new Vector2((this.start.x + this.end.x)/2,(this.start.y + this.end.y)/2);*/
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
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        object.add(new Dot(new Vector2(d.x*65, d.y*37-252), 2, Color.GREEN));
                        object.notifyAll();
                    }
                }
            });

                t1.start();
                t2.start();
                t3.start();
                t4.start();
        }

        /*for(int i = 0; i < 40000; i++){
            var r = rand.nextDouble();
                if(r < 0.01){//f1
                    d = new Vector2(0,(0.16f*d.y)+size);
                }else if(r<0.86){//f2
                    d = new Vector2((0.85f*d.x + 0.04f*d.y)+size,(-0.04f*d.x+0.85f*d.y+1.6f)+size);
                }else if(r <0.93){//f3
                    d = new Vector2((0.2f*d.x-0.26f*d.y)+size,(0.23f*d.x+0.22f*d.y+1.6f)+size);
                }else{ //f4
                    d = new Vector2((-0.15f*d.x+0.28f*d.y)+size,(0.26f*d.x+0.24f*d.y+0.44f)+size);
                }
            object.add(new Dot(new Vector2(d.x*65, d.y*37-252), 2, Color.GREEN));
            }*/
        //object.add(new Dot(new Vector2(100,100), Color.BLUE));

        return object;
    }

}