import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
	
	static int port = 11112;
	static java.net.ServerSocket serverSocket = null;
	
	static private String leseNachricht(Socket s) throws Exception {
		 BufferedReader br = 
		            new BufferedReader(
		                new InputStreamReader(
		                    s.getInputStream()));
		        char[] buffer = new char[200];
		        int anzahlZeichen = br.read(buffer, 0, 200); 
		        String nachricht = new String(buffer, 0, anzahlZeichen);
		        return nachricht;
	}
	
	static public String erstelleSocketUndLeseNachricht() throws Exception{
		
		String nachricht = "";
		
		if(serverSocket == null){
			serverSocket = new ServerSocket(port);
		}
		
		try {
			java.net.Socket client = warteAufAnmeldung(serverSocket);
			System.out.println("Server - Client am Server angemeldet, beginne Lesen der Nachricht");
			
			nachricht = leseNachricht(client);
			System.out.println("Server - empfangene Nachricht: " + nachricht);
			
		} catch (Exception e) {
			System.err.println("Server - Fehler beim Erstellen des Sockets/Lesen der Nachricht: " + e);
			throw e;
		}
		
		
		return nachricht;
	}
	
static public BestellPosition unmarschalNachricht( String msg ) {
		
		BestellPosition bp = new BestellPosition();
		
		// auflösen der einzelnen Variablen aus der Nachricht: 
		
		try {			
			bp.material = msg.substring(0,msg.indexOf("|"));
			bp.material = bp.material.substring(msg.indexOf("=")+1);
			msg = msg.substring(msg.indexOf("|")+1);
			bp.preis = msg.substring(0,msg.indexOf("|"));
			bp.preis = bp.preis.substring(msg.indexOf("=")+1);
			msg = msg.substring(msg.indexOf("|")+1);
			bp.anzahl=Integer.parseInt(msg.substring(msg.indexOf("=")+1));
			
			System.out.println("Server - Unmarshalling abgeschlossen");
			
		} catch (Exception e) {
			System.err.println("Server - Fehler beim Unmarshalling: " + bp.toString() + e);
			throw e;
		}	
		
		return bp;
	}
	
	public static void bearbeiteBestellung( BestellPosition bp) {
		System.out.println("Server - Bestellung erhalten:\n#######");
		System.out.println( bp );
		
	}
	
	public static void empfangeBestellung() throws Exception {
		String msg = erstelleSocketUndLeseNachricht();
		BestellPosition bp = unmarschalNachricht(msg);
		bearbeiteBestellung(bp);
	}
	

	public static void main(String[] args) throws Exception  {
		System.out.println("Server - Server wird gestartet");
		System.out.println("-----------------------------------------------------");
		
		while( true ) {
			empfangeBestellung();
			System.out.println("-----------------------------------------------------");
		}
			
	}

	static java.net.Socket warteAufAnmeldung(java.net.ServerSocket serverSocket) throws IOException {
		System.out.println("Server - warte auf Anmeldung eines Clients");
		java.net.Socket socket = serverSocket.accept(); // blockiert, bis sich
														// ein Client angemeldet
														// hat
		return socket;
	}
}
