package cyber.common;

import java.io.Serializable;

/**
 * Un événement de journal envoyé par un agent.
 * Format sur le réseau (UDP) : ip;TYPE;port;timestampMs
 * Exemple : 10.0.0.5;LOGIN_ECHEC;22;1733000000000
 */
public class LogEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String ip;
    private final TypeEvenement type;
    private final int port;
    private final long timestamp;

    public LogEvent(String ip, TypeEvenement type, int port, long timestamp) {
        this.ip = ip;
        this.type = type;
        this.port = port;
        this.timestamp = timestamp;
    }

    public String getIp() { return ip; }
    public TypeEvenement getType() { return type; }
    public int getPort() { return port; }
    public long getTimestamp() { return timestamp; }

    /** Transforme l'événement en une ligne de texte pour l'envoi UDP. */
    public String toLigne() {
        return ip + ";" + type + ";" + port + ";" + timestamp;
    }

    /** Reconstruit un événement à partir d'une ligne reçue. */
    public static LogEvent parse(String ligne) {
        String[] p = ligne.trim().split(";");
        if (p.length != 4) {
            throw new IllegalArgumentException("Format invalide : " + ligne);
        }
        try {
            return new LogEvent(p[0], TypeEvenement.valueOf(p[1]),
                    Integer.parseInt(p[2]), Long.parseLong(p[3]));
        } catch (IllegalArgumentException e) { // couvre aussi NumberFormatException
            throw new IllegalArgumentException("Format invalide : " + ligne, e);
        }
    }

    @Override
    public String toString() {
        return "LogEvent{" + toLigne() + "}";
    }
}