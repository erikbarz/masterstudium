import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

// Interface für Server
interface BestellServer extends Remote{
	public void bestelle( BestellPosition bp) throws RemoteException;
}

// Implementierung des Server Interfaces
public class BestellServerImpl implements BestellServer{
	
	
	public void bestelle( BestellPosition bp) throws RemoteException {
		System.out.println("Server - Bestellung erhalten:\n###########");
		System.out.println( bp );
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Server - Server wird gestartet");
		
		// RMI Registry starten
		try
		{
			System.out.println("Server - RMI Registry wird gestartet");
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT );
		}
		catch ( RemoteException e ){
			System.err.println("Server - Fehler beim Starten der Registry: " + e);			
		}
		
		// RMI Anmeldung
		try{
			System.out.println( "Server - melde Dienst an RMI an" );
			
			BestellServer server = new BestellServerImpl();
			server = (BestellServer) UnicastRemoteObject.exportObject( server, 0 );
		    RemoteServer.setLog( System.out );
	
		    Registry registry = LocateRegistry.getRegistry();
		    registry.rebind( "FernUniHagenRMIserver", server );
			    
		}catch(Exception e){
			System.err.println("Server - Fehler bei der Anmeldung an der Registry: " + e);	
		}

	}
	
}
