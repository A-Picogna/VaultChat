/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import modele.AbriException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import modele.Annuaire;
import modele.Abri;
import modele.Message;
import modele.Adresses;
import modele.ControleurException;
import modele.NoeudCentralException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class AbriBackend extends UnicastRemoteObject implements AbriLocalInterface, AbriRemoteInterface {
    
    protected String url;
    protected String controleurUrl;
    protected String noeudCentralUrl;

    protected Abri abri;
    final protected ControleurInterface controleur;
    protected NoeudCentralRemoteInterface noeudCentral;
    
    protected Annuaire abrisDistants;    // Map faisant le lien entre les url et les interfaces RMI des abris distants // Ne foit pas connaitre l'ensemble de ses abris, juste ses voisins
    protected ArrayList<String> copains; // Les urls des autres membres du groupe de l'abri courant // Pas dans l'annuaire -> imposer la gestion d'une liste locale aux abris pour les groupes
    
    protected Semaphore semaphore;
    
    public AbriBackend(String _url, Abri _abri) throws RemoteException, MalformedURLException {
        
        this.url = _url;
        this.controleurUrl = _url+"/controleur";
        this.noeudCentralUrl = "";
        
        this.abri = _abri;
        this.controleur = new SimplisteControleur(controleurUrl, this);
        try {
            this.controleur.connecterControleur();
        }
        catch(ControleurException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(NotBoundException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(MalformedURLException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(RemoteException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.noeudCentral = null;
        
        this.abrisDistants = new Annuaire();
        this.copains = new ArrayList<String>();
        
        this.semaphore = new Semaphore(0, true);
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
            deconnecterAbri();
            Naming.unbind(url);
        } finally {
            super.finalize();
        }
    }

    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public boolean estConnecte() {
        return abri.estConnecte();
    }

    @Override
    public Annuaire getAnnuaire() {
        return abrisDistants;
    }

    @Override
    public void connecterAbri() throws NoeudCentralException, AbriException, ControleurException, RemoteException, MalformedURLException, NotBoundException {
        // Enregistrer dans l'annuaire RMI
        Naming.rebind(url, (AbriRemoteInterface) this);
        
        // Enregistrement de tous les autres abris
        // et notification a tous les autres abris
        for (String name : Naming.list(Adresses.archetypeAdresseAbri())) {
            name = "rmi:" + name;
            if (!name.equals(url)) {
                Remote o = Naming.lookup(name);
                if (o instanceof AbriRemoteInterface) {
                    // Enregistrement de l'abri courant
                    System.out.println(url + ": \tEnregistrement aupres de " + name);
                    ((AbriRemoteInterface) o).enregistrerAbri(url, abri.donnerGroupe(), controleurUrl);
                    // Enregistrement d'un abri distant
                    AbriBackend.this.enregistrerAbri(name, ((AbriRemoteInterface) o).signalerGroupe(), (AbriRemoteInterface) o);
                }
                else if (o instanceof NoeudCentralRemoteInterface) {
                    if (noeudCentral == null) {
                        this.noeudCentralUrl = name;
                        noeudCentral = (NoeudCentralRemoteInterface) o;
                        noeudCentral.enregisterAbri(url);
                    }
                    else {
                        throw new AbriException("Plusieurs noeuds centraux semblent exister.");
                    }
                }
            }
        }
        abri.connecter();
    }

    /**
     *
     * @throws modele.NoeudCentralException
     * @throws java.lang.InterruptedException
     * @throws AbriException
     * @throws modele.ControleurException
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public void deconnecterAbri() throws NoeudCentralException, InterruptedException, AbriException, ControleurException, RemoteException, MalformedURLException, NotBoundException {
        controleur.demanderSectionCritique();
        semaphore.acquire();
        System.out.println(url + ": \tEntree en section critique");
        noeudCentral.modifierAiguillage(url, copains);
        
        // noeudCentral
        noeudCentral.supprimerAbri(url);
        noeudCentralUrl = "";
        noeudCentral = null;
        this.controleur.deconnecterControleur();
        
        // Autres abris
        /*for (AbriRemoteInterface distant : abrisDistants.getAbrisDistants().values()) {
            try {
                distant.supprimerAbri(url, controleurUrl);
            } catch (RemoteException ex) {
                Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        abrisDistants.vider();
        copains.clear();
        
        // Abri
        abri.deconnecter();
        
        // Annuaire RMI
        Naming.unbind(url);
        
        controleur.quitterSectionCritique();
        System.out.println(url + ": \tSortie de la section critique");
    }

    /**
     *
     * @param message
     * @throws RemoteException
     * @throws AbriException
     */
    @Override
    public synchronized void recevoirMessage(modele.Message message) throws RemoteException, AbriException {
        if (!message.getUrlDestinataire().contains(url)) {
            throw new AbriException("Message recu par le mauvais destinataire (" + message.getUrlDestinataire().toString() +  " != " + url + ")");
        }
        System.out.println(url + ": \tMessage recu de " + message.getUrlEmetteur() + " \"" + message.getContenu() + "\"");
        abri.ajouterMessage(message);
    }

    @Override
    public void emettreMessage(String message) throws InterruptedException, RemoteException, AbriException, NoeudCentralException {
            controleur.demanderSectionCritique();
            semaphore.acquire();
            System.out.println(url + ": \tEntree en section critique");
            System.out.println(url + " est dans le groupe " + abri.donnerGroupe());
            System.out.println(url + ": \tEmission vers " + copains.toString() + ": " + message);
            noeudCentral.modifierAiguillage(url, copains);
            noeudCentral.transmettre(new Message(url, copains, message));
            controleur.quitterSectionCritique();
            System.out.println(url + ": \tSortie de la section critique");
    }

    @Override
    public void enregistrerAbri(String urlDistant, String groupe, AbriRemoteInterface distant) {
        
        if (groupe.equals(abri.donnerGroupe()))
        {
            abrisDistants.ajouterAbriDistant(urlDistant, distant);
            this.copains.add(urlDistant);
            System.out.println(url + ": \tEnregistrement de l'abri " + urlDistant);
        }
 
    }

    @Override
    public synchronized void enregistrerAbri(String urlAbriDistant, String groupe, String urlControleurDistant) {
        try {
            AbriRemoteInterface o = (AbriRemoteInterface) Naming.lookup(urlAbriDistant);
            AbriBackend.this.enregistrerAbri(urlAbriDistant, groupe, o);
            enregistrerControleur(urlControleurDistant, groupe);
            o.enregistrerControleur(controleurUrl, groupe);
        } catch (NotBoundException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void supprimerAbri(String urlDistant) {
        
        if (copains.contains(urlDistant))
        {
            System.out.println(url + ": \tOubli de l'abri " + urlDistant);
            abrisDistants.retirerAbriDistant(urlDistant);
            copains.remove(urlDistant);
        }
    }
    
    @Override
    public synchronized void supprimerAbri(String urlAbriDistant, String urlControleurDistant) {
        try {
            AbriRemoteInterface o = (AbriRemoteInterface) Naming.lookup(urlAbriDistant);
            AbriBackend.this.supprimerAbri(urlAbriDistant);
            supprimerControleur(urlControleurDistant);
            o.supprimerControleur(controleurUrl);
        } catch (NotBoundException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void enregistrerControleur(String urlDistante, String groupe) throws NotBoundException, MalformedURLException, RemoteException {
        controleur.enregistrerControleur(urlDistante, groupe);
    }

    @Override
    public synchronized void supprimerControleur(String urlDistante) throws RemoteException {
        controleur.supprimerControleur(urlDistante);
    }

    @Override
    public void recevoirAutorisation() {
        semaphore.release();
    }
    
    @Override
    public void changerGroupe(String groupe)
    {
        abri.definirGroupe(groupe);
    }
    
    @Override
    public String signalerGroupe() throws RemoteException
    {
        return abri.donnerGroupe();
    }
    
}
