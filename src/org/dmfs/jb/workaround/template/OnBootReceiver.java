/*
 * OnBootReceiver.java
 *
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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * This receiver disables the workaround authenticator to let the original authenticator take over. Also it starts a service that re-enables the workaround once
 * that has happened.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class OnBootReceiver extends BroadcastReceiver
{
	private final static String TAG = "org.dmfs.jb.workaround.carddavsync.OnBootReceiver";

	/**
	 * The number of seconds to wait before we disable the workaround authenticator.
	 */
	private final long DELAY = 30; // seconds


	@Override
	public void onReceive(final Context context, Intent intent)
	{

		// start the service that re-enables the workaround
		Intent serviceIntent = new Intent(context, AccountEnableService.class);
		context.startService(serviceIntent);

		// Set an alarm in 30 seconds to disable the authenticator service. That seems to be necessary on some devices.
		Log.i(TAG, "setting alarm to disable authenticator");

		Intent alarmIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DELAY * 1000L, pendingAlarmIntent);
	}

}
