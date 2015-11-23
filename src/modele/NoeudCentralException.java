/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

/**
 * Classe pour les exceptions emanant du noeud central
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class NoeudCentralException extends Exception {

    public NoeudCentralException() {
        super();
    }
    
    public NoeudCentralException(String message) {
        super(message);
    }
    
}
