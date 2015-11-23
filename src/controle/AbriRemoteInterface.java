/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import java.rmi.Remote;
import java.rmi.RemoteException;
import modele.AbriException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public interface AbriRemoteInterface extends Remote {

    public void enregistrerAbri(String urlAbriDistant, String groupe, String urlControleurDistant) throws RemoteException;
    
    public void supprimerAbri(String urlAbriDistant, String urlControleurDistant) throws RemoteException;

    public void enregistrerControleur(String urlControleurDistant, String groupe) throws RemoteException;

    public void supprimerControleur(String urlControleurDistant) throws RemoteException;

    public void recevoirMessage(modele.Message transmission) throws RemoteException, AbriException;

    public String signalerGroupe() throws RemoteException;
}
