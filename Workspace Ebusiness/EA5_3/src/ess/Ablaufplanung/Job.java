package ess.Ablaufplanung;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Job
* @author ontology bean generator
* @version 2016/01/15, 17:02:17
*/
public class Job implements Concept {

   /**
* Protege name: bearbeitungsdauer
   */
   private int bearbeitungsdauer;
   public void setBearbeitungsdauer(int value) { 
    this.bearbeitungsdauer=value;
   }
   public int getBearbeitungsdauer() {
     return this.bearbeitungsdauer;
   }

}
