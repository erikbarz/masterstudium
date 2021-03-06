package ess;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Enumeration;
import java.util.Vector;


public class JobAgent extends Agent {

	int bearbeitungsdauer = 0;
	int budget;
	
	@SuppressWarnings("serial")
	protected void setup() {
		//Bearbeitungsdauer des Auftrags aus dem �bergebenen Parameter lesen
		Object[] args = getArguments();
		if( args.length < 2 ) {
			System.out.println( "Jobagent "+getLocalName()+": keine Bearbeitungsdauer f�r den Job angegeben" );
			this.doDelete();
			return;
		}
		try {
			bearbeitungsdauer = Integer.parseInt(args[0].toString());
			budget = Integer.parseInt(args[1].toString());
		} catch( Exception e) {
			System.out.println( "Jobagent "+getLocalName()+": Bearbeitungsdauer und Budget eines Jobs m�ssen vom Typ Integer sein" );
			this.doDelete();
			return;
		}
		
		//Agenten vom Typ "maschine" vom Verzeichnisdienst abfragen
		//und im Array resourcenAgenten speichern
		AID[] maschinenAgenten = null;

		//Abfage des Agent-Directory-Service analog http://jade.tilab.com/doc/tutorials/JADEProgramming-Tutorial-for-beginners.pdf
		// Setzen der Liste mit Maschinen-Agenten
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("maschine");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			maschinenAgenten = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				maschinenAgenten[i] = result[i].getName();
			}
			System.out.println("-----------------------------------------------------------------------------------");
			
			System.out.println("Jobagent "+getLocalName()+": Agent gestartet, " + result.length + " Maschinen-Agenten im ADS gefunden");
		} catch (FIPAException fe) {
			System.err.println("Fehler bei der Abfrage des ADS: " + fe);
			fe.printStackTrace();
		}

		
		//Nachricht (Call for Proposal - CFP) gem�� des Kontraktnetz-Protokolls 
		//vorbereiten, die an alle gefundenen Agenten versendet wird.
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		for (AID aid : maschinenAgenten) {		
			msg.addReceiver(aid);
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			
			// Bearbeitungszeit des Jobs in den Content der Nachricht schreiben
			msg.setContent(Integer.toString(bearbeitungsdauer));
						
		}
		System.out.println("Jobagent "+getLocalName()+": Content der Nachricht wurde an alle gefundenen Maschinenagenten geschrieben - Bearbeitungsdauer=" + bearbeitungsdauer );
		
		
		//Antwortverhalten f�r die Nachricht als Subklasse der Klasse <ContractNetInitiator>
		//implementieren und registrieren.
		addBehaviour(new ContractNetInitiator(this, msg) {

			//�berschreiben der Funktion <handleAllResponses> um das Auswahlverhalten
			//des Auftragsagenten nach Erhalt aller Vertragsvorschl�ge zu implementieren 
			@Override
			protected void handleAllResponses(Vector responses, Vector acceptances) {
				//Auswertung der Vorschl�ge und ermitteln des besten Vorschlags.
				//Gleichzeitig werden die Antworten an alle Resourcenagenten vorbereitet
				
				int fruehsteFertigstellungszeit = -1;
				ACLMessage accept = null;
				
				Enumeration e = responses.elements();
				
				//Implementierung der Auswahl der Maschine 
				//System.out.println("Jobagent "+getLocalName()+": beginne Auswertung der Angebote. Initialer Stand: " + fruehsteFertigstellungszeit + " ," + accept );
				
				while (e.hasMoreElements()) {
					ACLMessage msg = (ACLMessage) e.nextElement();
					if (msg.getPerformative() == ACLMessage.PROPOSE) {							
						
						//Content der Messages auswerten						
						String[] proposeContent = msg.getContent().split(" ");
						int proposeFertigstellung = Integer.parseInt(proposeContent[0]);
						int proposeKosten = Integer.parseInt(proposeContent[1]);
						
						// Pr�fung, ob Kosten im Budget liegen
						if(proposeKosten <= budget)
						{
							// wenn die erste Maschine, die im Budget liegt, durchlaufen wird, diese als aktuell beste setzen (Accept)
							if(accept == null){
								accept = msg;
								fruehsteFertigstellungszeit = proposeFertigstellung;
								System.out.println("Jobagent "+getLocalName()+": Initiales Setzen des besten Angebots: Fertigstellung=" + proposeFertigstellung + ", Kosten=" + proposeKosten + ", " + msg.getSender().getLocalName());
							}else{
								// Falls Fertigungszeit kleiner als bisherige beste, Accept neu setzen
								if(proposeFertigstellung < fruehsteFertigstellungszeit){
									// Absage an bisherigen besten vorbereiten
									ACLMessage reply = accept.createReply();
									reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
									acceptances.addElement(reply);
									
									// Variablen neu setzen
									fruehsteFertigstellungszeit = proposeFertigstellung;
									accept = msg;
									System.out.println("Jobagent "+getLocalName()+": besseres Angebot gefunden und gesetzt: Fertigstellung=" + proposeFertigstellung + ", Kosten=" + proposeKosten + ", " + msg.getSender().getLocalName());
								}else{
									//Absage vorbereiten
									ACLMessage reply = msg.createReply();
									reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
									acceptances.addElement(reply);
								}
							}								
							
						// wenn Kosten nicht im Budget liegen
						}else{							
							System.out.println("Jobagent "+getLocalName()+": Kosten der aktuell gepr�ften Maschine liegen nicht im Budget:  Fertigstellung=" + proposeFertigstellung + ", Kosten=" + proposeKosten + ", " + msg.getSender().getLocalName());
							//Absage vorbereiten
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
							acceptances.addElement(reply);
						}
						//System.out.println("Jobagent "+getLocalName()+": Zwischen-Stand der Auswertung. Aktuell bestes Angebot: " + accept.getContent() + ", Fertigstellungszeit: " + fruehsteFertigstellungszeit);
						
						
					}
				} // Ende der While Schleife			
				System.out.println("Jobagent "+getLocalName()+": finaler Stand der Auswertung: bestes Angebot von " + accept.getSender().getLocalName() + ", Fertigstellungszeit: " + fruehsteFertigstellungszeit);

				
				//Akzeptieren des besten Angebotes vorbereiten und den entsprechenden
				//Ressourcenagenten durch eine Nachricht informieren. 				
				if (accept != null) {
					//Erstellen der Akzeptanznachricht 
					ACLMessage reply = accept.createReply();
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent(bearbeitungsdauer +"");
					acceptances.addElement(reply);
				}	
									
			}} );
	  } 

}
