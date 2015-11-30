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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    protected Map<String, Boolean> jetons;  // L'abri a ou non les jetons des autres sites
    protected Map<String, Boolean> retardes;   // L'abri a retardé ou non l'acquittement des autres abris
    
    enum Etat {REPOS, ATTENTE, SC} ; 
    
    public SimplisteControleur(String url, AbriLocalInterface abri) throws RemoteException, MalformedURLException {
        this.url = url;
        this.abri = abri;
        this.controleursDistants = new Annuaire();
        this.controleurs = new ArrayList<String>();
        this.horloge = 0;
        this.hreq = 0;
        this.etat = Etat.REPOS;
        
        this.jetons = new HashMap();
        this.retardes = new HashMap();
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
    public void envoyerRequete(String urlDistant, int horl) {
        horloge = horloge+1 >= horl+1 ? horloge+1:horl+1;
        
        if (this.etat == Etat.REPOS) {
            try {
                ControleurRemoteInterface o = this.controleursDistants.chercherUrlControleur(urlDistant);
                o.envoyerAcquittement(url, hreq);
                this.jetons.put(urlDistant, Boolean.FALSE);
            }
            catch(ControleurException ex) {
                Logger.getLogger(SimplisteControleur.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(RemoteException ex) {
                Logger.getLogger(SimplisteControleur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (this.etat == Etat.SC || horl >= hreq) {
            this.retardes.put(urlDistant, Boolean.TRUE);
        }
        else {
            try {
                ControleurRemoteInterface o = this.controleursDistants.chercherUrlControleur(urlDistant);
                o.envoyerAcquittement(url, hreq);
                this.jetons.put(urlDistant, Boolean.FALSE);
                o.envoyerRequete(url, hreq);
            }
            catch(ControleurException ex) {
                Logger.getLogger(SimplisteControleur.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(RemoteException ex) {
                Logger.getLogger(SimplisteControleur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void envoyerAcquittement(String urlDistant, int horl) {
        horloge = horloge+1 >= horl+1 ? horloge+1:horl+1;
        this.jetons.put(urlDistant, Boolean.TRUE);
        if (!this.jetons.containsValue(false)) {
            this.etat = Etat.SC;
            signalerAutorisation();
        }
    }
    
    @Override
    public void demanderSectionCritique() {
        System.out.println(this.url + ": \tDemande de section critique enregistrée");
        this.hreq = horloge;
        
        Map<String, ControleurRemoteInterface> contrd = this.controleursDistants.getControleursDistants();
        for (Map.Entry<String, ControleurRemoteInterface> entry : contrd.entrySet())
        {
            //this.retardes.put(entry.getKey(), Boolean.FALSE);
            if (!this.jetons.get(entry.getKey())) {
                try {
                    entry.getValue().envoyerRequete(url, hreq);
                }
                catch(RemoteException ex) {
                    Logger.getLogger(SimplisteControleur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        this.etat = Etat.ATTENTE;
        if (!this.jetons.containsValue(false)) {
            this.etat = Etat.SC;
            signalerAutorisation();
        }
    }

    @Override
    public synchronized void signalerAutorisation() {
        System.out.println(this.url + ": \tSignalement de l'autorisation");
        abri.recevoirAutorisation();
    }

    @Override
    public void quitterSectionCritique() {
        System.out.println(this.url + ": \tFin de section critique");
        
        Map<String, ControleurRemoteInterface> contrd = this.controleursDistants.getControleursDistants();
        for (Map.Entry<String, ControleurRemoteInterface> entry : contrd.entrySet())
        {
            if (this.retardes.get(entry.getKey())) {
                try {
                    entry.getValue().envoyerAcquittement(url, hreq);
                    this.jetons.put(entry.getKey(), Boolean.FALSE);
                    this.retardes.put(entry.getKey(), Boolean.FALSE);
                }
                catch(RemoteException ex) {
                    Logger.getLogger(SimplisteControleur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        this.etat = Etat.REPOS;
    }
    
    @Override
    public void enregistrerControleur(String urlDistant, String groupe) throws NotBoundException, MalformedURLException, RemoteException {
        this.jetons.put(urlDistant, Boolean.FALSE);
        this.retardes.put(urlDistant, Boolean.FALSE);
        
        ControleurRemoteInterface o = (ControleurRemoteInterface) Naming.lookup(urlDistant);
        controleursDistants.ajouterControleurDistant(urlDistant, o);
        this.controleurs.add(urlDistant);
        System.out.println(this.url + ": \tEnregistrement du controleur " + urlDistant);
        
        // Indice de l'abri inutile
        /*
        Pattern pattern = Pattern.compile("^.*abri([0-9]*).*$");
        Matcher matcher = pattern.matcher(urlDistant);
        if (matcher.find())
            matcher.group(1);
        */
    }

    @Override
    public void supprimerControleur(String urlDistant) throws RemoteException {
        
        System.out.println(url + ": \tOubli du controleur " + urlDistant);
        
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
        // envoyer tous les jetons
        // pas obligé
    }
    
}
