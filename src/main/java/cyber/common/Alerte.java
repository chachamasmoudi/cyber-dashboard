package cyber.common;

import java.io.Serializable;

/**
 * Une alerte de sécurité (elle sera publiée sur le Topic JMS à l'étape 4).
 * Format du message JMS : SEVERITE|ip|regle|timestamp|message
 */
public class Alerte implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Severite severite;
    private final String ip;
    private final String regle;
    private final String message;
    private final long timestamp;

    public Alerte(Severite severite, String ip, String regle, String message) {
        this(severite, ip, regle, message, System.currentTimeMillis());
    }

    public Alerte(Severite severite, String ip, String regle, String message, long timestamp) {
        this.severite = severite;
        this.ip = ip;
        this.regle = regle;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Severite getSeverite() { return severite; }
    public String getIp() { return ip; }
    public String getRegle() { return regle; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    /** Texte qui sera envoyé dans le TextMessage JMS. */
    public String toMessage() {
        return severite + "|" + ip + "|" + regle + "|" + timestamp + "|" + message;
    }

    public static Alerte fromMessage(String texte) {
        String[] p = texte.split("\\|", 5);
        if (p.length != 5) {
            throw new IllegalArgumentException("Alerte invalide : " + texte);
        }
        return new Alerte(Severite.valueOf(p[0]), p[1], p[2], p[4], Long.parseLong(p[3]));
    }

    @Override
    public String toString() {
        return "[" + severite + "] " + regle + " | IP " + ip + " | " + message;
    }
}