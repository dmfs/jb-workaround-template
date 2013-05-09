/*
 * Copyright (C) 2012 Marten Gajda <marten@dmfs.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.dmfs.jb.workaround.template;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * Ensures the fake authenticator is enabled when the device is shut down to be prepared for the next boot. That ensures we don't leave the authenticator in
 * disabled state.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class ShutdownReceiver extends BroadcastReceiver
{
	private final static String TAG = "org.dmfs.jb.workaround.template.ShutdownReceiver";


	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent serviceIntent = new Intent(context, WorkaroundDisableService.class);
		context.stopService(serviceIntent);

		/*
		 * Re-enable fake authenticator for next boot.
		 * 
		 * Note: there might be a race condition if the checker task is still running. It might disable the authenticator again.
		 * 
		 * TODO: take care of this issue
		 */
		PackageManager pm = context.getPackageManager();
		ComponentName authenticatorComponent = new ComponentName(context, AuthenticationService.class);
		Log.i(TAG, "re-enabling workaround authenticator");
		pm.setComponentEnabledSetting(authenticatorComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

	}

}
