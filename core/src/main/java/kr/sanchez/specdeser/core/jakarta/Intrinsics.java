package kr.sanchez.specdeser.core.jakarta;

public class Intrinsics {

    private final byte[] BITSHIFT = new byte[]{24,16,8,0};

    public int indexOfConstant(byte[] input, int startPos, int inputSize, int i1) {
        int found = -1;
        byte firstByte = (byte) (i1 >> BITSHIFT[0]);
        while (startPos < inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos -1;
            boolean match = true;
            for (int i = 1; match && i < 4 && startPos < inputSize; i++) {
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

    private int getPositionOfByte(byte[] input, int startPos, byte symbol) {
        boolean found = input[startPos++] == symbol;
        while (!found && startPos < input.length) {
            found = input[startPos++] == symbol;
        }
        return startPos; // Returns the position of the next byte to the searched one.
    }
}
