package cyber.admin;

import cyber.common.ServiceAdmin;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

/**
 * Console d'administration (version texte, avant l'interface JavaFX).
 * Envoie des commandes au serveur par RMI : bloquer, debloquer, liste, stats.
 *
 * Usage : AdminConsole [hoteServeur]
 */
public class AdminConsole {

    public static void main(String[] args) throws Exception {
        String hote = args.length > 0 ? args[0] : "localhost";

        // Récupérer le stub du service distant dans le registre RMI du serveur
        Registry registre = LocateRegistry.getRegistry(hote, 1099);
        ServiceAdmin service = (ServiceAdmin) registre.lookup("ServiceAdmin");
        System.out.println("[Admin] Connecté au service RMI sur " + hote);

        aide();
        try (Scanner clavier = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                if (!clavier.hasNextLine()) {
                    break;
                }
                String[] mots = clavier.nextLine().trim().split("\\s+");
                String commande = mots[0].toLowerCase();
                try {
                    switch (commande) {
                        case "bloquer":
                            service.bloquerIP(mots[1]);
                            System.out.println("IP bloquée : " + mots[1]);
                            break;
                        case "debloquer":
                            System.out.println(service.debloquerIP(mots[1])
                                    ? "IP débloquée : " + mots[1] : "Cette IP n'était pas bloquée.");
                            break;
                        case "liste":
                            System.out.println("IP bloquées : " + service.listerIPBloquees());
                            break;
                        case "stats":
                            for (Map.Entry<String, Long> e : service.getStatistiques().entrySet()) {
                                System.out.println("  " + e.getKey() + " = " + e.getValue());
                            }
                            break;
                        case "quitter":
                            return;
                        case "":
                            break;
                        default:
                            aide();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Il manque l'adresse IP. Exemple : bloquer 10.0.0.5");
                }
            }
        }
    }

    private static void aide() {
        System.out.println("Commandes : bloquer <ip> | debloquer <ip> | liste | stats | quitter");
    }
}