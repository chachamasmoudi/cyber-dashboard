package cyber.server.jms;

import cyber.common.Alerte;
import cyber.server.detection.PublicateurAlertes;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Publie chaque alerte sur le Topic JMS « alertes ».
 * La connection factory et le topic sont récupérés par JNDI (voir jndi.properties).
 */
public class PublicateurJMS implements PublicateurAlertes, AutoCloseable {

    private final Connection connexion;
    private final Session session;
    private final MessageProducer producteur;

    public PublicateurJMS() throws NamingException, JMSException {
        java.util.Hashtable<String, String> env = new java.util.Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
        env.put("topic.alertes", "alertes");
        Context jndi = new InitialContext(env);                      // lit jndi.properties
        ConnectionFactory fabrique = (ConnectionFactory) jndi.lookup("ConnectionFactory");
        Topic topic = (Topic) jndi.lookup("alertes");

        connexion = fabrique.createConnection();
        connexion.start();
        session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producteur = session.createProducer(topic);
        System.out.println("[JMS] Connecté au broker, topic « alertes » prêt.");
    }

    /** Une Session JMS n'est pas thread-safe : on synchronise l'envoi. */
    @Override
    public synchronized void publier(Alerte alerte) {
        try {
            producteur.send(session.createTextMessage(alerte.toMessage()));
            System.out.println("[JMS] Alerte publiée : " + alerte);
        } catch (JMSException e) {
            System.err.println("[JMS] Échec de publication : " + e.getMessage());
        }
    }

    @Override
    public void close() throws JMSException {
        connexion.close();
    }
}