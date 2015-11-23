/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class Abri extends Observable {
    
    protected boolean connecte;
    protected List<Message> tampon;
    protected String groupe;
    
    public Abri() {
        connecte = false;
        tampon = new LinkedList();
        groupe = "";
    }

    public boolean estConnecte() {
        return connecte;
    }
    
    public void connecter() {
        connecte = true;
    }
    
    public String donnerGroupe() {
        return groupe;
    }
    
    public void definirGroupe(String groupe) {
        this.groupe = groupe;
    }
    
    public void deconnecter() {
        connecte = false;
    }
    
    public void ajouterMessage(Message message) {
        tampon.add(message);
        notifierObservateurs();
    }
    
    public void viderTampon() {
        tampon.clear();
    }
    
    public List<Message> lireTampon() {
        return tampon;
    }
    
    protected void notifierObservateurs() {
        super.setChanged();
        notifyObservers();
    }
    
}
