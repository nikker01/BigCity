package map.navi.component;


import java.util.ArrayList;

import com.doubleservice.bigcitynavigation.R;

import map.navi.Data.POI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class NavigationRouteSetting extends Activity{

	private String[] targetStart,targetEnd;
	private int startSelect = 0,endSelect = -1;
	private int preStartSelect = -1,preEndSelect = -1;
	private EditText editText_Start,editText_End;
	private boolean isTouchStartEditText = false;
	private Button confirm;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.navigation_route_setting);
		ArrayList<POI> poi = this.getIntent().getParcelableArrayListExtra("POI");
		//String mapName = this.getIntent().getStringExtra("mapName");
		this.setTitle(this.getResources().getString(R.string.action_bar_item_navigation_setting));
		
		editText_Start = (EditText) this.findViewById(R.id.edittext_start);
		editText_End = (EditText) this.findViewById(R.id.edittext_end);
		confirm = (Button)this.findViewById(R.id.button_set_route);
		
		editText_Start.setOnClickListener(this.clickListener);
		editText_End.setOnClickListener(this.clickListener);
		confirm.setOnClickListener(this.clickListener);
		
		targetStart = new String[poi.size()+1];
		targetEnd = new String[poi.size()];
		targetStart[0] = getResources().getString(R.string.layout_navigation_setting_current_locate);
		for(int index = 1;index<poi.size()+1;index++){
			targetStart[index] = poi.get(index-1).POIName;
		}
		for(int index = 0;index<poi.size();index++){
			targetEnd[index] = poi.get(index).POIName;
		}
	}
	
	 OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.edittext_start:
				isTouchStartEditText = true;
				listPOIDialog();
				break;
			case R.id.edittext_end:
				isTouchStartEditText = false;
				listPOIDialog();
				break;
			case R.id.button_set_route:
				backToNavigationActivity();
				break;
			}
			
		}
	};
	
	private void backToNavigationActivity() {
		if(endSelect!=-1) {
			Intent i = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("start", startSelect);
			bundle.putInt("end", endSelect);
			i.putExtras(bundle);
			setResult(this.RESULT_OK,i);
		}
		else {
			setResult(this.RESULT_CANCELED);
		}
		this.finish();
	}
	
	private void listPOIDialog() {
		
		final AlertDialog.Builder singlechoicedialog = new AlertDialog.Builder(this);
		
		String[] POIList ;
		if(isTouchStartEditText) {
			POIList = this.targetStart;
			singlechoicedialog.setTitle(getResources().getString(R.string.layout_navigation_setting_select_start));
		}
		else {
			POIList = this.targetEnd;
			singlechoicedialog.setTitle(getResources().getString(R.string.layout_navigation_setting_select_end));
		}
		singlechoicedialog.setSingleChoiceItems(POIList, -1,new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int item) {
			if(isTouchStartEditText) {
				startSelect = item;
			}
			else
				endSelect = item;
		 }
		});
		
		singlechoicedialog.setPositiveButton(getResources().getString(R.string.dialog_btn_confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(isTouchStartEditText) {
					editText_Start.setText(" "+targetStart[startSelect]);
					preStartSelect = startSelect;
				}
				else {
					editText_End.setText(" "+targetEnd[endSelect]);
					preEndSelect = endSelect;
					editText_End.setTextColor(Color.parseColor("#282828"));
				}
	    	}
		});
	    	        
		singlechoicedialog.setNegativeButton(getResources().getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(isTouchStartEditText) {
					startSelect = preStartSelect;
				}
				else
					endSelect = preEndSelect;
	    	   }
	    }); 
		
		AlertDialog alert_dialog = singlechoicedialog.create();
		
		alert_dialog.show();
	}
}
