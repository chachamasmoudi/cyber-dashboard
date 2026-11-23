package cyber.server.detection;

import cyber.common.Alerte;
import cyber.common.LogEvent;
import cyber.common.Severite;
import cyber.common.TypeEvenement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Détecte une IP qui touche plus de {@code seuil} ports différents en {@code fenetreMs} ms.
 */
public class ScanPorts implements Regle {

    private final int seuil;
    private final long fenetreMs;
    // Pour chaque IP : paires {instant, port}
    private final Map<String, Deque<long[]>> acces = new ConcurrentHashMap<>();

    public ScanPorts(int seuil, long fenetreMs) {
        this.seuil = seuil;
        this.fenetreMs = fenetreMs;
    }

    @Override
    public Optional<Alerte> analyser(LogEvent e) {
        if (e.getType() != TypeEvenement.CONNEXION_PORT) {
            return Optional.empty();
        }
        Deque<long[]> file = acces.computeIfAbsent(e.getIp(), k -> new ArrayDeque<>());
        synchronized (file) {
            long maintenant = System.currentTimeMillis();
            file.addLast(new long[] {maintenant, e.getPort()});
            while (!file.isEmpty() && maintenant - file.peekFirst()[0] > fenetreMs) {
                file.pollFirst();
            }
            Set<Long> portsDistincts = new HashSet<>();
            for (long[] a : file) {
                portsDistincts.add(a[1]);
            }
            if (portsDistincts.size() > seuil) {
                int nb = portsDistincts.size();
                file.clear();
                return Optional.of(new Alerte(Severite.ELEVEE, e.getIp(), "ScanPorts",
                        nb + " ports différents testés en moins de " + (fenetreMs / 1000) + " s"));
            }
        }
        return Optional.empty();
    }
}