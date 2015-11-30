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
public interface ControleurRemoteInterface extends Remote {

    public void envoyerRequete(int indice, int horloge) throws RemoteException;
    
    public void envoyerAcquittement(int indice, int horloge) throws RemoteException;
    
    void enregistrerControleur(String urlDistant, String groupe);
    
    void supprimerControleur(String urlDistant);
}
