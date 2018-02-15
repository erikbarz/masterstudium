package ess;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import ess.Ablaufplanung.AP_Ontology;
import ess.Ablaufplanung.AkzeptiereAngebot;
import ess.Ablaufplanung.AnfrageAngebot;
import ess.Ablaufplanung.Angebot;
import ess.Ablaufplanung.UnterbreiteAngebot;

/**
* @author Erik Barz / Matrikel-Nr: 3096050
*/
public class MaschinenAgent extends Agent {
	private static final long serialVersionUID = 1L;
	
	int fertigstellungszeit = 0;
	int bearbeitungspauschale;
	
	private Codec codec = new SLCodec();
	private Ontology ontology = AP_Ontology.getInstance();
	
	private void registerAgent(String name) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("maschine");
		sd.setName(String.valueOf(name));
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("MaschinenAgent "+getLocalName()+": Agent gestartet und registriert: Name=" + getName() + ", Bearbeitungspauschale=" + bearbeitungspauschale + ", Typ=" + sd.getType());
	}
	
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		Object[] args = getArguments();
		if( args.length < 2 ) {
			System.out.println( "MaschinenAgent "+getLocalName()+": Maschinenidentifizierung Mx und Bearbeitungspauschale als Parameter angeben" );
			this.doDelete();
			return;
		}
		try {
			this.bearbeitungspauschale = Integer.parseInt(args[1].toString());
		} catch( Exception e) {
			System.out.println( "MaschinenAgent "+getLocalName()+": Bearbeitungspauschale muss vom Typ Integer sein" );
			this.doDelete();
			return;
		}
		
		//registriere diese Maschine als Leistungsanbieter vom Typ "maschine"
		registerAgent(args[0].toString());
		
		//Verhalten für eingehende Nachrichten implementieren und registrieren
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP) );

		addBehaviour(new ContractNetResponder(this, template) {
			private static final long serialVersionUID = 1L;

			//Reaktion auf eingehende Angebotsaufforderung. 
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				
				ACLMessage propose = null;				
				try {
					//Aktion aus Nachricht extrahieren
					ContentElement content = getContentManager().extractContent(cfp);
					Concept action = ((Action)content).getAction();
					AnfrageAngebot aa = (AnfrageAngebot)action;
			
					int bearbeitungszeit = aa.getJob().getBearbeitungsdauer();
					System.out.println("MaschinenAgent "+getLocalName()+": CFP erhalten von "+cfp.getSender().getLocalName()+". Bearbeitungszeit="+bearbeitungszeit);
					
					propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					
					//Format setzen analog Template --> Protokoll, Sprache und Ontologie setzen; 
					propose.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
					propose.setLanguage(codec.getName());
					propose.setOntology(ontology.getName());
					
					
					// Objekt vom Typ UnterbreiteAngebot erstellen und als Content setzen
					Angebot a = new Angebot();
					a.setFertigstellungstermin((bearbeitungszeit+fertigstellungszeit));
					a.setPreis(bearbeitungspauschale);
					UnterbreiteAngebot ua = new UnterbreiteAngebot();
					ua.setAngebot(a);
					getContentManager().fillContent(propose,  new Action(cfp.getSender(), ua));
					
					System.out.println("MaschinenAgent "+getLocalName()+": Sende folgendes Angebot: Bearbeitungspauschale=" + bearbeitungspauschale + ", frühester Fertigstellungstermin=" + (bearbeitungszeit+fertigstellungszeit));
				
				} catch( Exception e) {
					e.printStackTrace();
				}
				return propose;
			}

			//Reaktion auf eingehende Akzeptanz eines vorherigen Angebots.
			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {

				System.out.println("MaschinenAgent "+getLocalName()+": Empfange Annahme des gesendeten Angebots ... Bearbeitung ... Sende Inform für Abschluss der Aktion");
									
				// AkzeptiereAngebot aus Content der Nachricht "accept" parsen
				ContentElement acceptContent = null;
				AkzeptiereAngebot aa = null;
				try {
					acceptContent = getContentManager().extractContent(accept);
					Concept action = ((Action)acceptContent).getAction();
					aa = (AkzeptiereAngebot)action;
				} catch (Exception e) {
					System.out.println("MaschinenAgent "+getLocalName()+": Fehler bei der Verarbeitung der Accept Nachricht vom Job");
					e.printStackTrace();
				} 			
				
				// Angebot aus Content der nachricht "propose" parsen
				ContentElement proposeContent = null;
				UnterbreiteAngebot ua = null;
				try {
					proposeContent = getContentManager().extractContent(propose);
					Concept action = ((Action)proposeContent).getAction();
					ua = (UnterbreiteAngebot)action;
					
					//neue Fertistellungszeit setzen
					fertigstellungszeit = ua.getAngebot().getFertigstellungstermin();

				} catch (Exception e) {
					System.out.println("MaschinenAgent "+getLocalName()+": Fehler bei der Verarbeitung der Propose Nachricht vom Job (nach Akzeptieren)");
					e.printStackTrace();
				} 					
				
				// Inform Nachricht an den Job schicken als Bestätigung
				ACLMessage inform = accept.createReply();
				inform.setPerformative(ACLMessage.INFORM);
	
				return inform;
				
			}

			//Reaktion auf die Ablehnung eines Angebots. 
			@Override
			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {

				System.out.println("MaschinenAgent "+getLocalName()+": Empfange Ablehnung des gesendeten Angebots");
			}
		} );
	}
}
