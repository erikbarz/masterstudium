package ess.Ablaufplanung;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Angebot
* @author ontology bean generator
* @version 2016/01/15, 17:02:17
*/
public class Angebot implements Concept {

   /**
* Protege name: fertigstellungstermin
   */
   private int fertigstellungstermin;
   public void setFertigstellungstermin(int value) { 
    this.fertigstellungstermin=value;
   }
   public int getFertigstellungstermin() {
     return this.fertigstellungstermin;
   }

   /**
* Protege name: preis
   */
   private int preis;
   public void setPreis(int value) { 
    this.preis=value;
   }
   public int getPreis() {
     return this.preis;
   }

}
