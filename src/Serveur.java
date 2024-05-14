import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.imageio.ImageIO;

public class Serveur {

    public void receiveScreenshot(int port) {
        try {
            DatagramSocket socket = new DatagramSocket(port);

            // Créer un tampon pour recevoir les données
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Recevoir les données par paquets
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                socket.receive(packet);
                byteArrayOutputStream.write(packet.getData(), 0, packet.getLength());
                if (packet.getLength() < buffer.length) {
                    break; // Fin de la réception des données
                }
            }

            // Convertir les données en objet image
            byte[] imageData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
            BufferedImage screenshot = ImageIO.read(byteArrayInputStream);

            // Sauvegarder la capture d'écran sur le serveur
            ImageIO.write(screenshot, "png", new File("C:\\Users\\AdMin\\Downloads\\screenshot.png"));

            System.out.println("Capture d'écran sauvegardée avec succès.");

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receiveMousePosition(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket(9876); // Écoute sur le port 9876

        byte[] receiveData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

            System.out.println("Position reçue : " + message);
        }
    }

}
