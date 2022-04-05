package dev.bong.control;


import dev.bong.entity.Historique;
import dev.bong.entity.TypeMoteur;
import dev.bong.entity.TypeRequete;
import dev.bong.view.RechercheController;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlRechercheFichier extends ControlRecherche implements Runnable {

    //liste de mot clé
    private List<String> nomFicRecherche = new ArrayList<>();
    private List<String> nomFicBan = new ArrayList<>();

    //Mode pour la recherche
    private boolean modeOuvert;

    //Permet d'initialiser la com + les listes de mots clés
    public ControlRechercheFichier(ProgressIndicator progressIndicator, ProgressBar progressBar,
                                   List<String> nomFicRecherche, List<String> nomFicBan, String extension,
                                   boolean modeOuvert, TypeMoteur typeMoteur, RechercheController rechercheController) throws Exception {
        super(new ControlRequete(TypeRequete.RECHERCHE_FICHIER),progressIndicator,progressBar, typeMoteur,rechercheController);

        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        //Laisse le temps à la communication de s'établir entre tous les agents
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testCommunication.testerCommunication();

        this.ajoutExtension(extension,nomFicRecherche,nomFicBan);

        this.modeOuvert = modeOuvert;
    }

    //se lance avec .start() (Tread)
    public void run(){

        // creation des set servant a recuperer les resultats des recherches
        Set<String> resMotCle = new HashSet<>();
        Set<String> resMotBan = new HashSet<>();
        Set<String> resTotal = new HashSet<>();

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        System.out.println("mode Ouvert : " + modeOuvert);

        if (modeOuvert){
            System.out.println("On indexe");
            ControlIndexation.IndexationDuModeOuvert();
        }

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        // appel des fonctions de recherches
        if (!nomFicRecherche.toString().equals("[]")) resMotCle=recherche(nomFicRecherche,TypeRequete.RECHERCHE_FICHIER,true);
        else {
            progressBar.setProgress(progressBar.getProgress() + 0.3);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
        }
        if (!nomFicBan.toString().equals("[]")) resMotBan=recherche(nomFicBan,TypeRequete.RECHERCHE_FICHIER,false);
        else {
            progressBar.setProgress(progressBar.getProgress() + 0.3);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
        }

        // ajout des recherches a polarité positives et suppression des polarité négatives
        if (nomFicRecherche.toString().equals("[]")){

            //inverser les résultats

        } else {
            resTotal.addAll(resMotCle);
            resTotal.removeAll(resMotBan);
        }

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        // affichage du resultat total
        System.out.println("resultat final : \n"+resTotal);

        //envoie resultats
        controlEnvoieResultat.receptionResultat(resTotal);

        //appel de l'historique
        Historique.ecrire(TypeRequete.RECHERCHE_FICHIER,"motCle : " + nomFicRecherche + ",motBan : " + nomFicBan + ";" + resTotal);


        //delier l'OBSERVER
        controlRequete.removePropertyChangeListener();
        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        rechercheController.afficherResultat();

    }

    public void ajoutExtension(String extension, List<String> listeMc, List<String> listeBan){

        if (!nomFicRecherche.toString().equals("[]")) {
            for (String mot : listeMc){
                this.nomFicRecherche.add(mot + extension);
            }
        }

        if (!nomFicBan.toString().equals("[]")){
            for (String mot : listeBan){
                this.nomFicBan.add(mot+extension);
            }
        }


    }

}
