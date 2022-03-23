# AndroidUtils
![GitHub](https://img.shields.io/github/license/Nek-12/AndroidUtils)
![GitHub last commit](https://img.shields.io/github/last-commit/Nek-12/AndroidUtils)
![Maintenance](https://img.shields.io/maintenance/yes/2022)
[![Downloads on Jitpack](https://jitpack.io/v/Nek-12/AndroidUtils/month.svg)](https://jitpack.io/#Nek-12/AndroidUtils.svg)

Latest version is  [![Jitpack Version](https://jitpack.io/v/Nek-12/AndroidUtils.svg)](https://jitpack.io/#Nek-12/AndroidUtils) 

Extensions available:
```kotlin
val utilsVersion = "<look up 👆🏻>"
implementation ("com.github.Nek-12.AndroidUtils:databinding-recyclerview:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:preferences-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:databinding-genericpagingadapter:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:databinding:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:core-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:android-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:safenavcontroller:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:coroutine-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:room:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:material-ktx:$utilsVersion")
implementation("com.github.Nek-12.AndroidUtils:viewbinding:$utilsVersion")
implementation("com.github.Nek-12.AndroidUtils:compose-ktx:$utilsVersion")
```  

### RecyclerView
For documentation on `databinding-recyclerview`, check out [this doc](docs/databinding-recyclerview.md)

### Room
For documentation on `room`, check out [this one](docs/room.md)

### ApiResult
ApiResult is included in the `coroutines-ktx` artifact.  
Read [this doc](docs/networking.md) for reference.

## Other components
Documentation on those is still TBD, however there is not much code in them, so you can check out sources or javadocs if you want more.

* `SafeNavController` - will give you a class to replace your NavController that you use with navigation library,
  because it has one huge flaw: The Dreaded "Destination Not Found" Exception. To avoid crashing your app at runtime,
  use `Fragment.findSafeNavController()` instead of `Fragment.findNavController()` and use provided methods just like
  you would use the usual controller.
* `databinding` - will give you a generic DataBindingFragment class implementation. Super useful if you use `recyclerview` or databinding already. Extend that     class and override your layout id. No need to null out binding, inflate anything, just initialize your fragment logic in the `onViewReady()`, using kotlin synthetics-styled syntax. (`viewbinding` argifact is the same for viewbinding, as you might have guessed)
* `preferences-ktx `- A sharedPreferences delegate that allows you to write one-liners for loading and saving data
 from/to your app's default SharedPreferences.
 Uses `SharedPreferences.Editor.apply()` that does operations on background thread and caches values.
 SharedPreferences is a Singleton object so you can easily get as many references as you want, if you use default shared preferences, don't worry about accessing   or creating properties.
 However, if you're using custom sharedPreferences argument, you must manage the lifecycle yourself.  
 example:
     ```
     var isFirstLaunch: Boolean = booleanPreference(KEY_FIRST_LAUNCH)
     if (isFirstLaunch) {
         //...
     }
     isFirstLaunch = false
    ```
* `core-ktx` - Will give you a Time class implementation that I used in one of my projects, because there is still no
  analogue on the internet. If you need to manipulate time values efficiently or store time in the database (supported
  by `room` extension `DatabaseConverters` class by the way), then use `Time`. This artifact has literally zero
  dependencies, and does not depend on any android parts, actually.
* `android-ktx` - Will give you multiple extension functions, and a WebClient - the WebViewClient that solves almost all
  problems of the usual Client + WebView pairing you may encounter, such as crashes and errors on unknown link types, no
  way to handle external links, security vulnerabilities, and the mess that Google made of WebViewClient callbacks (when
  same callback gets called 2, 3 or more times in a row. It is also not bound to the view lifecycle, so you can just
  attach and detach it and your data will be safe, even in ViewModel)
* `compose-ktx` will let you streamline your code by providing a new way to work with resources. Instead of writing
  ```kotlin
  stringResource(R.string.some_string) 
  ```  
  you can now write
  ```kotlin
  R.string.some_string.string() //plurals, drawables and AVDs supported too
  ```
  And as always, useful extensions included
* Other `***-ktx` artifacts will give you some useful extension functions like `collectOnLifecycle()` from
  coroutines-ktx that I used in examples above to simplify working with system APIs, coroutines, and other android
  components.

For more information and other examples see javadocs in the library code.
If you find something that is missing, feel free to tell me about it using Github issues.

* [This Medium Post](https://medium.com/@berryhuang/android-room-generic-dao-27cfc21a4912) inspired me to create a generic DAO implementation.
* [This Medium Article](https://medium.com/android-news/using-databinding-like-a-pro-to-write-generic-recyclerview-adapter-f94cb39b65c4) inspired me to create my `recyclerview` implementation
* Other extensions, tricks, classes and ideas were inspired by open-source community: Medium posts, StackOverflow answers, other libraries and so on. Thanks to everyone for such a valuable information!
* My job and freelance projects inspired multitude of extensions used here

## License
```
   Copyright 2022 Nikita Vaizin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```
