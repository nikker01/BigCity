package com.doubleservice.DataVO;

import java.io.Serializable;

public class NavDataVO implements Serializable{

	public int MethodID;
	public String ItemName = "";
	public String PositionX;
	public String PositionY;
	public String Number;
	public String Area;
	public String Floor;
	public String FriendTel = ""; // only used in API getFriendLocation
}
