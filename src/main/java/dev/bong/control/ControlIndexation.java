package dev.bong.control;

import dev.bong.entity.Requete;
import dev.bong.entity.TypeRequete;

public class ControlIndexation extends Thread {
    private static ControlRequete controlRequete = new ControlRequete(TypeRequete.INDEXATION);

    private boolean requeteForcee;

    public ControlIndexation(boolean requeteForcee){
        this.requeteForcee = requeteForcee;
    }

    public void run() {
        boolean requeteFinit = false;

        //Lancer la com
        controlRequete.lancerCommunicationBus();

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Utilisation particulière de controlRequete, car on ne soumet ici qu'une seule requête spécifique à l'indexation

        controlRequete.creerRequeteIndexation(requeteForcee);

        for (Requete requete : controlRequete.getListeRequete()){
            controlRequete.envoyerRequete(requete);
        }

        while (!requeteFinit){
            try {
                requeteFinit = controlRequete.touteRequeteFinit();
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(controlRequete.getListeResultat());


        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        //delier
        controlRequete.removePropertyChangeListener();
    }

    public static void IndexationDuModeOuvert(){
        controlRequete.creerRequeteIndexation(false);

        for (Requete requete : controlRequete.getListeRequete()){
            controlRequete.envoyerRequete(requete);
        }

        boolean requeteFinit = false;

        while (!requeteFinit){
            try {
                requeteFinit = controlRequete.touteRequeteFinit();
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}