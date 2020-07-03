package de.fernunihagen.smartlogistics.input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Stream;

import de.fernunihagen.smartlogistics.dataobjects.OrderDTO;

public class OutputWriter {
	public static void writeCSVfromOrderList(List<OrderDTO> orderRecords, String outputFilePath) {

		FileWriter csvWriter;
		try {
			csvWriter = new FileWriter(outputFilePath);
			// 1. Header schreiben
			// Country entfernt - ist irreführend für KI; später weitere Felder entfernt, da Cloud Runtime out of memory 
			//csvWriter.append("InvoiceNo;StockCode;Description;Quantity;InvoiceDate;UnitPrice;CustomerID;Country;orderedKepDl;latitude;longitude");
			csvWriter.append("InvoiceNo;StockCode;InvoiceDate;CustomerID;orderedKepDl;latitude;longitude");
			csvWriter.append("\n");

			// 2. alten header entfernen
			orderRecords.remove(0);
			
			// 3. je Order eine Zeile schreiben
			System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte Schreiben der Output Datei");
			int rowNr = 0;
			for (OrderDTO rowData : orderRecords) {
				rowNr++;
			    csvWriter.append(rowData.toString());
			    csvWriter.append("\n");
			    //System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Starte Schreiben der Output Datei - Datensatz Nr. " + rowNr);
			}
			System.out.println(Utils.getCurrentTime(System.currentTimeMillis()) + "Schreiben der Output Datei abgeschlossen - Anzahl Datensätze: " + rowNr);

			csvWriter.flush();
			csvWriter.close();
			
		} catch (IOException e) {
			System.out.println("Fehler beim Erzeugen der Output Datei");
			e.printStackTrace();
		}
		
	}
		
}
