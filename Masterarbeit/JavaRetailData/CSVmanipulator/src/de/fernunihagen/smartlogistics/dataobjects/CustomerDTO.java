package de.fernunihagen.smartlogistics.dataobjects;

public class CustomerDTO {
	private String CustomerID;
	private String addressLatitude;
	private String addressLongitude;
	
	public String getCustomerID() {
		return CustomerID;
	}
	public void setCustomerID(String customerID) {
		CustomerID = customerID;
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
	
	public CustomerDTO(String customerID, String addressLatitude, String addressLongitude) {
		super();
		CustomerID = customerID;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
	}
	
	public CustomerDTO(String customerID) {
		super();
		CustomerID = customerID;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomerDTO customerToCompare = (CustomerDTO) o;
        try {
        	return Integer.parseInt(CustomerID) == Integer.parseInt(customerToCompare.CustomerID);
		} catch (Exception e) {
			System.out.println("Fehler beim Vergleich der Kunden-Objekte");
			return false;
		}
        
    }
	
}
