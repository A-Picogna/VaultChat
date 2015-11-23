/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import modele.Annuaire;
import modele.AbriException;
import modele.Message;
import modele.NoeudCentral;
import modele.NoeudCentralException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class NoeudCentralBackend extends UnicastRemoteObject implements NoeudCentralRemoteInterface {

    protected String url;
    protected NoeudCentral noeudCentral;
    protected Annuaire abris;

    public NoeudCentralBackend(String _url) throws RemoteException, MalformedURLException {
        this.url = _url;
        noeudCentral = new NoeudCentral();
        abris = new Annuaire();
        Naming.rebind(url, (NoeudCentralRemoteInterface) this);
    }

    @Override
    public void finalize() throws RemoteException, NotBoundException, MalformedURLException, Throwable {
        try {
            Naming.unbind(url);
        } finally {
            super.finalize();
        }
    }

    public NoeudCentral getNoeudCentral() {
        return noeudCentral;
    }

    public Annuaire getAnnuaire() {
        return abris;
    }

    @Override
    public synchronized void modifierAiguillage(String depuisUrl, ArrayList<String> versUrl) throws RemoteException, NoeudCentralException {
        
        System.out.print(url + ": \tReconfiguration du réseau de " + depuisUrl + " vers ");
        Iterator<String> itr = versUrl.iterator();
        while (itr.hasNext()) {
            System.out.print(itr.next());
        }
        System.out.print("\n");
        
        noeudCentral.reconfigurerAiguillage(depuisUrl, versUrl);
    }

    @Override
    public synchronized void transmettre(Message message) throws RemoteException, AbriException, NoeudCentralException {
        try {
            noeudCentral.demarrerTransmission();
            
            System.out.println(url + ": \tTransmission du message \"" + message.toString() + "\"");
            
            ArrayList<String> abrisCible = noeudCentral.getVersUrl();
            Iterator<String> itr = abrisCible.iterator();
            
            while (itr.hasNext()) {
                AbriRemoteInterface c = abris.chercherUrl(itr.next());
                c.recevoirMessage(message);
            }
        } catch (RemoteException ex) {
            throw ex;
        } catch (AbriException ex) {
            throw ex;
        } finally {
            noeudCentral.stopperTransmission();
        }
    }

    @Override
    public synchronized void enregisterAbri(String urlAbriDistant) throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println(url + ": \tEnregistrement de l'abri dans l'annuaire " + urlAbriDistant);
        AbriRemoteInterface abriDistant = (AbriRemoteInterface) Naming.lookup(urlAbriDistant);
        abris.ajouterAbriDistant(urlAbriDistant, abriDistant);
    }

    @Override
    public synchronized void supprimerAbri(String urlAbriDistant) throws RemoteException {
        System.out.println(url + ": \tSuppression de l'abri de l'annuaire " + urlAbriDistant);
        abris.retirerAbriDistant(urlAbriDistant);
    }

}
