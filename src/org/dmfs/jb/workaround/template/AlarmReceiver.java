package org.dmfs.jb.workaround.template;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * This receiver actually disables the workaround authenticator. It's called delayed to ensure the device is ready and the original authenticator can take over.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AlarmReceiver extends BroadcastReceiver
{
	private final static String TAG = "org.dmfs.jb.workaround.template.AlarmReceiver";


	@Override
	public void onReceive(Context context, Intent intent)
	{
		final PackageManager pm = context.getPackageManager();

		// disable workaround
		Log.i(TAG, "disabling workaround authenticator");
		pm.setComponentEnabledSetting(new ComponentName(context, AuthenticationService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			PackageManager.DONT_KILL_APP);
	}

}
