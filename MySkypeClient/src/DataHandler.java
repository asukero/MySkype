import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class DataHandler extends Thread {
    public static byte[] decompressData(byte[] data) {
        try {
            GZIPInputStream gzipInputStream
                    = new GZIPInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream byteArrayOutputStream
                    = new ByteArrayOutputStream();

            for (;;) {
                int byteRead = gzipInputStream.read();

                if (byteRead == -1) {
                    break;
                }

                byteArrayOutputStream.write((byte) byteRead);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (java.io.IOException IOException) {
            System.out.println("Decompress error: " + IOException.getMessage());

            return null;
        }
    }

    public static ByteArrayOutputStream compressData(byte[] buffer)
            throws IOException {
        ByteArrayOutputStream byteArrayOutputStream
                = new ByteArrayOutputStream();
        GZIPOutputStream GZIPOutputStream
                = new GZIPOutputStream(byteArrayOutputStream);

        GZIPOutputStream.write(buffer);
        GZIPOutputStream.flush();
        GZIPOutputStream.close();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();

        return byteArrayOutputStream;
    }

    protected void stopHandler() {
        try {
            this.join();
        } catch (InterruptedException interruptedException) {
            System.out.println("Join error: "
                    + interruptedException.getMessage());
        }
    }
}
