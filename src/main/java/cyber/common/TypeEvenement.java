package cyber.common;

/** Type d'événement présent dans un journal de connexion. */
public enum TypeEvenement {
    LOGIN_OK,        // connexion réussie
    LOGIN_ECHEC,     // échec d'authentification
    CONNEXION_PORT   // tentative de connexion sur un port (pour détecter un scan)
}