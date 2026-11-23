package cyber.server.udp;

import cyber.common.LogEvent;
import cyber.server.detection.MoteurDetection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Reçoit les journaux envoyés par les agents en UDP.
 * Un thread reçoit les paquets, un pool de threads les analyse.
 * Rappel : UDP ne garantit pas la livraison (un paquet peut se perdre).
 */
public class RecepteurUDP implements Runnable {

    private final int port;
    private final MoteurDetection moteur;
    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    private volatile boolean actif = true;
    private volatile DatagramSocket socket;

    public RecepteurUDP(int port, MoteurDetection moteur) {
        this.port = port;
        this.moteur = moteur;
    }

    @Override
    public void run() {
        try (DatagramSocket s = new DatagramSocket(port)) {
            socket = s;
            byte[] tampon = new byte[1024];
            System.out.println("[UDP] En écoute sur le port " + port);
            while (actif) {
                DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
                s.receive(paquet);
                String ligne = new String(paquet.getData(), 0, paquet.getLength(), StandardCharsets.UTF_8);
                pool.submit(() -> traiter(ligne));
            }
        } catch (SocketException e) {
            if (actif) {
                System.err.println("[UDP] Erreur socket : " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("[UDP] Erreur d'entrée/sortie : " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private void traiter(String ligne) {
        try {
            moteur.traiter(LogEvent.parse(ligne));
        } catch (IllegalArgumentException e) {
            System.err.println("[UDP] Paquet ignoré : " + e.getMessage());
        }
    }

    public void arreter() {
        actif = false;
        DatagramSocket s = socket;
        if (s != null) {
            s.close();
        }
    }
}