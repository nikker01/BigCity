package com.doubleservice.memorycontroller;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "Double_Service/";
		} else {
			return CommonUtil.getRootFilePath() + "Double_Service";
		}
	}
}
