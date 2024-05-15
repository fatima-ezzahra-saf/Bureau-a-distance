import java.io.IOException;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 8000);

        // Envoyer la capture d'écran
        client.sendScreenshot();

        // Écouter les événements envoyés par le serveur
        new Thread(() -> client.listenForEvents()).start();

        System.out.println("Client en écoute des événements");
    }
}
