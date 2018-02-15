package ess.Ablaufplanung;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * veranlasst einen Maschinenagenten ein Angebot für die Bearbeitung eines Jobs zu erstellen
* Protege name: AnfrageAngebot
* @author ontology bean generator
* @version 2016/01/15, 17:02:17
*/
public class AnfrageAngebot implements AgentAction {

   /**
* Protege name: job
   */
   private Job job;
   public void setJob(Job value) { 
    this.job=value;
   }
   public Job getJob() {
     return this.job;
   }

}
