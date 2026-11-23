package cyber.server.detection;

import cyber.common.Alerte;
import cyber.common.LogEvent;
import cyber.common.Severite;
import cyber.common.TypeEvenement;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

/** Détecte une connexion réussie entre minuit et 6h du matin. */
public class HorsHoraires implements Regle {

    @Override
    public Optional<Alerte> analyser(LogEvent e) {
        if (e.getType() != TypeEvenement.LOGIN_OK) {
            return Optional.empty();
        }
        int heure = Instant.ofEpochMilli(e.getTimestamp()).atZone(ZoneId.systemDefault()).getHour();
        if (heure < 6) {
            return Optional.of(new Alerte(Severite.MOYENNE, e.getIp(), "HorsHoraires",
                    "Connexion réussie à " + heure + "h (hors horaires de travail)"));
        }
        return Optional.empty();
    }
}