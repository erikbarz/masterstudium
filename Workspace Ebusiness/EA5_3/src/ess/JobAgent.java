package ess;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
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

import ess.Ablaufplanung.AP_Ontology;
import ess.Ablaufplanung.AkzeptiereAngebot;
import ess.Ablaufplanung.AnfrageAngebot;
import ess.Ablaufplanung.Job;
import ess.Ablaufplanung.UnterbreiteAngebot;

/**
* @author Erik Barz / Matrikel-Nr: 3096050
*/
public class JobAgent extends Agent {
	
	private Codec codec = new SLCodec();
	private Ontology ontology = AP_Ontology.getInstance();

	int bearbeitungsdauer = 0;
	int budget;
	
	//Abfrage des Agent-Directory um Maschinenagenten zu ermitteln
	private AID[] getMaschineAgents() {
		AID[] maschinenAgenten = null;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("maschine");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template); 
			maschinenAgenten = new AID[result.length];
			String agentenNamen = "";
			for (int i = 0; i < result.length; ++i) {
				maschinenAgenten[i] = result[i].getName();
				agentenNamen += maschinenAgenten[i].getLocalName() + ", ";
			}
			System.out.println("-----------------------------------------------------------------------------------");
			
			System.out.println("Jobagent "+getLocalName()+": Agent gestartet. Name=" + getName() + ", Budget=" + budget + ", Bearbeitungsdauer=" + bearbeitungsdauer);
			System.out.println("Jobagent "+getLocalName()+": " + result.length + " Maschinen-Agenten im ADS gefunden: " + agentenNamen);
		}
		catch (FIPAException fe) {
			System.out.println("Jobagent "+getLocalName()+": schwerer Fehler bei der Suche nach MaschinenAgenten");
			fe.printStackTrace();
		}
		return maschinenAgenten;
	}
	
	
	@SuppressWarnings("serial")
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		//Bearbeitungsdauer und Budget des Auftrags aus den übergebenen Parametern lesen
		Object[] args = getArguments();
		if( args.length < 2 ) {
			System.out.println( "Jobagent "+getLocalName()+": keine Bearbeitungsdauer für den Job angegeben" );
			this.doDelete();
			return;
		}
		try {
			bearbeitungsdauer = Integer.parseInt(args[0].toString());
			budget = Integer.parseInt(args[1].toString());
		} catch( Exception e) {
			System.out.println( "Jobagent "+getLocalName()+": Bearbeitungsdauer und Budget eines Jobs müssen vom Typ Integer sein" );
			this.doDelete();
			return;
		}
		
		//Agenten vom Typ "maschine" vom Verzeichnisdienst abfragen
		AID[] maschinenAgenten = getMaschineAgents();
		
		//Aktion "AnfrageAngebot" erstellen
		Job j = new Job();
		j.setBearbeitungsdauer(bearbeitungsdauer);
		AnfrageAngebot aa = new AnfrageAngebot();
		aa.setJob(j);
		
		//Nachricht (Call for Proposal - CFP) gemäß des Kontraktnetz-Protokolls 
		//vorbereiten, die an alle gefundenen Agenten versendet wird.

		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		
		
		for (AID aid : maschinenAgenten) {		
			msg.addReceiver(aid);
			try {
				getContentManager().fillContent(msg, new Action(aid, aa));
			} catch (CodecException | OntologyException e) {
				System.out.println("Jobagent "+getLocalName()+": Schwerer Fehler beim Versenden der Anfragen");
				e.printStackTrace();
			}
		}
		System.out.println("Jobagent "+getLocalName()+": Content der Nachricht wurde an alle gefundenen Maschinenagenten geschrieben - Bearbeitungsdauer=" + bearbeitungsdauer );
		
		
		//Antwortverhalten für die Nachricht als Subklasse der Klasse <ContractNetInitiator>
		//implementieren und registrieren.
		addBehaviour(new ContractNetInitiator(this, msg) {

			//Überschreiben der Funktion <handleAllResponses>, um das Auswahlverhalten
			//des Auftragsagenten nach Erhalt aller Vertragsvorschläge zu implementieren 
			@Override
			protected void handleAllResponses(Vector responses, Vector acceptances) {
				//Auswertung der Vorschläge und ermitteln des besten Vorschlags.
				//Gleichzeitig werden die Antworten an alle Resourcenagenten vorbereitet
				int fruehsteFertigstellungszeit = -1;
				ACLMessage accept = null;
				
				//Implementierung der Auswahl der Maschine 
				//System.out.println("Jobagent "+getLocalName()+": beginne Auswertung der Angebote. Initialer Stand: " + fruehsteFertigstellungszeit + " ," + accept );
				
				Enumeration e = responses.elements();
				while (e.hasMoreElements()) {
					ACLMessage msg = (ACLMessage) e.nextElement();
					if (msg.getPerformative() == ACLMessage.PROPOSE) {							
						
						//Content der Messages auswerten
						// Angepasst analog Maschinenagent --> Angebot aus Content parsen und Werte auslesen
						ContentElement proposeContent = null;
						UnterbreiteAngebot ua = null;
						int proposeFertigstellung = -1;
						int proposeKosten=-1;
						try {
							proposeContent = getContentManager().extractContent(msg);
							Concept action = ((Action)proposeContent).getAction();
							ua = (UnterbreiteAngebot)action;

							proposeFertigstellung = ua.getAngebot().getFertigstellungstermin();
							proposeKosten = ua.getAngebot().getPreis();
							
						} catch (Exception exc) {
							System.out.println("JobAgent "+getLocalName()+": schwerer Fehler bei der Verarbeitung der Propose Nachricht vom MaschinenAgenten");
							exc.printStackTrace();
						} 							
						
						// Prüfung, ob Kosten im Budget liegen
						if(proposeKosten <= budget)
						{
							// wenn die erste Maschine, die im Budget liegt, durchlaufen wird, diese als aktuell beste setzen (Accept)
							if(accept == null){
								accept = msg;
								fruehsteFertigstellungszeit = proposeFertigstellung;
								System.out.println("Jobagent "+getLocalName()+": Initiales Setzen des besten Angebots: " + msg.getSender().getLocalName() + ", Kosten=" + proposeKosten + ", Fertigstellungstermin=" + proposeFertigstellung);
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
									System.out.println("Jobagent "+getLocalName()+": besseres Angebot gefunden und gesetzt: "+ msg.getSender().getLocalName() + ", Kosten=" + proposeKosten + ", Fertigstellungstermin=" + proposeFertigstellung);
								}else{
									//Absage vorbereiten
									ACLMessage reply = msg.createReply();
									reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
									acceptances.addElement(reply);
								}
							}								
							
						// wenn Kosten nicht im Budget liegen
						}else{							
							System.out.println("Jobagent "+getLocalName()+": Kosten der aktuell geprüften Maschine liegen nicht im Budget: " + msg.getSender().getLocalName() + ", Kosten=" + proposeKosten + ", Fertigstellungstermin=" + proposeFertigstellung);
							//Absage vorbereiten
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
							acceptances.addElement(reply);
						}
						//System.out.println("Jobagent "+getLocalName()+": Zwischen-Stand der Auswertung. Aktuell bestes Angebot: " + accept.getContent() + ", Fertigstellungszeit: " + fruehsteFertigstellungszeit);
						
						
					}
				} // Ende der While Schleife	
				
				System.out.println("Jobagent "+getLocalName()+": finaler Stand der Auswertung. Bestes Angebot: " + accept.getSender().getLocalName() + ", Fertigstellungstermin=" + fruehsteFertigstellungszeit);

				
				//Akzeptieren des besten Angebotes vorbereiten und den entsprechenden
				//Ressourcenagenten durch eine Nachricht informieren. 				
				if (accept != null) {
					//Erstellen der Akzeptanznachricht 					
					ACLMessage reply = accept.createReply();
					
					// Erstellung des Objekt vom Typ AkzeptiereAngebot
					AkzeptiereAngebot aa = new AkzeptiereAngebot();
					
					try {
						getContentManager().fillContent(reply,  new Action(accept.getSender(), aa));
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						//Format setzen analog Template --> Protokoll, Sprache und Ontologie setzen; 
						reply.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
						reply.setLanguage(codec.getName());
						reply.setOntology(ontology.getName());
						
						acceptances.addElement(reply);
					} catch (Exception e1) {
						System.out.println("Jobagent "+getLocalName()+": schwerer Fehler bei der Erstellung der Akzeptanz-Nachricht");
						e1.printStackTrace();
					} 									

				}
			}
		});
	}

}
