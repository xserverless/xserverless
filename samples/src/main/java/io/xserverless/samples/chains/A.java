package io.xserverless.samples.chains;

public class A {
    public void ab() {
        System.out.println("A.ab() start.");
        new B().bc();
        System.out.println("A.ab() end.");
    }

    public void ac() {
        System.out.println("A.ac() start.");
        new C().c();
        System.out.println("A.ac() end.");
    }

    public void ad() {
        System.out.println("A.ad() start.");
        new D().d();
        System.out.println("A.ad() end.");
    }
}
