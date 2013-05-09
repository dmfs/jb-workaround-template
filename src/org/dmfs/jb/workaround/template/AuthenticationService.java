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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * The fake authenticator service.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AuthenticationService extends Service
{
	private Authenticator mAuthenticator;


	@Override
	public void onCreate()
	{
		super.onCreate();
		mAuthenticator = new Authenticator(this);

		/*
		 * Actually we don't want the workaround authenticator, it's better to let the original authenticator do the job. Start a Service that ensures
		 * everything is in place.
		 */
		Intent serviceIntent = new Intent(this, WorkaroundDisableService.class);
		startService(serviceIntent);
	}


	@Override
	public IBinder onBind(Intent intent)
	{
		return mAuthenticator.getIBinder();
	}

}
