package io.xserverless.samples.chains;

public class B {
    public void bc() {
        System.out.println("B.bc() start.");
        new C().c();
        System.out.println("B.bc() end.");
    }
    public void bd() {
        System.out.println("B.bd() start.");
        new D().d();
        System.out.println("B.bd() end.");
    }
}
