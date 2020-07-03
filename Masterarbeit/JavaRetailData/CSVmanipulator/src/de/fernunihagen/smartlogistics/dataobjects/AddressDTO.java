package de.fernunihagen.smartlogistics.dataobjects;

public class AddressDTO {
	private String Name;
	private String Amenity;
	private String longitude;
	private String latitude;
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getAmenity() {
		return Amenity;
	}
	public void setAmenity(String amenity) {
		Amenity = amenity;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public AddressDTO(String name, String amenity, String longitude, String latitude) {
		super();
		Name = name;
		Amenity = amenity;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	
}
