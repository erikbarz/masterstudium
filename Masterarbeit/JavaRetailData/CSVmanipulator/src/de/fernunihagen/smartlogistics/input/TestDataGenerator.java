package de.fernunihagen.smartlogistics.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.fernunihagen.smartlogistics.dataobjects.AddressDTO;
import de.fernunihagen.smartlogistics.dataobjects.CustomerDTO;
import de.fernunihagen.smartlogistics.dataobjects.OrderDTO;

public class TestDataGenerator {
	public static String getRandomKepDl() {
		String kepDl = "";
		
		// auswürfeln des KEP-DL
		Random wuerfel = new Random();
		int augenZahl;
		augenZahl = 1 + wuerfel.nextInt(100);
		
		// Aufteilung analog Geschäftszahl 2017/2018 https://cdn.statcdn.com/Infographic/images/normal/16963.jpeg
		if(augenZahl<=57) 					{kepDl = "DHL";}
		if(augenZahl>57 && augenZahl<=87) 	{kepDl = "Hermes";}
		if(augenZahl>87 && augenZahl<=94)	{kepDl = "DPD";}
		if(augenZahl>94 && augenZahl<=98) 	{kepDl = "UPS";}
		if(augenZahl>98) 					{kepDl = "GLS";}
		
		return kepDl;
	}

	public static List<OrderDTO> setRandomAdressesInOrders(List<AddressDTO> randomAddressRecords,	List<OrderDTO> orderRecords) {
				
		// über alle oders iterieren und kunden-Liste aufbauen -> dabei jedem Kunden in der Kunden-Liste eine Adresse zuweisen
		Map<Integer, CustomerDTO> customerRecords = new HashMap();
		int orderCount=0;
		int ordersWithoutAddress=0;
		System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte Aufbau der Kundenliste");
		for (OrderDTO orderDTO : orderRecords) {
			// ersten Lauf überspringen Aufgrund Beschreibung in CSV
			if(orderCount>0 && !customerRecords.containsKey(Integer.parseInt(orderDTO.getCustomerID()))) {
				if(customerRecords.size()<randomAddressRecords.size()) {
					// zufälliges Zuweisen von Adressen über die Adress-ID und die Kunden-ID
					customerRecords.put(Integer.parseInt(orderDTO.getCustomerID()), new CustomerDTO(orderDTO.getCustomerID(),randomAddressRecords.get(customerRecords.size()+1).getLatitude(),randomAddressRecords.get(customerRecords.size()+1).getLongitude()));
					//System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Kunde" + customerRecords.size()  +" angelegt");
				}else {
					//System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "zu wenige Adressen vorhanden, Orders ohne Adressen: " + ordersWithoutAddress);
					ordersWithoutAddress++;
				}
			}else{
				//System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Kunde ist bereits in der Kundenliste enthalten");
			}
			orderCount++;
		}		
		System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Aufbau der Kundenliste abgeschlossen. Kunden: " + customerRecords.size() + ". Orders ohne Adresse: " + ordersWithoutAddress);
		
		// über alle oders iterieren, und je Kunde die distinkte Adresse zuweisen 
		System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte Aufbau der Orderliste mit distinkten ADressen je Kunde");
		List<OrderDTO> resultOrderRecords=new ArrayList<OrderDTO>();
		int resultOrderCount=0;
		for (OrderDTO orderDTO : orderRecords) {
			if(resultOrderCount>0) {
				OrderDTO resultOrder = new OrderDTO(orderDTO.getInvoiceNo(), orderDTO.getStockCode(), orderDTO.getDescription(), orderDTO.getQuantity(), orderDTO.getInvoiceDate(), orderDTO.getUnitPrice(), orderDTO.getCustomerID(), orderDTO.getCountry(), orderDTO.getOrderedKepDl());
				CustomerDTO customer = customerRecords.get(Integer.parseInt(orderDTO.getCustomerID()));
				resultOrder.setAddress(customer.getAddressLatitude(), customer.getAddressLongitude());
				resultOrderRecords.add(resultOrder);
			}
			
			resultOrderCount++;
		}
		System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Aufbau der Orderliste mit distinkten Adressen je Kunde abgeschlossen");
		return resultOrderRecords;
	}
}
