/*
 * AccountEnableService.java
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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;


/**
 * This service listens for account updates. Once the original authenticator has taken over it enables the workaround again.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AccountEnableService extends Service implements OnAccountsUpdateListener
{
	private final static String TAG = "org.dmfs.jb.workaround.carddavsync.AccountEnableService";


	@Override
	public void onCreate()
	{
		super.onCreate();
		AccountManager.get(this).addOnAccountsUpdatedListener(this, null, true);
	}


	@Override
	public IBinder onBind(Intent intent)
	{
		// no binding allowed
		return null;
	}


	@Override
	public void onAccountsUpdated(Account[] accounts)
	{
		AccountManager am = AccountManager.get(this);
		AuthenticatorDescription[] authenticators = am.getAuthenticatorTypes();
		String package_name = getString(R.string.package_name);

		// check all authenticators for the original package name
		for (AuthenticatorDescription authenticator : authenticators)
		{
			if (package_name.equals(authenticator.packageName))
			{
				// enable the workaround now that the original authenticator has taken over
				Log.v(TAG, "enable workaround authenticator");
				PackageManager pm = getPackageManager();
				pm.setComponentEnabledSetting(new ComponentName(this, AuthenticationService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);
			}
		}
	}

}
