package cyber.server;

import cyber.server.udp.RecepteurUDP;

/** Point d'entrée du serveur central (version de l'étape 1 : UDP seulement). */
public class Serveur {

    public static final int PORT_UDP = 9999;

    public static void main(String[] args) {
        Thread threadUdp = new Thread(new RecepteurUDP(PORT_UDP), "recepteur-udp");
        threadUdp.start();
        System.out.println("[Serveur] Prêt. Ctrl+C pour arrêter.");
    }
}