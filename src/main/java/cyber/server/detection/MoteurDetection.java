package cyber.server.detection;

import cyber.common.Alerte;
import cyber.common.LogEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cœur du serveur : applique les règles à chaque événement reçu,
 * et mémorise les IP bloquées par l'administrateur (le blocage sera appelé via RMI à l'étape 3).
 * Classe thread-safe : plusieurs threads l'appellent en même temps.
 */
public class MoteurDetection {

    private final List<Regle> regles;
    private final PublicateurAlertes publicateur;
    private final Set<String> ipBloquees = ConcurrentHashMap.newKeySet();

    private final AtomicLong evenementsRecus = new AtomicLong();
    private final AtomicLong evenementsIgnores = new AtomicLong();
    private final AtomicLong alertesPubliees = new AtomicLong();

    public MoteurDetection(List<Regle> regles, PublicateurAlertes publicateur) {
        this.regles = regles;
        this.publicateur = publicateur;
    }

    public void traiter(LogEvent evenement) {
        evenementsRecus.incrementAndGet();

        if (ipBloquees.contains(evenement.getIp())) {
            evenementsIgnores.incrementAndGet();
            return; // IP bloquée : on ignore l'événement
        }
        for (Regle regle : regles) {
            regle.analyser(evenement).ifPresent(this::declencher);
        }
    }

    private void declencher(Alerte alerte) {
        alertesPubliees.incrementAndGet();
        publicateur.publier(alerte);
    }

    // ----- Actions qui seront appelées via RMI à l'étape 3 -----

    public void bloquer(String ip) {
        ipBloquees.add(ip);
        System.out.println("[Moteur] IP bloquée : " + ip);
    }

    public boolean debloquer(String ip) {
        boolean etait = ipBloquees.remove(ip);
        if (etait) {
            System.out.println("[Moteur] IP débloquée : " + ip);
        }
        return etait;
    }

    public List<String> ipBloquees() {
        List<String> liste = new ArrayList<>(ipBloquees);
        Collections.sort(liste);
        return liste;
    }

    public Map<String, Long> statistiques() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("evenementsRecus", evenementsRecus.get());
        stats.put("evenementsIgnores", evenementsIgnores.get());
        stats.put("alertesPubliees", alertesPubliees.get());
        stats.put("ipBloquees", (long) ipBloquees.size());
        return stats;
    }
}