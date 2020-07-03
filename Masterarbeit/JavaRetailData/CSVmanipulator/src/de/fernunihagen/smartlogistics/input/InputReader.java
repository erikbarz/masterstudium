package de.fernunihagen.smartlogistics.input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fernunihagen.smartlogistics.dataobjects.AddressDTO;
import de.fernunihagen.smartlogistics.dataobjects.CustomerDTO;
import de.fernunihagen.smartlogistics.dataobjects.OrderDTO;

public class InputReader {

	public static List<AddressDTO> readAddressRecords(String adressInputFilePath) {
		final String COMMA_DELIMITER = ",";
		
		// 1. File einlesen
				List<AddressDTO> addressRecords = new ArrayList<>();
				System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte einlesen der Adress-Input-Datei");
				try (BufferedReader br = new BufferedReader(new FileReader(adressInputFilePath))) {
				    String line;
				    while ((line = br.readLine()) != null) {
				        // einzelne Attribute des Datensatzes trennen 
				    	String[] recordRawData = line.split(COMMA_DELIMITER);

				        // 2. Daten parsen
				    	AddressDTO address = new AddressDTO(recordRawData[1], recordRawData[3], recordRawData[6], recordRawData[5]);
				    	addressRecords.add(address);
				        //System.out.println(order);
				       // System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte einlesen der Adress-Input-Datei - Datensatz Nr. " + addressRecords.size());
				    }    
				    System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Einlesen der Adress-Input-Datei abgeschlossen: " + addressRecords.size() + " Datensätze gelesen");
				    br.close();
				    return addressRecords;
				    
				    
				} catch (FileNotFoundException e) {
					System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Fehler beim Einlesen der Adress-Datei : Datei konnte nicht gefunden werden");
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Fehler beim Einlesen der Adress-Datei : IO Fehler");
					e.printStackTrace();
					return null;
				}
		
	}
	
	public static List<OrderDTO> readAndManipulateOrderRecords(String inputFilePath) {
		
		final String COMMA_DELIMITER = ";";
				
		// 1. File einlesen
		List<OrderDTO> orderRecords = new ArrayList<>();
		Map<String, String> invoiceKepMapping = new HashMap();
		int ordersWithoutCustomerId = 0;
		boolean headerLine=true;
		System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte einlesen der Order-Input-Datei");
		try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        // einzelne Attribute des Datensatzes trennen 
		    	String[] recordRawData = line.split(COMMA_DELIMITER);
		    	
		    	if(!headerLine) {
		    		String kepDL = "";
			    	// prüfen, ob der invoice-No bereits ein KEP zugewiesen wurde
			    	if(invoiceKepMapping.containsKey(recordRawData[0])) {
			    		// falls ja, diesen wiederverwenden
			    		kepDL=invoiceKepMapping.get(recordRawData[0]);		    		
			    	}else {
			    		// falls nein, neuen vergeben und in Map speichern
			    		kepDL=TestDataGenerator.getRandomKepDl();
			    		invoiceKepMapping.put(recordRawData[0], kepDL);
					}
			    		
			    	
			        // 2. Daten parsen und KEP-DL zufällig zuweisen
			        OrderDTO order = new OrderDTO(recordRawData[0], recordRawData[1], recordRawData[2], recordRawData[3], recordRawData[4], recordRawData[5], recordRawData[6], recordRawData[7], kepDL);
			        
			        // nur hinzufügen, wenn Customer gepflegt
			        if(!order.getCustomerID().equals(null) && !order.getCustomerID().equals("")) {
			        	orderRecords.add(order);
			        }else {
			        	ordersWithoutCustomerId++;
			        }
			        //System.out.println(order);
			        //System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte einlesen der Order-Input-Datei - Datensatz Nr. " + orderRecords.size());
		    	}
		    	
		    	headerLine=false;
		    	
		    }    
		    System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Einlesen der Order-Input-Datei abgeschlossen: " + orderRecords.size() + " Datensätze gelesen. verworfene Datensätze ohne Kunde: " + ordersWithoutCustomerId);
		    br.close();
		    return orderRecords;
		    
		    
		} catch (FileNotFoundException e) {
			System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Fehler beim Einlesen der Order-Datei : Datei konnte nicht gefunden werden");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Fehler beim Einlesen der Order-Datei : IO Fehler");
			e.printStackTrace();
			return null;
		}
		
		
	}

	

}
