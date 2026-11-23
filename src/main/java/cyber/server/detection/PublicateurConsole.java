package cyber.server.detection;

import cyber.common.Alerte;

/** Affiche simplement les alertes dans la console (en attendant JMS). */
public class PublicateurConsole implements PublicateurAlertes {
    @Override
    public void publier(Alerte alerte) {
        System.out.println("[ALERTE] " + alerte);
    }
}