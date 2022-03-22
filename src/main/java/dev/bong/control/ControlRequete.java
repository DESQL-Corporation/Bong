package dev.bong.control;

import dev.bong.boundary.Clavier;
import dev.bong.entity.*;
import fr.dgac.ivy.IvyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ControlRequete implements PropertyChangeListener{
    private CommunicationIvy communicationIvy = CommunicationIvy.getInstance();
    private TypeRequete typeRequete;

    private List<Requete> listeRequete = new ArrayList<>();
    private List<String> listeResultat = new ArrayList<>();

    private int nbRequeteFinit = 0;

    public ControlRequete(TypeRequete typeRequete){
        this.typeRequete = typeRequete;
        communicationIvy.addPropertyChangeListener(ListenerPropriete.RESULTAT.toString(),this);
    }

    public boolean lancerCommunicationBus(){
        try {
            communicationIvy.lancerCommunication();
        } catch (IvyException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void fermerCommunicationBus(){
        communicationIvy.fermerCommunication();
    }

    public void creerRequeteRecherche(String mot){
        Requete requete = new Requete(mot);
        requete.initRecherche();
        listeRequete.add(requete);
    }

    public void creerRequeteIndexation(){
        Requete requete = new Requete();
        requete.initIndexation();
        listeRequete.add(requete);
    }

    public void initRequete(){
        listeRequete.clear();
        listeResultat.clear();
        nbRequeteFinit=0;
    }

    public void creerEtenvoyerListeRequete(List<String> motCle){
        initRequete();

        for (String s : motCle) {
            creerRequeteRecherche(s);
        }

       test();

        for (Requete requete : listeRequete) {
            envoyerRequete(requete);
        }
    }

    public void envoyerRequete(Requete requete){
        requete.start(typeRequete);
    }

    public boolean touteRequeteFinit(){
        return nbRequeteFinit == listeRequete.size();
    }

    public int nombreRequeteFinit(){
        return nbRequeteFinit;
    }

    public List<Requete> getListeRequete() {
        return listeRequete;
    }

    public List<String> getListeResultat() {
        return listeResultat;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String message = (String) evt.getNewValue();
        listeResultat.add(message);
        this.nbRequeteFinit++;
        System.out.println(nbRequeteFinit + " / " + listeRequete.size());
    }

    public void test(){
        try {
            System.out.println("test : " + communicationIvy.envoieMessage("test")); //Ne marche pas si k'affiche pas le nb de personne a qui j'envoie
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }
}
