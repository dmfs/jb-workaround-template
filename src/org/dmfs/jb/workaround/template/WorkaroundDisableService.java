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

import java.util.Timer;
import java.util.TimerTask;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;


/**
 * Takes care of disabling and enabling the workaround if necessary.
 * <p>
 * If the account is managed by the fake authenticator this service checks for the existence of the original sync app and disables the fake authenticator if
 * it's found. Once the original authenticator has taken over it enables the workaround again.
 * </p>
 * <p>
 * It's recommended to call this service from your sync adapter to ensure the app is activated and run on the next boot. Use something like this:
 * </p>
 * 
 * <pre>
 * try
 * {
 * 	// we call the workaround to activate it
 * 	Intent workaroundIntent = new Intent(&quot;org.dmfs.jb_workaround.INIT&quot;);
 * 	workaroundIntent.setPackage(mContext.getString(R.string.jb_bug_workaround_app_package));
 * 	mContext.startService(workaroundIntent);
 * }
 * catch (Exception e)
 * {
 * 	Log.w(TAG, &quot;could not init workaround app&quot;);
 * }
 * </pre>
 * 
 * This way it shouldn't be necessary anymore to open the app once.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class WorkaroundDisableService extends Service
{
	private final static String TAG = "org.dmfs.jb.workaround.template.WorkaroundDisableService";
	private final static Timer mTimer = new Timer();


	@Override
	public void onCreate()
	{
		super.onCreate();

		String packageName = getString(R.string.package_name);
		String accountType = getString(R.string.account_type);
		if (!packageName.equals(getPackageForAccount(WorkaroundDisableService.this, accountType)))
		{
			// the account is not managed by the real authenticator, we have to run the checker task
			mTimer.scheduleAtFixedRate(new mCheckerTask(), 2500, 2500);
		}
		else
		{
			// everything is in place, nothing to do
			Log.i(TAG, "not starting because everything is fine");
			stopSelf();
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// explicitly return START_STICKY
		return START_STICKY;
	}


	@Override
	public IBinder onBind(Intent intent)
	{
		// no binding allowed
		return null;
	}

	/**
	 * The checker task does all the work and checks the accounts and packages.
	 * 
	 * @author Marten Gajda <marten@dmfs.org>
	 */
	private class mCheckerTask extends TimerTask
	{
		public void run()
		{
			Log.i(TAG, "checking accounts");

			Context context = WorkaroundDisableService.this;
			String packageName = getString(R.string.package_name);
			String accountType = getString(R.string.account_type);

			try
			{
				context.getPackageManager().getApplicationInfo(packageName, 0);
			}
			catch (PackageManager.NameNotFoundException e)
			{
				// original package not found yet, nothing to do
				Log.i(TAG, "package " + packageName + " not present (yet).");
				return;
			}

			/*
			 * The original authenticator package has been found, disable the workaround authenticator of not already done.
			 */
			PackageManager pm = getPackageManager();
			ComponentName authenticatorComponent = new ComponentName(context, AuthenticationService.class);
			if (pm.getComponentEnabledSetting(authenticatorComponent) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
			{
				// the fake authenticator is not disabled yet, do that now to let the real authenticator take over
				Log.i(TAG, "disabling workaround authenticator");
				pm.setComponentEnabledSetting(new ComponentName(context, AuthenticationService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);

				// we can't do anything else here, give the account manager some time to take notice of the change
				return;
			}

			// fake authenticator is disabled now. All we can do is to wait for the original authenticator to take over
			if (packageName.equals(getPackageForAccount(WorkaroundDisableService.this, accountType)))
			{
				// re-enable the workaround now that the original authenticator has taken over
				Log.i(TAG, "re-enabling workaround authenticator");
				pm.setComponentEnabledSetting(authenticatorComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

				// stop this service, we're done
				mTimer.cancel();
				WorkaroundDisableService.this.stopSelf();
			}
		}
	}


	/**
	 * Return the package that contains the active authenticator for the given account type.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param accountType
	 *            The account type to check.
	 * @return The package name of the package or <code>null</code> if no package exists that manages the account.
	 */
	private static String getPackageForAccount(Context context, String accountType)
	{
		AccountManager am = AccountManager.get(context);
		AuthenticatorDescription[] authenticators = am.getAuthenticatorTypes();
		// check all authenticators for the given package name
		for (AuthenticatorDescription authenticator : authenticators)
		{
			if (authenticator.type.equals(accountType))
			{
				return authenticator.packageName;
			}
		}
		return null;
	}
}
