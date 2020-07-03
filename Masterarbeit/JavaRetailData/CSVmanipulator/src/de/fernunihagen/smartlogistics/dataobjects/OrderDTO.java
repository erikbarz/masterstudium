package de.fernunihagen.smartlogistics.dataobjects;

public class OrderDTO {
	// Attribute aus Beispieldaten
	private String InvoiceNo;
	private String StockCode;
	private String Description;
	private String Quantity;
	private String InvoiceDate;
	private String UnitPrice;
	private String CustomerID;
	private String Country;
	// neues zusätzliches Attribut
	private String orderedKepDl;
	private String addressLatitude;
	private String addressLongitude;
	
	
	public String getInvoiceNo() {
		return InvoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		InvoiceNo = invoiceNo;
	}
	public String getStockCode() {
		return StockCode;
	}
	public void setStockCode(String stockCode) {
		StockCode = stockCode;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getQuantity() {
		return Quantity;
	}
	public void setQuantity(String quantity) {
		Quantity = quantity;
	}
	public String getInvoiceDate() {
		return InvoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		InvoiceDate = invoiceDate;
	}
	public String getUnitPrice() {
		return UnitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		UnitPrice = unitPrice;
	}
	public String getCustomerID() {
		return CustomerID;
	}
	public void setCustomerID(String customerID) {
		CustomerID = customerID;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public String getOrderedKepDl() {
		return orderedKepDl;
	}
	public void setOrderedKepDl(String orderedKepDl) {
		this.orderedKepDl = orderedKepDl;
	}
	public String getAddressLatitude() {
		return addressLatitude;
	}
	public void setAddressLatitude(String addressLatitude) {
		this.addressLatitude = addressLatitude;
	}
	public String getAddressLongitude() {
		return addressLongitude;
	}
	public void setAddressLongitude(String addressLongitude) {
		this.addressLongitude = addressLongitude;
	}
	public OrderDTO(String invoiceNo, String stockCode, String description, String quantity, String invoiceDate,
			String unitPrice, String customerID, String country, String orderedKepDl) {
		super();
		InvoiceNo = invoiceNo;
		StockCode = stockCode;
		Description = description;
		Quantity = quantity;
		InvoiceDate = invoiceDate;
		UnitPrice = unitPrice;
		CustomerID = customerID;
		Country = country;
		this.orderedKepDl = orderedKepDl;
	}
	
	public void setAddress(String latitude, String longitude) {
		setAddressLatitude(latitude);
		setAddressLongitude(longitude);
	}
	
	@Override	
	public String toString() {
		return InvoiceNo + ";" + StockCode + ";" + 
				//Description	+ ";" + 
				//Quantity + ";" + 
				InvoiceDate + ";" + 
				//UnitPrice + ";" + 
				CustomerID + ";" + 
				//Country + ";" + 
				orderedKepDl + ";" + addressLatitude + ";" + addressLongitude;
	}
	
	
	
}
