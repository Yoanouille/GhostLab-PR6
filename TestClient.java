import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket(InetAddress.getByName("localhost"), 6667);
            byte[] data = new byte[256];
            int len = s.getInputStream().read(data, 0, 256);
            String mess = new String(data, 0, len);
            System.out.println(mess);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
