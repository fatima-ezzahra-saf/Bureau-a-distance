import java.io.IOException;

public class ServeurTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Serveur serveur = new Serveur(8000);
        System.out.println("serveur commence");

        serveur.startReceivingScreenshots(); // Décommentez pour recevoir la capture d'écran

        //serveur.receiveMousePosition(8000); // Commentez si non utilisé pour ce test
        System.out.println("Événement prêt à être reçu");
    }
}
