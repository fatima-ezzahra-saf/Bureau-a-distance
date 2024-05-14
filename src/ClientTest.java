import java.io.IOException;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 8000);
        //client.sendScreenshot(); // Commentez l'envoi de la capture d'écran
        client.startMouseListening(); // Ajoutez l'envoi de la position de la souris
        System.out.println("message envoye");
    }
}
