package ess;
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


public class MaschinenAgent extends Agent {
	private static final long serialVersionUID = 1L;
	
	int fertigstellungszeit = 0;
	int bearbeitungspauschale;
	
	protected void setup() {
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
		
		//Maschine als Leistungsanbieter vom Typ "maschine" registrieren
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("maschine");
		sd.setName(String.valueOf(args[0]));
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		System.out.println("MaschinenAgent "+getLocalName()+": Agent gestartet und registriert: Name=" + sd.getName() + ", Bearbeitungspauschale=" + bearbeitungspauschale + ", Typ=" + sd.getType());
		
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP) );

		addBehaviour(new ContractNetResponder(this, template) {
			private static final long serialVersionUID = 1L;

			// Reaktion auf eingehende Angebotsaufforderung. 
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("MaschinenAgent "+getLocalName()+": CFP erhalten von "+cfp.getSender().getName()+". Bearbeitungsdauer ist "+cfp.getContent());
				
				int bearbeitungszeit = Integer.parseInt( cfp.getContent() );
				int fruehsteFertigstellungszeit = fertigstellungszeit + bearbeitungszeit;
				

				ACLMessage propose = cfp.createReply();
				propose.setPerformative(ACLMessage.PROPOSE);
				
				// Inhalt der Nachricht setzen, damit der Job-Agent die Informationsgrundlage zur Entscheidung hat
				propose.setContent(fruehsteFertigstellungszeit + " " + bearbeitungspauschale);
				System.out.println("MaschinenAgent "+getLocalName()+": Sende folgendes Angebot: früheste Fertigstellungszeit=" + fruehsteFertigstellungszeit + ", Bearbeitungspauschale=" + bearbeitungspauschale);
				
				return propose;
			}

			// Reaktion auf eingehende Akzeptanz eines vorherigen Angebots.
			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
				System.out.println("MaschinenAgent "+getLocalName()+": Empfange Annahme des gesendeten Angebots ... Bearbeitung ... Sende Inform für Abschluss der Aktion");
				fertigstellungszeit+= Integer.valueOf(accept.getContent());
				ACLMessage inform = accept.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
	
			}

			// Reaktion auf die Ablehnung eines Angebots. 
			@Override
			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				System.out.println("MaschinenAgent "+getLocalName()+": Empfange Ablehnung des gesendeten Angebots");
			}
		} );
	}
}
