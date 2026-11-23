package cyber.server.rmi;

import cyber.common.ServiceAdmin;
import cyber.server.detection.MoteurDetection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/** Implémentation du service RMI : délègue au moteur de détection. */
public class ServiceAdminImpl extends UnicastRemoteObject implements ServiceAdmin {
    private static final long serialVersionUID = 1L;

    private final transient MoteurDetection moteur;

    public ServiceAdminImpl(MoteurDetection moteur) throws RemoteException {
        super(); // exporte l'objet pour qu'il soit appelable à distance
        this.moteur = moteur;
    }

    @Override
    public void bloquerIP(String ip) throws RemoteException {
        moteur.bloquer(ip);
    }

    @Override
    public boolean debloquerIP(String ip) throws RemoteException {
        return moteur.debloquer(ip);
    }

    @Override
    public List<String> listerIPBloquees() throws RemoteException {
        return moteur.ipBloquees();
    }

    @Override
    public Map<String, Long> getStatistiques() throws RemoteException {
        return moteur.statistiques();
    }
}