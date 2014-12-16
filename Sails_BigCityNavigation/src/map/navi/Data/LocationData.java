package map.navi.Data;

public class LocationData {
	
	/* example:
	 * <title>¿«ªL¯ùÀ]</title>
		<number>4001</number>
		<area>BIG CITY</area>
		<floor>F4</floor>
		<coorX>90</coorX>
		<coorY>218</coorY>
	 */
	public String locationTitle;
	public String locationNumber;
	public String locationArea;
	public String locationFloor;
	public String locationPosX;
	public String locationPosY;
	
	
	
	public LocationData() {};
	
	public String dataLog() {
		return "title = "+locationTitle+", number = "+locationNumber+", area = "+locationArea+", floor = "+locationFloor+", x = "+locationPosX+", y = "+locationPosY;
	}
	
	
	public String getLocationArea() {
		return locationArea;
	}




	public void setLocationArea(String locationArea) {
		this.locationArea = locationArea;
	}




	public String getLocationTitle() {
		return locationTitle;
	}

	public void setLocationTitle(String locationTitle) {
		this.locationTitle = locationTitle;
	}


	public String getLocationNumber() {
		return locationNumber;
	}


	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}


	public String getLocationFloor() {
		return locationFloor;
	}


	public void setLocationFloor(String locationFloor) {
		this.locationFloor = locationFloor;
	}

	public String getLocationPosX() {
		return locationPosX;
	}

	public void setLocationPosX(String locationPosX) {
		this.locationPosX = locationPosX;
	}

	public String getLocationPosY() {
		return locationPosY;
	}

	public void setLocationPosY(String locationPosY) {
		this.locationPosY = locationPosY;
	}
	
	
	
}
