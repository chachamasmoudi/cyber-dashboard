package cyber.server;

import cyber.server.detection.ForceBrute;
import cyber.server.detection.MoteurDetection;
import cyber.server.detection.PublicateurAlertes;
import cyber.server.detection.PublicateurConsole;
import cyber.server.detection.ScanPorts;
import cyber.server.udp.RecepteurUDP;
import cyber.server.detection.HorsHoraires;

import java.util.List;

/** Point d'entrée du serveur central (étape 2 : UDP + détection). */
public class Serveur {

    public static final int PORT_UDP = 9999;

    public static void main(String[] args) {
        PublicateurAlertes publicateur = new PublicateurConsole();

        MoteurDetection moteur = new MoteurDetection(
                List.of(new ForceBrute(5, 30_000),     // plus de 5 échecs en 30 s
                        new ScanPorts(10, 10_000),     // plus de 10 ports en 10 s
                        new HorsHoraires()),
                publicateur);

        Thread threadUdp = new Thread(new RecepteurUDP(PORT_UDP, moteur), "recepteur-udp");
        threadUdp.start();
        System.out.println("[Serveur] Prêt. Ctrl+C pour arrêter.");
    }
}