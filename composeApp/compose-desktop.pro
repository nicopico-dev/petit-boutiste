# Documentation
# https://www.guardsquare.com/manual/configuration/usage
# https://www.guardsquare.com/manual/troubleshooting/troubleshooting

# Ensure our own code is not removed by shrinking
-keep,includedescriptorclasses class fr.nicopico.petitboutiste.** { *; }

# Jewel
-dontoptimize # required for DecoratedWindow
-dontwarn androidx.compose.desktop.DesktopTheme*
-keep class org.jetbrains.jewel.** { *; }
-dontwarn org.jetbrains.jewel.**
-keep class com.jetbrains.JBR* { *; }

# FileKit / JNA
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.* { *; }
# Keep FileKit Windows JNA descriptors (fix missing descriptor class note)
-keep class io.github.vinceglb.filekit.dialogs.platform.windows.jna.** { *; }

# Protobuf (desktop target)
# Using protobuf-java (not lite). Keep all protobuf types to avoid stripping reflective methods used by JsonFormat and descriptors.
-keep class com.google.protobuf.** { *; }

# Suppress optional platform integrations not present on desktop
-dontwarn com.google.appengine.**
-dontwarn com.google.apphosting.**
-dontwarn android.os.**
-dontwarn libcore.io.**
-dontwarn org.robolectric.**

# Kotlin scripting pulls in shaded IntelliJ/Guava/checkerframework/etc. via kotlin-compiler-embeddable
# Those classes are not needed at runtime for our app, so silence missing optional types
-dontwarn org.jetbrains.kotlin.com.google.**
-dontwarn org.jetbrains.kotlin.com.intellij.**
-dontwarn org.jetbrains.kotlin.io.opentelemetry.**
-dontwarn org.jetbrains.annotations.**
-dontwarn org.checkerframework.**
-dontwarn org.codehaus.mojo.**
-dontwarn org.jctools.**
-dontwarn javax.annotation.**
-dontwarn com.google.gson.**
-dontwarn com.google.protobuf.**
-dontwarn org.jetbrains.kotlin.scripting.**
-dontwarn org.jetbrains.kotlin.cli.**
-dontwarn org.jetbrains.kotlin.backend.**
-dontwarn org.jetbrains.kotlin.codegen.**
-dontwarn org.jetbrains.kotlin.descriptors.**
-dontwarn org.jetbrains.kotlin.resolve.**
-dontwarn org.jetbrains.kotlin.types.**
-dontwarn org.jetbrains.kotlin.javac.**
-dontwarn org.jetbrains.kotlin.org.jline.**
-dontwarn kotlin.annotations.jvm.**
-dontwarn org.mozilla.**
-dontwarn com.sun.tools.javac.**
-dontwarn javax.tools.**
