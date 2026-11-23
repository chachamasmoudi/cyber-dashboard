package cyber.server.detection;

import cyber.common.Alerte;
import cyber.common.LogEvent;

import java.util.Optional;

/**
 * Une règle de détection. Pour en ajouter une, il suffit d'écrire une nouvelle
 * classe qui implémente cette interface puis de l'ajouter à la liste dans Serveur.
 */
public interface Regle {
    /** Analyse un événement. Retourne une alerte si la règle est déclenchée. */
    Optional<Alerte> analyser(LogEvent evenement);
}