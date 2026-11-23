package cyber.server.udp;

import cyber.common.LogEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

/**
 * Reçoit les journaux envoyés par les agents en UDP.
 * Version de l'étape 1 : on se contente d'afficher les événements.
 */
public class RecepteurUDP implements Runnable {

    private final int port;

    public RecepteurUDP(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] tampon = new byte[1024];
            System.out.println("[UDP] En écoute sur le port " + port);
            while (true) {
                DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
                socket.receive(paquet); // bloque jusqu'à l'arrivée d'un paquet
                String ligne = new String(paquet.getData(), 0, paquet.getLength(), StandardCharsets.UTF_8);
                try {
                    LogEvent evenement = LogEvent.parse(ligne);
                    System.out.println("[UDP] Reçu : " + evenement);
                } catch (IllegalArgumentException e) {
                    System.err.println("[UDP] Paquet ignoré : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[UDP] Erreur : " + e.getMessage());
        }
    }
}