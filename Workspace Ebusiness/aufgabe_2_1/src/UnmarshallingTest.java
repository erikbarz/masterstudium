
public class UnmarshallingTest {

	public static void main(String[] args) {
		String msg = "Material=Stuhl|Preis=302.91|Anzahl=5";
		System.out.println(unmarschalNachricht(msg).toString());
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
	
}
