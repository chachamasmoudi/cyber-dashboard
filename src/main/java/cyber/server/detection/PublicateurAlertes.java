package cyber.server.detection;

import cyber.common.Alerte;

/** Abstraction : où part l'alerte ? (console pour tester, JMS en production). */
public interface PublicateurAlertes {
    void publier(Alerte alerte);
}