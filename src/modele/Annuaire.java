/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import controle.AbriRemoteInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class Annuaire extends Observable {
    
    protected Map<String, AbriRemoteInterface> abrisDistants;
    
    public Annuaire() {
        abrisDistants = new HashMap();
    }
    
    public Map<String, AbriRemoteInterface>getAbrisDistants() {
        return abrisDistants;
    }
    
    public void ajouterAbriDistant(String url, AbriRemoteInterface abri) {
        abrisDistants.put(url, abri);
        notifierObservateurs();
    }
    
    public void retirerAbriDistant(String url) {
        abrisDistants.remove(url);
        notifierObservateurs();
    }

    public void vider() {
        abrisDistants.clear();
        notifierObservateurs();
    }

    protected void notifierObservateurs() {
        super.setChanged();
        notifyObservers();
    }

    public AbriRemoteInterface chercherUrl(String urlDistant) throws AbriException {
        AbriRemoteInterface abri = abrisDistants.get(urlDistant);
        if (abri == null) { throw new AbriException("Abri " + urlDistant + " introuvable dans l'annuaire local."); }
        else { return abri; }
    }
    
}
