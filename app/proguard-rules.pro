# ProGuard & R8 configuration rules for AppDialer

# Keep AppModel data models
-keep class com.github.yongjhih.appdialer.model.** { *; }

# Keep AppDialerTime Logger for release benchmark testing
-keep class com.github.yongjhih.appdialer.util.Logger { *; }