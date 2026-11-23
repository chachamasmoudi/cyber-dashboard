package cyber.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interface RMI : actions que l'administrateur peut appeler à distance sur le serveur.
 * Toute méthode distante doit déclarer RemoteException.
 */
public interface ServiceAdmin extends Remote {

    /** Bloque une adresse IP : ses prochains événements seront ignorés. */
    void bloquerIP(String ip) throws RemoteException;

    /** Débloque une adresse IP. Retourne false si elle n'était pas bloquée. */
    boolean debloquerIP(String ip) throws RemoteException;

    List<String> listerIPBloquees() throws RemoteException;

    /** Compteurs : evenementsRecus, evenementsIgnores, alertesPubliees, ipBloquees. */
    Map<String, Long> getStatistiques() throws RemoteException;
}