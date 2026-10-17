# Expedition Browser ProGuard rules
-keep class com.expedition.browser.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-dontwarn androidx.webkit.**
