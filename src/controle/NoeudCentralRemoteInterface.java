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
import java.util.ArrayList;
import modele.AbriException;
import modele.Message;
import modele.NoeudCentralException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public interface NoeudCentralRemoteInterface extends Remote {
    
    public void modifierAiguillage(String depuisUrl, ArrayList<String> versListeUrl) throws RemoteException, NoeudCentralException;
    
    public void transmettre(Message message) throws RemoteException, AbriException, NoeudCentralException;
    
    public void enregisterAbri(String url) throws RemoteException, NotBoundException, MalformedURLException;
    
    public void supprimerAbri(String url) throws RemoteException;
    
}
