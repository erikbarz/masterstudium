import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
	
	public static void schreibeNachricht( Socket s, String msg) throws Exception {
		PrintWriter printWriter =
	            new PrintWriter(
	                new OutputStreamWriter(
	                    s.getOutputStream()));
	        printWriter.print(msg);
	        printWriter.flush();
	}
	
	public static void verbindeUndSchreibeNachricht(String msg) throws Exception {
		System.out.println("Client - starte Verbindung zum Server ...");
		String ip = "127.0.0.1"; // localhost
	 	int port = 11112;
	 	java.net.Socket socket = new java.net.Socket(ip,port); // verbindet sich mit Server	 	
	 	System.out.println("Client - ... Verbindung hergestellt");
	  	schreibeNachricht(socket, msg);
	   			
	}
	
	public static String marshall( BestellPosition bp) {
		String msg = "";
		msg="Material=" + bp.material + "|" + "Preis=" + bp.preis + "|" + "Anzahl=" + bp.anzahl;
		
		System.out.println("Client - Marshalling abgeschlossen. abzusetzende Nachricht: \n#######\n" + msg);
		return msg;
	}
	
	public static void sendeBestellung(BestellPosition bp) throws Exception {
		String msg = marshall(bp);
		verbindeUndSchreibeNachricht(msg);	
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("Client - starte Bestellungvorgang");
		
		BestellPosition bp = new BestellPosition();
		
		bp.material = "Stuhl";
		bp.preis = "302.91";
		bp.anzahl = 5;
		
		sendeBestellung(bp);
		
		System.out.println("Client - Bestellungvorgang abgeschlossen");
	}
}
