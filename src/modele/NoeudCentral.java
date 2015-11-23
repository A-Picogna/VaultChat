/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Classe qui simule le noeud central du réseau via l'abri entrants et les abris sortants
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class NoeudCentral extends Observable {
    
    protected String depuisUrl;
    protected ArrayList<String> versUrl;
    protected boolean transmissionEnCours;
    
    public void reconfigurerAiguillage(String _depuisUrl, ArrayList<String> _versUrl) throws NoeudCentralException {
        if (transmissionEnCours) {
            throw new NoeudCentralException("Impossible de modifier la configuration du noeud central lorsqu'une transmission est en cours");
        }
        this.depuisUrl = _depuisUrl;
        this.versUrl = _versUrl;
        notifierObservateurs();
    }
    
    public String getDepuisUrl() throws NoeudCentralException {
        if (depuisUrl == null) {
            throw new NoeudCentralException("Le noeud central n'est configuré pour aucun emetteur.");
        }
        return depuisUrl;
    }
    
    public ArrayList<String> getVersUrl() throws NoeudCentralException {
        if (versUrl == null) {
            throw new NoeudCentralException("Le noeud central n'est configuré pour aucun destinataire.");
        }
        return versUrl;
    }
    
    public boolean tranmissionEnCours() {
        return transmissionEnCours;
    }
    
    public void demarrerTransmission() {
        transmissionEnCours = true;
        notifierObservateurs();
    }
    
    public void stopperTransmission() {
        transmissionEnCours = false;
        notifierObservateurs();
    }
    
    protected void notifierObservateurs() {
        super.setChanged();
        notifyObservers();
    }
    
}
