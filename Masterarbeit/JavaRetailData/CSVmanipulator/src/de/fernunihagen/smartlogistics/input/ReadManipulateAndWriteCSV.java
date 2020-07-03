package de.fernunihagen.smartlogistics.input;

import java.util.List;

import de.fernunihagen.smartlogistics.dataobjects.AddressDTO;
import de.fernunihagen.smartlogistics.dataobjects.OrderDTO;

public class ReadManipulateAndWriteCSV {

	public static void main(String[] args) {
		
		// Pfade deklarieren
		final String adressInputFilePath = "C:/Users/erikb/BitbucketRepository/masterstudium/Masterarbeit/JavaRetailData/CSVmanipulator/data/sampleCoordinatesInput.csv";
		final String orderInputFilePath = "C:/Users/erikb/BitbucketRepository/masterstudium/Masterarbeit/JavaRetailData/CSVmanipulator/data/sampleDataInput.csv";
		final String orderOutputFilePath = "C:/Users/erikb/BitbucketRepository/masterstudium/Masterarbeit/JavaRetailData/CSVmanipulator/data/sampleDataOutput-withoutCountry-oneKepPerInvoice.csv";
		
		// Adressen einlesen
		List<AddressDTO> addressRecords = null;		
		addressRecords = InputReader.readAddressRecords(adressInputFilePath);
		
		// Orders einlesen und KEP zuweisen
		List<OrderDTO> orderRecords = null;		
		orderRecords = InputReader.readAndManipulateOrderRecords(orderInputFilePath );
		
		// über alle kunden iterieren und Addressen zuweisen
		orderRecords = TestDataGenerator.setRandomAdressesInOrders(addressRecords, orderRecords);
		
		// Orders schreiben
		OutputWriter.writeCSVfromOrderList(orderRecords, orderOutputFilePath);

	}

}
