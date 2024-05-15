import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
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

    public void listenForEvents() {
        try {
            DatagramSocket eventSocket = new DatagramSocket(9000); // Port pour écouter les événements
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                eventSocket.receive(packet);
                String event = new String(packet.getData(), 0, packet.getLength());
                processEvent(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processEvent(String event) {
        try {
            String[] parts = event.split(",");
            String eventType = parts[0];
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            Robot robot = new Robot();

            switch (eventType) {
                case "MOUSE_CLICKED":
                    robot.mouseMove(x, y);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    System.out.println("Clic de souris appliqué aux coordonnées : (" + x + ", " + y + ")");
                    break;
                // Ajoutez d'autres événements et leurs actions ici
                default:
                    System.out.println("Événement non reconnu : " + event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startScreenshotSending() {
        new Thread(() -> {
            while (true) {
                sendScreenshot();
                try {
                    Thread.sleep(1000); // Prendre une capture d'écran toutes les secondes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 8000);

        // Démarrer l'envoi continu des captures d'écran
        client.startScreenshotSending();

        // Écouter les événements envoyés par le serveur
        new Thread(client::listenForEvents).start();

        System.out.println("Client en écoute des événements");
    }
}
