package map.navi.component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.NaviPlan;
import map.navi.Data.PushMessageNode;

import com.doubleservice.bigcitynavigation.R;
import com.radiusnetworks.ibeacon.IBeacon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PushMessageManager {

	private Activity activity;
	private ArrayList<IBeacon> scannedPushMessageIBeacons ;
	private AlertDialog dmAlert ; 
	private boolean dmDialogIsOpen = false;
	private String currentFileName,currentFileExtension;
	private int currentRssiMax ,maxRssiIndex;
	private HashMap<String,PushMessageNode> messageCollection;
	private String TAG = "PushMessageManager";
	private File docFolder;
	
	public PushMessageManager(Activity activity) {
		this(activity,new HashMap<String,PushMessageNode>());
	}
	
	public PushMessageManager(Activity activity,HashMap<String,PushMessageNode> messageCollection) {
		this.activity = activity;
		this.messageCollection = messageCollection;
		docFolder = new File(Environment.getExternalStorageDirectory(),"Double_Service");
		docFolder.mkdirs();
		Builder builder = new AlertDialog.Builder(this.activity);
		dmAlert  = builder.create();
	}
	
	
	public void setPushMessageCollection(HashMap<String,PushMessageNode> messageCollection) {
		this.messageCollection = messageCollection;
		loadPushMessageFoldFromRawToSD();
	}
	
	private void loadPushMessageFoldFromRawToSD() {
		if(messageCollection.size()>0) {
			ArrayList<PushMessageNode> pushNodes = new ArrayList<PushMessageNode>();
			pushNodes.addAll(this.messageCollection.values());
		//(ArrayList<PushMessageNode>) this.messageCollection.values();
			for (PushMessageNode node: pushNodes) {
				if(node.hasFile) {
					Log.i("MessageNode", "file = "+node.fileName+", extension = "+node.extension);
				
					copyFileByFileName(node.fileName,node.extension);
				}
			}
		}
	}
	
	public boolean copyFileByFileName(String fileName,String extension) {

		try {

			if (docFolder.canWrite()) {
				Log.i("copyFileByFileName", "docFolder.canWrite");

				AssetManager assetFiles = this.activity.getAssets();
			
				int resId = this.activity.getResources().getIdentifier(fileName, "raw",
						this.activity.getPackageName());
				InputStream src = this.activity.getResources().openRawResource(resId);
				OutputStream dst = new FileOutputStream(docFolder + "/"
						+ fileName +extension);
				
				// Copy the bits from instream to outstream
				byte[] buf = new byte[1024];
				int len;
				while ((len = src.read(buf)) > 0) {
					dst.write(buf, 0, len);
				}
				src.close();
				dst.close();
				// }
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public void setScannedPushMessageIBeacons(ArrayList<IBeacon> scannedPushMessageIBeacons) {
		this.scannedPushMessageIBeacons = scannedPushMessageIBeacons;
		if(scannedPushMessageIBeacons.size()>0) {
			
			MathProxy.iBeaconSortingRssiStrongToWeak(this.scannedPushMessageIBeacons);
			PushMessageNode node ;
			for(IBeacon iBeacon: this.scannedPushMessageIBeacons) {
				String ibeaconID = iBeacon.getMajor()+"_"+iBeacon.getMinor();
				node = messageCollection.get(iBeacon.getMajor()+"_"+iBeacon.getMinor());
				
				Log.i(TAG, ibeaconID+" "+iBeacon.getRssi());
				if(iBeacon.getRssi()>= node.openMessageRssiLimit) {
					if(node.hasFile) {
						this.showMessageAndOpenFile(node.message, node.fileName, node.extension);
					}
					else
						this.showMessage(node.message);
				}
			}
			/*
			this.currentRssiMax = Integer.MIN_VALUE;
			IBeacon maxRssiIbeacon = scannedPushMessageIBeacons.get(0);
			for(IBeacon ibeacon:scannedPushMessageIBeacons) {
				if(ibeacon.getRssi()>currentRssiMax) {
					maxRssiIbeacon = ibeacon;
				}
			}
			PushMessageNode node = messageCollection.get(maxRssiIbeacon.getMajor()+"_"+maxRssiIbeacon.getMinor());
			if(maxRssiIbeacon.getRssi()>node.openMessageRssiLimit) {
				if(node.hasFile) {
					this.showMessageAndOpenFile(node.message);
					currentFileName = node.fileName;
					currentFileExtension = node.extension;
				}
				else
					this.showMessage(node.message);
			}
			*/
		}
	}
	
	public void showMessage(String msg) {
		if(!dmDialogIsOpen) {
			dmDialogIsOpen = true;
		Builder MyAlertDialog = new AlertDialog.Builder(this.activity);
		MyAlertDialog.setTitle("hotzmsg_title");
		MyAlertDialog.setMessage(msg);
		MyAlertDialog.setCancelable(false);
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dmDialogIsOpen = false;
			}
		};
		MyAlertDialog.setNeutralButton("btn_close", OkClick);
		MyAlertDialog.show();
		}
	}
	
	public void showMessageAndOpenFile(String msg,String fileName,String extension) {
		this.currentFileName = fileName;
		this.currentFileExtension = extension;
		this.showMessageAndOpenFile(msg);
	}

	private void showMessageAndOpenFile(String msg) {
		// TODO Auto-generated method stub

		// whether the dialog is opened or not
		if (!dmDialogIsOpen) {
			
			dmDialogIsOpen = true; // set the state to true means the dialog
									// opened

			dmAlert.setTitle("hotzmsg_title");
			dmAlert.setMessage(msg);
			dmAlert.setCancelable(false);
			dmAlert.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// isDMShow = true;
					dmDialogIsOpen = false;
					
					doOpenFile(currentFileName,currentFileExtension);
					
				}
			});
			dmAlert.setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// isDMShow = true;
					dmDialogIsOpen = false;
					dmAlert.dismiss();
				}
			});

			dmAlert.show();
		}
	}
	/*
	protected void doOpenDM(String fileName) {
		// TODO Auto-generated method stub
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Double_Service/" + fileName + ".pdf");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		this.activity.startActivity(intent);
	}
	*/
	protected void doOpenFile(String fileName, String ext) {
		// TODO Auto-generated method stub
		
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Double_Service/" + fileName+ext);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		// �v��
		if(  ext.equals(".mpg")
		  || ext.equals(".mp4")
		  )
		    intent.setDataAndType( Uri.fromFile(file), "video/*" );
		// ����
		else if( ext.equals(".mp3") )
		    intent.setDataAndType( Uri.fromFile(file), "audio/*" );
		// �v��
		else if( ext.equals(".bmp")
		      || ext.equals(".gif")
		      || ext.equals(".jpg")
		      || ext.equals("p.ng")
		  )
		    intent.setDataAndType( Uri.fromFile(file), "image/*" );
		// ��r��
		else if(ext.equals(".txt")
		      || ext.equals(".html")
		      )
		    intent.setDataAndType( Uri.fromFile(file), "text/*" );
		// Android APK
		else if( ext.equals(".apk")
		      )
		    intent.setDataAndType( Uri.fromFile(file), "application/vnd.android.package-archive" );
		else if( ext.equals(".pdf")	      )
			intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		// ��L
		else
		    intent.setDataAndType( Uri.fromFile(file), "application/*" );
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		this.activity.startActivity(intent);
	}
}
