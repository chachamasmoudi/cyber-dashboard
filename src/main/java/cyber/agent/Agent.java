package cyber.agent;

import cyber.common.LogEvent;
import cyber.common.TypeEvenement;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Agent + simulateur d'attaques : envoie des journaux au serveur en UDP.
 *
 * Usage : Agent <hôteServeur> <portUdp> <scenario> [ipSimulée]
 * Scénarios : normal | bruteforce | portscan | nuit
 */
public class Agent {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage : Agent <hote> <port> <normal|bruteforce|portscan|nuit> [ip]");
            return;
        }
        String hote = args[0];
        int port = Integer.parseInt(args[1]);
        String scenario = args[2];
        String ip = args.length > 3 ? args[3] : "10.0.0.5";

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serveur = InetAddress.getByName(hote);
            Envoi envoi = new Envoi(socket, serveur, port);

            switch (scenario) {
                case "normal":
                    for (int i = 0; i < 5; i++) {
                        envoi.envoyer(new LogEvent(ip, TypeEvenement.LOGIN_OK, 22, System.currentTimeMillis()));
                        Thread.sleep(500);
                    }
                    break;
                case "bruteforce":
                    // 8 échecs rapprochés : dépasse le seuil de 5 en 30 s
                    for (int i = 0; i < 8; i++) {
                        envoi.envoyer(new LogEvent(ip, TypeEvenement.LOGIN_ECHEC, 22, System.currentTimeMillis()));
                        Thread.sleep(200);
                    }
                    break;
                case "portscan":
                    // 15 ports différents en peu de temps : dépasse le seuil de 10 en 10 s
                    for (int p = 1000; p < 1015; p++) {
                        envoi.envoyer(new LogEvent(ip, TypeEvenement.CONNEXION_PORT, p, System.currentTimeMillis()));
                        Thread.sleep(100);
                    }
                    break;
                case "nuit":
                    // Connexion réussie à 3h du matin
                    long troisHeures = LocalDate.now().atTime(3, 0)
                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    envoi.envoyer(new LogEvent(ip, TypeEvenement.LOGIN_OK, 22, troisHeures));
                    break;
                default:
                    System.out.println("Scénario inconnu : " + scenario);
            }
        }
        System.out.println("[Agent] Scénario '" + scenario + "' terminé.");
    }

    /** Petit utilitaire d'envoi d'un datagramme UDP. */
    private static class Envoi {
        private final DatagramSocket socket;
        private final InetAddress serveur;
        private final int port;

        Envoi(DatagramSocket socket, InetAddress serveur, int port) {
            this.socket = socket;
            this.serveur = serveur;
            this.port = port;
        }

        void envoyer(LogEvent e) throws Exception {
            byte[] donnees = e.toLigne().getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(donnees, donnees.length, serveur, port));
            System.out.println("[Agent] Envoyé : " + e.toLigne());
        }
    }
}