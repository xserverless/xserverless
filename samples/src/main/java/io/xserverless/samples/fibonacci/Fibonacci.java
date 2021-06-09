package io.xserverless.samples.fibonacci;

public class Fibonacci {
    public static void print(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("the input argument n must be equal or greater than 0. input n: " + n);
        }

        int a = 0, b = 1;
        if (n > 0) {
            System.out.println(0);
        }
        if (n > 1) {
            System.out.println(1);
        }

        for (int i = 2; i < n; i++) {
            int c = a + b;
            a = b;
            b = c;
            System.out.println(c);
        }
    }
}
