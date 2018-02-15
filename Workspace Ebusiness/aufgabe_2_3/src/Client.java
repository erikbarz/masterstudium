import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Client {
	public static void main(String[] args) throws Exception {
		
		// Bestellung initialisieren
		BestellPosition bp = new BestellPosition();
		bp.material = "Stuhl";
		bp.preis = "302.91";
		bp.anzahl = 5;
		
		// Dienst bei RMI anfragen
		Registry registry = null;
		BestellServer server = null;
		
		try{
			System.out.println( "Client - suche nach Dienst" );
			registry = LocateRegistry.getRegistry();
			server = (BestellServer) registry.lookup( "FernUniHagenRMIserver" );
		}catch(Exception e){
			System.err.println( "Client - Fehler bei der Suche nach Dienst: " + e);
		}
		
		if(registry!=null && server !=null){
			try {
				System.out.println( "Client - setze Bestellung ab: \n###########\n" + bp );
				server.bestelle(bp);
			} catch (Exception e) {
				System.err.println( "Client - Fehler beim Absetzen der Bestellung: " + e);
			}
		}
		
	}
}
