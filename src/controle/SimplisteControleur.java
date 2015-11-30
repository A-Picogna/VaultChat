/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modele.AbriException;
import modele.Adresses;
import modele.Annuaire;
import modele.ControleurException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class SimplisteControleur extends UnicastRemoteObject implements ControleurInterface, ControleurRemoteInterface {
    
    protected String url;
    protected AbriLocalInterface abri;
    protected Annuaire controleursDistants;
    protected ArrayList<String> controleurs;    // Liste de l'ensemble des contrôleurs en cours d'utilisation
    
    protected int horloge;  // Valeur de l'horloge logique
    protected Etat etat;  // Etat de l'abri
    protected int hreq;     // Heure de la requête courante
    protected boolean[] jetons;  // L'abri a ou non les jetons des autres sites
    protected boolean[] retardes;   // L'abri a retardé ou non l'acquittement des autres abris
    
    enum Etat {REPOS, ATTENTE, SC} ; 
    
    public SimplisteControleur(String url, AbriLocalInterface abri) throws RemoteException, MalformedURLException {
        this.url = url;
        this.abri = abri;
        this.controleursDistants = new Annuaire();
        this.controleurs = new ArrayList<String>();
        this.horloge = 0;
        this.hreq = 0;
        this.etat = Etat.REPOS;
    }
    
    /**
     * @throws AbriException
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     * @throws Throwable
     */
    @Override
    public void finalize() throws AbriException, RemoteException, NotBoundException, MalformedURLException, Throwable {
        try {
            deconnecterControleur();
            Naming.unbind(url);
        } finally {
            super.finalize();
        }
    }
    
    @Override
    public void envoyerRequete(int indice, int horloge) {
        
    }
    
    @Override
    public void envoyerAcquittement(int indice, int horloge) {
        
    }
    
    @Override
    public void demanderSectionCritique() {
        System.out.println(this.url + ": \tDemande de section critique enregistrée");
        this.hreq = horloge;
        for (int i=0;i<this.controleurs.size();i++){
            this.retardes[i] = false;
            if (this.jetons[i] == false)
                ;//envoyer
        }
        signalerAutorisation();
    }

    @Override
    public synchronized void signalerAutorisation() {
        System.out.println(this.url + ": \tSignalement de l'autorisation");
        abri.recevoirAutorisation();
    }

    @Override
    public void quitterSectionCritique() {
        System.out.println(this.url + ": \tFin de section critique");
    }

    @Override
    public void enregistrerControleur(String urlDistant, String groupe) {
        System.out.println(this.url + ": \tEnregistrement du controleur " + urlDistant);
        controleurs.add(urlDistant);
        Pattern pattern = Pattern.compile("^.*abri([0-9]*).*$");
        Matcher matcher = pattern.matcher(urlDistant);
        if (matcher.find())
            matcher.group(1);
    }

    @Override
    public void supprimerControleur(String urlDistant) {
        System.out.println(this.url + ": \tSuppression du controleur " + urlDistant);
        controleurs.remove(urlDistant);
    }
    
    @Override
    public void connecterControleur() throws ControleurException, RemoteException, MalformedURLException, NotBoundException {
        Naming.rebind(url, (ControleurRemoteInterface) this);
        
        // Enregistrement de tous les autres controleurs
        // et notification a tous les autres controleurs
        for (String name : Naming.list(Adresses.archetypeAdresseControleur())) {
            name = "rmi:" + name;
            if (!name.equals(url)) {
                Remote o = Naming.lookup(name);
                if (o instanceof ControleurRemoteInterface) {
                    // Enregistrement du controleur courant
                    System.out.println(url + ": \tEnregistrement aupres de " + name);
                    ((ControleurRemoteInterface) o).enregistrerControleur(url, "0");
                    // Enregistrement d'un controleur distant
                    SimplisteControleur.this.enregistrerControleur(name, "0");
                }
            }
        }
    }
    
    @Override
    public void deconnecterControleur() throws ControleurException, RemoteException, MalformedURLException, NotBoundException {
        // Autres abris
        /*for (ControleurRemoteInterface distant : controleursDistants.getAbrisDistants().values()) {
            try {
                distant.supprimerAbri(url, controleurUrl);
            } catch (RemoteException ex) {
                Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        controleursDistants.vider();*/
        
        // Annuaire RMI
        Naming.unbind(url);
    }
    
}
