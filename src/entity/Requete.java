package entity;

import fr.dgac.ivy.IvyException;


public class Requete{
    private CommunicationIvy communicationIvy= CommunicationIvy.getInstance();
    private String mot;
    private EtatRequete etatRequete = EtatRequete.WAITING_FOR_INIT;
    private String resultat="";

    public Requete(String mot){
        this.mot = mot;
    }

    public void init(){
        try {

            communicationIvy.definirBind(mot);

            etatRequete = EtatRequete.READY_FOR_START;

        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            communicationIvy.envoieMessage("Interface message=rechercheMotCle source=" + mot);
            etatRequete = EtatRequete.RUNNABLE;
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public EtatRequete getEtatRequete() {
        return etatRequete;
    }
}
