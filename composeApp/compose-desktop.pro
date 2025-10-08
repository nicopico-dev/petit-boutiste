# Documentation
# https://www.guardsquare.com/manual/configuration/usage
# https://www.guardsquare.com/manual/troubleshooting/troubleshooting

# Ensure our own code is not removed by shrinking
-keep,includedescriptorclasses class fr.nicopico.petitboutiste.** { *; }

# Jewel
-dontoptimize # required for DecoratedWindow
-dontwarn androidx.compose.desktop.DesktopTheme*
-keep class ** extends org.jetbrains.jewel.ui.painter.PainterHint
-keep class com.jetbrains.JBR* { *; }

# FileKit / JNA
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.* { *; }
# Keep FileKit Windows JNA descriptors (fix missing descriptor class note)
-keep class io.github.vinceglb.filekit.dialogs.platform.windows.jna.** { *; }

# Protobuf (desktop target)
-assumevalues class com.google.protobuf.Android { static boolean ASSUME_ANDROID return false; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keep class com.google.protobuf.DescriptorProtos$** { *; }
-keep class com.google.protobuf.Descriptors$** { *; }
-keep class com.google.protobuf.DynamicMessage { *; }
-keep class com.google.protobuf.util.** { *; }   # for JsonFormat

# Suppress optional platform integrations not present on desktop
-dontwarn com.google.appengine.**
-dontwarn com.google.apphosting.**
-dontwarn android.os.**
-dontwarn libcore.io.**
-dontwarn org.robolectric.**
