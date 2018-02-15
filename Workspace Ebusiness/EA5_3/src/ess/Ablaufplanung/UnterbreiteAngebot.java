package ess.Ablaufplanung;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * durch diese Aktion wird einem Jobagenten ein Angebot für die Bearbeitung eines Jobs unterbreitet
* Protege name: UnterbreiteAngebot
* @author ontology bean generator
* @version 2016/01/15, 17:02:17
*/
public class UnterbreiteAngebot implements AgentAction {

   /**
* Protege name: angebot
   */
   private Angebot angebot;
   public void setAngebot(Angebot value) { 
    this.angebot=value;
   }
   public Angebot getAngebot() {
     return this.angebot;
   }

}
