package com.example.stlviewer.control;

import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.model.Triangle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PolyhedronController implements Runnable{

    private Polyhedron polyhedron;

    // BlockingQueue to hold objects for calculation
    private BlockingQueue<Triangle> blockingQueue = new LinkedBlockingQueue<>();
    private volatile boolean isReadingFinished = false;

    public PolyhedronController (Polyhedron polyhedron)
    {
        this.polyhedron = polyhedron;
    }

    public PolyhedronController() {
        this.polyhedron = new Polyhedron();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if(isReadingFinished && blockingQueue.isEmpty()){
                    break;
                } else if (!blockingQueue.isEmpty()) {
                    Triangle triangle = blockingQueue.take();
                    polyhedron.addTriangle(triangle);
                }else{
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setReadingFinished(boolean readingFinished){
        this.isReadingFinished = readingFinished;
    }

    public void calculateVolume ()
    {
        polyhedron.calculateVolume();
    }

    public void calculateSurfaceArea ()
    {
        polyhedron.calculateSurfaceArea();
    }

    public void addTriangle(Triangle triangle) {
        blockingQueue.add(triangle);
    }

    public Polyhedron getPolyhedron ()
    {
        return polyhedron;
    }
}
