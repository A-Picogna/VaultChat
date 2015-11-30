/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import controle.AbriRemoteInterface;
import controle.ControleurRemoteInterface;
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
    protected Map<String, ControleurRemoteInterface> controleursDistants;
    
    public Annuaire() {
        abrisDistants = new HashMap();
        controleursDistants = new HashMap();
    }
    
    public Map<String, AbriRemoteInterface>getAbrisDistants() {
        return abrisDistants;
    }
    
    public Map<String, ControleurRemoteInterface>getControleursDistants() {
        return controleursDistants;
    }
    
    public void ajouterAbriDistant(String url, AbriRemoteInterface abri) {
        abrisDistants.put(url, abri);
        notifierObservateurs();
    }
    
    public void ajouterControleurDistant(String url, ControleurRemoteInterface controleur) {
        controleursDistants.put(url, controleur);
    }
    
    public void retirerAbriDistant(String url) {
        abrisDistants.remove(url);
        notifierObservateurs();
    }
    
    public void retirerControleurDistant(String url) {
        controleursDistants.remove(url);
    }

    public void vider() {
        abrisDistants.clear();
        controleursDistants.clear();
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
    
    public ControleurRemoteInterface chercherUrlControleur(String urlDistant) throws ControleurException {
        ControleurRemoteInterface controleur = controleursDistants.get(urlDistant);
        if (controleur == null) { throw new ControleurException("Controleur " + urlDistant + " introuvable dans l'annuaire local."); }
        else { return controleur; }
    }
    
}
