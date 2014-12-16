package map.navi.ViewComponent;

import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;

import map.navi.component.NavigationRouteSetting;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class NavigationPopupView {
	
	private Navigation navigation;
	private PopupWindow popUpWindow;
	private Button button_PlanNavigation,button_StopNavigation,button_NavigationTarget,button_StartLocate,
				   button_name;
	//private LinearLayout linearlayout;
	private ImageView image;
	public final int viewNavigation = 0,viewFoodAndShop = 1 ,viewParking = 2,viewFriend = 3;
	private int currentMode ;
	private String foodAndShopName ="";
	//private LinearLayout group;
	public NavigationPopupView(Navigation navigation) {
		this.navigation = navigation;
		//LayoutInflater inflater = (LayoutInflater) this.navigation.getSystemService(navigation.LAYOUT_INFLATER_SERVICE);  
		//View view = inflater.inflate(R.layout.navigation_popup_window, null); 
		//this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
		//currentMode = viewFoodAndShop;
		//viewWithFoodAndShop ();
		viewWithNavigation();
	}
	
	private void viewWithNavigation () {
		LayoutInflater inflater = (LayoutInflater) this.navigation.getSystemService(navigation.LAYOUT_INFLATER_SERVICE);  
		View view = inflater.inflate(R.layout.navigation_popup_window, null); 
		if(this.popUpWindow==null) {
			this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
		}
		else {

			popUpWindow.dismiss();
			popUpWindow.setContentView(view);
			//popUpWindow.showAtLocation(this.navigation.findViewById(R.id.navigation_main_layout),Gravity.BOTTOM ,0,0);
		}
		//this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
        this.popUpWindow.setAnimationStyle(R.style.anim_popup_window);
        this.popUpWindow.setOutsideTouchable(true);
        this.popUpWindow.getContentView().setFocusable(true);
        this.popUpWindow.getContentView().setFocusableInTouchMode(true);
        
        this.button_StartLocate = (Button)view.findViewById(R.id.pupop_window_start_locate);
        this.button_StartLocate.setOnClickListener(listener);
        this.button_NavigationTarget = (Button)view.findViewById(R.id.pupop_window_navigation_target);
        this.button_NavigationTarget.setOnClickListener(listener);
        this.button_PlanNavigation = (Button)view.findViewById(R.id.pupop_window_plan_navigation);
        this.button_PlanNavigation.setOnClickListener(listener);
        this.button_StopNavigation = (Button)view.findViewById(R.id.pupop_window_stop_navigation);
        this.button_StopNavigation.setOnClickListener(listener);
        button_StopNavigation.setVisibility(button_StopNavigation.GONE);
		button_NavigationTarget.setVisibility(button_NavigationTarget.GONE);
	}
	
	private void viewWithFoodAndShop () {
		LayoutInflater inflater = (LayoutInflater) this.navigation.getSystemService(navigation.LAYOUT_INFLATER_SERVICE);  
		View view = inflater.inflate(R.layout.navigation_popup_window_food_and_shop, null);  
		
		if(this.popUpWindow==null) {
			this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
		}
		else {
			popUpWindow.dismiss();
			popUpWindow.setContentView(view);
			//popUpWindow.showAtLocation(this.navigation.findViewById(R.id.navigation_main_layout),Gravity.BOTTOM ,0,0);			
		}
        
		//this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
        this.popUpWindow.setAnimationStyle(R.style.anim_popup_window);
        this.popUpWindow.setOutsideTouchable(true);
        this.popUpWindow.getContentView().setFocusable(true);
        this.popUpWindow.getContentView().setFocusableInTouchMode(true);
        
        
        this.button_StartLocate = (Button)view.findViewById(R.id.pupop_window_start_locate);
        this.button_StartLocate.setOnClickListener(listener);
        this.button_NavigationTarget = (Button)view.findViewById(R.id.pupop_window_navigation_target);
        this.button_NavigationTarget.setOnClickListener(listener);
        this.button_PlanNavigation = (Button)view.findViewById(R.id.pupop_window_plan_navigation);
        this.button_PlanNavigation.setOnClickListener(listener);
        this.button_StopNavigation = (Button)view.findViewById(R.id.pupop_window_stop_navigation);
        this.button_StopNavigation.setOnClickListener(listener);
        this.button_name = (Button)view.findViewById(R.id.popup_window_food_and_shop_target_name);
        this.button_name.setText(foodAndShopName);
        this.image = (ImageView) view.findViewById(R.id.imageline1);
       // this.linearlayout = (LinearLayout) view.findViewById(R.id.linearLayout2);
        button_StopNavigation.setVisibility(button_StopNavigation.GONE);
		button_NavigationTarget.setVisibility(button_NavigationTarget.GONE);
	}
	
	private void viewWithFriend () {
		LayoutInflater inflater = (LayoutInflater) this.navigation.getSystemService(navigation.LAYOUT_INFLATER_SERVICE);  
		View view = inflater.inflate(R.layout.navigation_popup_window_friend, null);  
		
		if(this.popUpWindow==null) {
			this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
		}
		else {

			popUpWindow.dismiss();
			popUpWindow.setContentView(view);
			//popUpWindow.showAtLocation(this.navigation.findViewById(R.id.navigation_main_layout),Gravity.BOTTOM ,0,0);
		}
        
		this.popUpWindow.setAnimationStyle(R.style.anim_popup_window);
        this.popUpWindow.setOutsideTouchable(true);
        this.popUpWindow.getContentView().setFocusable(true);
        this.popUpWindow.getContentView().setFocusableInTouchMode(true);
        
        this.button_StartLocate = (Button)view.findViewById(R.id.pupop_window_start_locate);
        this.button_StartLocate.setOnClickListener(listener);
       
        this.button_PlanNavigation = (Button)view.findViewById(R.id.pupop_window_plan_navigation);
        this.button_PlanNavigation.setText(foodAndShopName);
	}
	
	public void closePopupWindow() {
		if(this.popUpWindow.isShowing()) {
			this.popUpWindow.dismiss();
		}
	}
	
	private void viewWithParking() {
		LayoutInflater inflater = (LayoutInflater) this.navigation.getSystemService(navigation.LAYOUT_INFLATER_SERVICE);  
		View view = inflater.inflate(R.layout.navigation_popup_window_parking, null); 
		if(this.popUpWindow==null) {
			this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
		}
		else {

			popUpWindow.dismiss();
			popUpWindow.setContentView(view);
			//popUpWindow.showAtLocation(this.navigation.findViewById(R.id.navigation_main_layout),Gravity.BOTTOM ,0,0);
		}
		//this.popUpWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false);
        this.popUpWindow.setAnimationStyle(R.style.anim_popup_window);
        this.popUpWindow.setOutsideTouchable(true);
        this.popUpWindow.getContentView().setFocusable(true);
        this.popUpWindow.getContentView().setFocusableInTouchMode(true);
        
        this.button_StartLocate = (Button)view.findViewById(R.id.pupop_window_start_locate);
        this.button_StartLocate.setOnClickListener(listener);
       
        this.button_PlanNavigation = (Button)view.findViewById(R.id.pupop_window_plan_navigation);
        this.button_PlanNavigation.setText(foodAndShopName);
	}
	
	public void changePopView(int id) {
		switch (id) {
		case viewNavigation :
			currentMode = viewNavigation;
			this.viewWithNavigation();
			break;
		case viewFoodAndShop :
			currentMode = viewFoodAndShop;
			this.viewWithFoodAndShop();
			break;
		case viewParking :
			currentMode = viewParking;
			this.viewWithParking();
			break;
		case viewFriend :
			currentMode = viewFriend;
			this.viewWithFriend();
			break;
		}
	}
	
	public void setFreeAndShopTaegetName(String name) {
		this.foodAndShopName = name;
		
	}
	
	public void onPanelTouchAction() {
		//if(popUpWindow!=null) {
			if(!this.popUpWindow.isShowing()) {
			this.popUpWindow.showAtLocation(this.navigation.findViewById(R.id.navigation_main_layout),Gravity.BOTTOM ,0,0);
			}
			else {
				this.popUpWindow.dismiss();
			}		
		//}
	}
	
	
	OnClickListener listener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
				
			case R.id.pupop_window_start_locate:
				navigation.isHandChangeMap = false;
				navigation.startLocate();
			break;
			case R.id.pupop_window_plan_navigation:
				//Toast.makeText(navigation, "2", Toast.LENGTH_SHORT).show();
				toPlanRoute();
				break;
			case R.id.pupop_window_stop_navigation:
				//Toast.makeText(navigation, "3", Toast.LENGTH_SHORT).show();
				closeNavigationMode();
				break;
			case R.id.pupop_window_navigation_target:
				//Toast.makeText(navigation, "1", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	
	
	
	public void closeNavigationMode() {
		if(this.popUpWindow.isShowing()) {
			this.popUpWindow.dismiss();
		}
		if(currentMode == viewNavigation) {
			button_PlanNavigation.setVisibility(button_PlanNavigation.VISIBLE);
			button_StopNavigation.setVisibility(button_StopNavigation.GONE);
			button_NavigationTarget.setVisibility(button_NavigationTarget.GONE);
		}
		else if(currentMode == viewFoodAndShop) {
			//linearlayout.setVisibility(linearlayout.VISIBLE);
			
			button_PlanNavigation.setVisibility(button_PlanNavigation.VISIBLE);
			button_name.setVisibility(button_name.VISIBLE);
			image.setVisibility(image.VISIBLE);
			button_StopNavigation.setVisibility(button_StopNavigation.GONE);
			button_NavigationTarget.setVisibility(button_NavigationTarget.GONE);
		}
		
		this.navigation.closeNavigation();
		this.onPanelTouchAction();
		//button_NavigationTarget.setText("");
	}
	
	public void changeToNavigationMode(String targetPOIName) {
		if(currentMode == viewNavigation) {
			button_PlanNavigation.setVisibility(button_PlanNavigation.GONE);
			button_StopNavigation.setVisibility(button_StopNavigation.VISIBLE);
			button_NavigationTarget.setVisibility(button_NavigationTarget.VISIBLE);
			button_StartLocate.setVisibility(button_StartLocate.VISIBLE);
			button_NavigationTarget.setText(targetPOIName);
			this.navigation.openNavigation();
		}
		else if(currentMode == viewFoodAndShop) {
			//linearlayout.setVisibility(linearlayout.INVISIBLE);
			this.popUpWindow.dismiss();
			
			button_PlanNavigation.setVisibility(button_PlanNavigation.GONE);
			image.setVisibility(image.GONE);
			button_StopNavigation.setVisibility(button_StopNavigation.VISIBLE);
			button_name.setVisibility(button_name.INVISIBLE);
			button_NavigationTarget.setVisibility(button_NavigationTarget.VISIBLE);
			button_StartLocate.setVisibility(button_StartLocate.VISIBLE);
			button_NavigationTarget.setText(targetPOIName);
			this.navigation.openNavigation();
		}
		
		this.onPanelTouchAction();
	}
	
	private void toPlanRoute() {
		if(currentMode == viewNavigation) {
		 Intent intent = new Intent();
         intent.setClass(this.navigation,NavigationRouteSetting.class);
         Bundle bundle = new Bundle();
         bundle.putParcelableArrayList("POI", this.navigation.plan.POICollection);//.putDouble("height",height );
         intent.putExtras(bundle);
         intent.putExtra("mapName", this.navigation.plan.currentMapName);
         this.navigation.startActivityForResult(intent, this.navigation.REQUSET_CODE_ROUTESETTING);//.startActivity(intent);
		}
		else if(currentMode == viewFoodAndShop) {
			this.navigation.naviToFoodAndShop();
			this.changeToNavigationMode(foodAndShopName);
		}
	}
	
}
