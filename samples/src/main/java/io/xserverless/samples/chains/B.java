package io.xserverless.samples.chains;

public class B {
    public void bc() {
        System.out.println("B.bc() start.");
        new C().c();
        System.out.println("B.bc() end.");
    }
}
