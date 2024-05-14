import java.io.IOException;

public class ServeurTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Serveur serveur = new Serveur();
        System.out.println("serveur commence");
        //serveur.receiveScreenshot(8000);
        serveur.receiveMousePosition(8000);
        System.out.println("message recu");
    }
}
