package kr.sanchez.specdeser.core;

public class Foo {
    private int boo(int x, int y) {
        return 32 + x + y;
    }

    int foo() {
        int r = 0;
        for (int i = 0; i < 1000000; i++) {
            for (int j = 0; j < 10; j++) {
                int b = boo(i,j);
                r += i + b;
                System.out.print("." + b);
            }
        }
        return r;
    }


    public static void main(String[] args) {
        Foo f = new Foo();

        while (true) {
            if (f.foo() != 0) {
                continue;
            }
            break;
        }

    }
}
