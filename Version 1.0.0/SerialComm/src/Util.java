import java.io.IOException;

public class Util {

    public static byte[] toByte(int[] data) throws IOException {

        byte[] bytes = new byte[data.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) data[i];
        }
        return bytes;
    }
}
