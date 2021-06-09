package io.xserverless.samples.states;

public class Counter {
    private int n = 0;

    public void increase() {
        n++;
    }

    public void output() {
        System.out.println(n);
    }

    public double stateless() {
        return Math.random();
    }

    public void stateless2() {
        new Counter().output();
    }
}
