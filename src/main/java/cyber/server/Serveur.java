package cyber.server;

import cyber.server.detection.ForceBrute;
import cyber.server.detection.HorsHoraires;
import cyber.server.detection.MoteurDetection;
import cyber.server.detection.PublicateurAlertes;
import cyber.server.detection.PublicateurConsole;
import cyber.server.detection.ScanPorts;
import cyber.server.jms.PublicateurJMS;
import cyber.server.rmi.ServiceAdminImpl;
import cyber.server.udp.RecepteurUDP;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

/**
 * Point d'entrée du serveur central (étape 4 : UDP + détection + RMI + JMS).
 * Option : --sans-jms  → affiche les alertes en console (pour tester sans ActiveMQ).
 */
public class Serveur {

    public static final int PORT_UDP = 9999;
    public static final int PORT_RMI = 1099;
    public static final String NOM_RMI = "ServiceAdmin";

    public static void main(String[] args) throws Exception {
        boolean sansJms = Arrays.asList(args).contains("--sans-jms");

        PublicateurAlertes publicateur = sansJms ? new PublicateurConsole() : new PublicateurJMS();

        MoteurDetection moteur = new MoteurDetection(
                List.of(new ForceBrute(5, 30_000),     // plus de 5 échecs en 30 s
                        new ScanPorts(10, 10_000),     // plus de 10 ports en 10 s
                        new HorsHoraires()),
                publicateur);

        // 1) Réception des logs UDP dans un thread dédié
        Thread threadUdp = new Thread(new RecepteurUDP(PORT_UDP, moteur), "recepteur-udp");
        threadUdp.start();

        // 2) Service RMI pour l'administration
        Registry registre = LocateRegistry.createRegistry(PORT_RMI);
        registre.rebind(NOM_RMI, new ServiceAdminImpl(moteur));
        System.out.println("[RMI] Service '" + NOM_RMI + "' publié sur le port " + PORT_RMI);

        System.out.println("[Serveur] Prêt. Ctrl+C pour arrêter.");
    }
}