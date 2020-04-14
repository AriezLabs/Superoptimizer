package parser;

public class Util {
    public static int byteArrayToLeInt(byte[] encodedValue) {
        int value = (encodedValue[3] << (Byte.SIZE * 3));
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE);
        value |= (encodedValue[0] & 0xFF);
        return value;
    }
    public static long byteArrayToLeLong(byte[] encodedValue) {
        long value = ((long) encodedValue[7] << (Byte.SIZE * 7));
        value |= ((long) encodedValue[6] & 0xFF) << (Byte.SIZE * 6);
        value |= ((long) encodedValue[5] & 0xFF) << (Byte.SIZE * 5);
        value |= ((long) encodedValue[4] & 0xFF) << (Byte.SIZE * 4);
        value |= (encodedValue[3] << (Byte.SIZE * 3));
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE);
        value |= (encodedValue[0] & 0xFF);
        return value;
    }
    public static int signExtend(int number, int bits) {
        return (number << (32 - bits)) >> (32 - bits);
    }
}
