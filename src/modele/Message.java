/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Classe representant une information envoyee par un abri vers un autre
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class Message implements Serializable {

    protected String urlEmetteur;
    protected ArrayList<String> urlDestinataire;
    protected String contenu;
    protected String timestamp;

    public Message(String _urlEmetteur, ArrayList<String> _urlDestinataire, String _contenu) {
        this.urlEmetteur = _urlEmetteur;
        this.urlDestinataire = _urlDestinataire;
        this.contenu = _contenu;
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
    }

    public String getUrlEmetteur() {
        return urlEmetteur;
    }

    public ArrayList<String> getUrlDestinataire() {
        return urlDestinataire;
    }

    public String getContenu() {
        return contenu;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public String toString() {
        return "A " + timestamp + ", de " + urlEmetteur + " pour " + urlDestinataire + ": " + contenu;
    }

    public String toHTML() {
        return "<u>A " + timestamp + "<br>De <b>" + urlEmetteur + "</b><br>Pour " + urlDestinataire + "</u>:<br>" + contenu + "<hr>";
    }

}
