import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Serveur {

    private DatagramSocket socket;
    private JFrame frame;
    private JLabel label;

    public Serveur(int port) throws IOException {
        socket = new DatagramSocket(port);
        initializeFrame();
    }

    private void initializeFrame() {
        frame = new JFrame("Capture d'écran");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        label = new JLabel();
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                sendMouseEvent("MOUSE_CLICKED", x, y);
            }
        });
    }

    public void receiveScreenshot() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                socket.receive(packet);
                byteArrayOutputStream.write(packet.getData(), 0, packet.getLength());
                if (packet.getLength() < buffer.length) {
                    break; // Fin de la réception des données
                }
            }

            byte[] imageData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
            BufferedImage screenshot = ImageIO.read(byteArrayInputStream);

            updateScreenshot(screenshot); // Mettre à jour l'image

            System.out.println("Capture d'écran reçue.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateScreenshot(BufferedImage screenshot) {
        label.setIcon(new ImageIcon(screenshot));
        frame.pack();
    }

    private void sendMouseEvent(String eventType, int x, int y) {
        try {
            InetAddress clientAddress = InetAddress.getByName("localhost");
            int clientPort = 9000;
            String event = eventType + "," + x + "," + y;
            byte[] buffer = event.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(packet);
            System.out.println("Événement envoyé : " + event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReceivingScreenshots() {
        new Thread(() -> {
            while (true) {
                receiveScreenshot();
                try {
                    Thread.sleep(50); // Attendre une seconde avant de recevoir la prochaine capture d'écran
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
