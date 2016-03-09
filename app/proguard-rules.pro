# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\hai\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn io.realm.processor.RealmProcessor
-dontwarn io.realm.processor.ClassMetaData
-dontwarn io.realm.processor.DefaultModuleGenerator
-dontwarn io.realm.processor.ModuleMetaData
-dontwarn io.realm.processor.RealmProxyClassGenerator
-dontwarn io.realm.processor.RealmProxyMediatorGenerator
-dontwarn io.realm.processor.RealmVersionChecker
-dontwarn io.realm.processor.Utils
-dontwarn io.realm.processor.javawriter.JavaWriter