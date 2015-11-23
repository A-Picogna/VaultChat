/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import modele.AbriException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import modele.Annuaire;
import modele.NoeudCentralException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public interface AbriLocalInterface {
    
    public String getUrl();
        
    public boolean estConnecte();
    
    public Annuaire getAnnuaire();
    
    public void connecterAbri() throws AbriException, RemoteException, MalformedURLException, NotBoundException;
    
    public void deconnecterAbri() throws AbriException, RemoteException, MalformedURLException, NotBoundException;
    
    public void emettreMessage(String message) throws InterruptedException, RemoteException, AbriException, NoeudCentralException;
    
    public void enregistrerAbri(String url, String groupe, AbriRemoteInterface distant);
    
    public void supprimerAbri(String url);
    
    public void recevoirAutorisation();

    public void changerGroupe(String groupe);
    
}
