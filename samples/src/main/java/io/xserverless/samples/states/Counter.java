package io.xserverless.samples.states;

public class Counter {
    private int n = 0;

    public void increase() {
        n++;
    }

    public void output() {
        System.out.println(n);
    }
}
