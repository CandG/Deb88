package cz.brno.greld.DiscussWithUs;

public class Location {
	private double latitude;
	private double longitude;
	
	
	public Location(double latitude, double longitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	
	
}
