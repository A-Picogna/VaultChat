package controle;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import modele.ControleurException;

/**
 * Interface definissant les fonctionnalites attendues d'un controleur pour le projet VaultChat
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public interface ControleurInterface {
	
	/**
	 * Receptionne du demande d'entree en section critique de la part du processus metier
	 */
	void demanderSectionCritique();
	
	/**
	 * Signale l'autorisation d'entrer en section critique aupres du processus metier
	 */
	void signalerAutorisation();
	
	/**
	 * Receptionne la notification du processus metier a sa sortie de la section critique
	 */
	void quitterSectionCritique();
	
	/**
	 * Enregistre l'URL d'un controleur distant
	 * @param urlDistant l'URL a memoriser
         * @param groupe le groupe auquel appartient l'abri
         * @throws java.rmi.NotBoundException
         * @throws java.net.MalformedURLException
         * @throws java.rmi.RemoteException
	 */
	void enregistrerControleur(String urlDistant, String groupe) throws NotBoundException, MalformedURLException, RemoteException;
	
	/**
	 * Oublie l'URL d'un controleur distant
	 * @param urlDistant l'URL a oublier
	 */
	void supprimerControleur(String urlDistant) throws RemoteException;
	
        /**
	 * Bind l'URL du controleur
         * @throws modele.ControleurException
         * @throws java.rmi.RemoteException
         * @throws java.net.MalformedURLException
         * @throws java.rmi.NotBoundException
	 */
        void connecterControleur() throws ControleurException, RemoteException, MalformedURLException, NotBoundException;
        
        /**
	 * Unbind l'URL du controleur
         * @throws modele.ControleurException
         * @throws java.rmi.RemoteException
         * @throws java.net.MalformedURLException
         * @throws java.rmi.NotBoundException
	 */
        void deconnecterControleur() throws ControleurException, RemoteException, MalformedURLException, NotBoundException;
}
