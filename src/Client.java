import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Client {

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    public Client(String host, int port) {
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(host);
            serverPort = port;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendScreenshot() {
        try {
            // Capture d'écran et envoi
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "png", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            int maxPacketSize = 1024;

            for (int i = 0; i < imageBytes.length; i += maxPacketSize) {
                int endIndex = Math.min(i + maxPacketSize, imageBytes.length);
                byte[] packetData = Arrays.copyOfRange(imageBytes, i, endIndex);
                DatagramPacket packet = new DatagramPacket(packetData, packetData.length, serverAddress, serverPort);
                socket.send(packet);
            }

            System.out.println("Capture d'écran envoyée avec succès.");
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    public void startMouseListening() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost"); // Remplacer par l'adresse IP du serveur

        while (true) {
            Point mousePoint = MouseInfo.getPointerInfo().getLocation();
            String message = mousePoint.getX() + "," + mousePoint.getY();
            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9876);
            socket.send(sendPacket);
            System.out.println("Position envoyée : " + message);

            try {
                Thread.sleep(1000); // Envoi toutes les secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
