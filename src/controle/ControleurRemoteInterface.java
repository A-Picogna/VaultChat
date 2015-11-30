/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public interface ControleurRemoteInterface extends Remote {

    public void envoyerRequete(String urlDistant, int horloge) throws RemoteException;
    
    public void envoyerAcquittement(String urlDistant, int horloge) throws RemoteException;
    
    void enregistrerControleur(String urlDistant, String groupe) throws NotBoundException, MalformedURLException, RemoteException;
    
    void supprimerControleur(String urlDistant) throws RemoteException;
}
