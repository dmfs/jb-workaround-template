# Jelly Bean workaround template app

This is a template for a workaround app for [Android issue #34880](http://code.google.com/p/android/issues/detail?id=34880) in Jelly Bean.

## How to use

To implement your own workaround **first** *change the package name*! Otherwise your workaround can not be installed in parallel with other workaround apps that use this template (and the same package name).

Also you will have to change the following files:

* res/values/strings: you have to adjust almost all values according to your needs. Fill in the values from your original sync app.
* res/xml/account\_preferences.xml: That file should look like your original account\_preferences.xml (actually that shouldn't be necessary anymore)
* src/org/dmfs/jb/workaround/template/Authenticator.java: (you already changed the package name right?) This file should provide basic functionality. (I think this isn't necessary anymore) 
* res/drawable\*: insert the icons of your original sync app

That's it, I think.

This version of the workaround disables the fake-authenticator what it's started. This allows the real authenticator to take over. Once this has happened the fake-authenticator will be enabled again. So, in fact it should not be necessary to use custom icons, populate account\_preferences.xml and `addAccount()` in Authenticator.java but it won't hurt to do it.


## License

Copyright (c) Marten Gajda 2012-2013, licensed under Apache License Version 2.0. You're explicitly permitted to license your workaround app under GPL, but Include
