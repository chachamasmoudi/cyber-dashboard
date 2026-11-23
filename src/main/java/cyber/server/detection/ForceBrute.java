package cyber.server.detection;

import cyber.common.Alerte;
import cyber.common.LogEvent;
import cyber.common.Severite;
import cyber.common.TypeEvenement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Détecte plus de {@code seuil} échecs de connexion en {@code fenetreMs} millisecondes
 * depuis la même IP (fenêtre glissante).
 */
public class ForceBrute implements Regle {

    private final int seuil;
    private final long fenetreMs;
    // Pour chaque IP : les instants (ms) de ses échecs récents
    private final Map<String, Deque<Long>> echecs = new ConcurrentHashMap<>();

    public ForceBrute(int seuil, long fenetreMs) {
        this.seuil = seuil;
        this.fenetreMs = fenetreMs;
    }

    @Override
    public Optional<Alerte> analyser(LogEvent e) {
        if (e.getType() != TypeEvenement.LOGIN_ECHEC) {
            return Optional.empty();
        }
        Deque<Long> instants = echecs.computeIfAbsent(e.getIp(), k -> new ArrayDeque<>());
        // Plusieurs threads peuvent traiter la même IP : on synchronise sur sa file.
        synchronized (instants) {
            long maintenant = System.currentTimeMillis();
            instants.addLast(maintenant);
            while (!instants.isEmpty() && maintenant - instants.peekFirst() > fenetreMs) {
                instants.pollFirst();
            }
            if (instants.size() > seuil) {
                int nb = instants.size();
                instants.clear(); // évite de ré-alerter à chaque événement suivant
                return Optional.of(new Alerte(Severite.CRITIQUE, e.getIp(), "ForceBrute",
                        nb + " échecs de connexion en moins de " + (fenetreMs / 1000) + " s"));
            }
        }
        return Optional.empty();
    }
}