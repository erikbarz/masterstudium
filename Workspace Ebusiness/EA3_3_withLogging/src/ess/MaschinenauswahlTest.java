 package ess;


public class MaschinenauswahlTest {

	public static void main(String[] args) {
		int fruehsteFertigstellungszeit = -1;
		String accept = null;
		
// 		test für Job 1 auf allen Maschinen		
		int bearbeitungsdauer = 10;
		int budget = 40;
				
		String antwortM1 = "10 10 M1";
		String antwortM2 = "10 10 M2";
		String antwortM3 = "10 30 M3";
		
// 		test für Job 2 auf allen Maschinen		
//		int bearbeitungsdauer = 20;
//		int budget = 30;		
//		
//		String antwortM1 = "30 10 M1";
//		String antwortM2 = "20 10 M2";
//		String antwortM3 = "20 30 M3";
		
		
// 		test für Job 3 auf allen Maschinen		
//		int bearbeitungsdauer = 15;
//		int budget = 35;		
//		
//		String antwortM1 = "25 10 M1";
//		String antwortM2 = "35 10 M2";
//		String antwortM3 = "15 30 M3";
		
// 		test für Job 4 auf allen Maschinen		
//		int bearbeitungsdauer = 18;
//		int budget = 40;
//				
//		String antwortM1 = "28 10 M1";
//		String antwortM2 = "38 10 M2";
//		String antwortM3 = "33 30 M3";
		
// 		test für Job 5 auf allen Maschinen		
//		int bearbeitungsdauer = 7;
//		int budget = 20;
//				
//		String antwortM1 = "35 10 M1";
//		String antwortM2 = "27 10 M2";
//		String antwortM3 = "22 30 M3";
		
		String[] messages = new String[3];
		messages[0] = antwortM1;
		messages[1] = antwortM2;
		messages[2] = antwortM3;
		
		System.out.println("initialer Stand: " + fruehsteFertigstellungszeit + " , " + accept);
		
		//Content der Messages auswerten
		for(String msg : messages){
			String[] antwort = msg.split(" ");
			int angebotFertigstellung = Integer.parseInt(antwort[0]);
			int angebotKosten = Integer.parseInt(antwort[1]);
			
			// Prüfung, ob Kosten im Budget liegen
			if(angebotKosten <= budget)
			{
				// wenn die erste Maschine, die im Budget liegt, durchlaufen wird, als beste setzen
				if(accept == null){
					accept = msg;
					fruehsteFertigstellungszeit = angebotFertigstellung;
					System.out.println("Initiales Setzen des besten Angebots: " + msg);
				}else{
					// Falls Fertigungszeit kleiner als bisherige beste, neu setzen
					if(angebotFertigstellung < fruehsteFertigstellungszeit){
						fruehsteFertigstellungszeit = angebotFertigstellung;
						accept = msg;
						System.out.println("Setzen des besten Angebots: " + msg);
					}else{
						//TODO: Absage
					}
				}
					
				
			// wenn Kosten nicht im Budget liegen
			}else{
				//TODO: Absage schicken
				System.out.println("Kosten der Maschine liegen nicht im Budget: " + msg);
			}
			System.out.println("Zwischen-Stand: " + accept);
		}
		System.out.println("finaler Stand: " + accept);
		// Status der ersten Nachricht (initiales setzen des besten angebots) auf absage setzen, falls eine andere besser war
		
		//TODO hier nachrichten rausschreiben
		
		
		
	}

}
