# Kotlin
-keepattributes *Annotation*, InnerClasses

-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.text.RegexOption

-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

# Crashlytics
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Throwable  # Keep custom exceptions.

# AndroidX
-keep class androidx.core.app.** { *; }
