package com.doubleservice.bigcitynavigation;

import android.content.DialogInterface;

public interface IGenericAlert {
	public abstract void PositiveMethod(DialogInterface dialog, int id);
	public abstract void NegativeMethod(DialogInterface dialog, int id);
}
