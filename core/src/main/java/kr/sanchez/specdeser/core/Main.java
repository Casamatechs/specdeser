package kr.sanchez.specdeser.core;

import kr.sanchez.specdeser.core.jakarta.metadata.Int1;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    private final byte[] BITSHIFT = new byte[]{24,16,8,0};

    public int indexOfConstant(byte[] input, int inputSize, int i1) {
        int found = -1;
        int startPos = 0;
        while (startPos < inputSize) {
            int ptr = startPos;
            boolean match = true;
            for (int i = 0; match && i < 4 && startPos < inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                if (b == 0) break;
                match = b == input[startPos++];
            }
            if (match) {
                found = ptr;
                break;
            }
        }
        return found;
    }

    private int boo(int x) {
        return 32 + x;
    }

    int foo(byte[] arr, int search) {
        int r = 0;
        for (int i = 0; i < 1000000; i++) {
            int b = indexOfConstant(arr,arr.length,search);
//            int b = boo(i);
            r += i + b;
            System.out.print("." + b);
        }
        return r;
    }


    public static void main(String[] args) {
        Main f = new Main();
        byte[] in1 = """
                {
                "id":"foo",
                "name":"aaa",
                "loc":41,
                "num":1234
                }
                """.replaceAll("\\s","").getBytes(StandardCharsets.UTF_8);
        int[] looks = new int[]{new Int1("id".getBytes(StandardCharsets.UTF_8), 2, 0).i1,
                                new Int1("foo".getBytes(StandardCharsets.UTF_8), 3, 0).i1,
                                new Int1("loc".getBytes(StandardCharsets.UTF_8), 3, 0).i1,
                                new Int1("41".getBytes(StandardCharsets.UTF_8), 2, 0).i1};

        while (true) {
            if (f.foo(in1, looks[ThreadLocalRandom.current().nextInt(0, 4)]) != 0) {
                continue;
            }
            break;
        }

    }
}
