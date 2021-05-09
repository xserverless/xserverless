package io.xserverless.function.command.sample;

public class SampleClass {
    private int a;

    public static void main(String[] args) {
        SampleClass sampleClass = new SampleClass();
        for (int i = 0; i < 1000; i++) {
            sampleClass.increase();
        }
        sampleClass.output();
    }

    public void increase() {
        a++;
    }

    public void output() {
        System.out.println(a);
    }
}
