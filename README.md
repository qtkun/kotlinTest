# kotlinTest
Jetpack和kotlin学习demo
## app主工程
包括kotlin语法学习，
viewModel，LiveData，LifeCycle，Room，协程，
原生跳转flutter页面以及数据交互
## flutter_module
简单的flutter页面，跳转到原生页面及数据交互
+ app
+ flutter_module

-optimizationpasses 5                       # 代码混淆的压缩比例，值介于0-7，默认5
-verbose                                    # 混淆时记录日志
-dontoptimize                               # 不优化输入的类文件
-dontshrink                                 # 关闭压缩
-dontpreverify                              # 关闭预校验(作用于Java平台，Android不需要，去掉可加快混淆)
-dontoptimize                               # 关闭代码优化
-dontobfuscate                              # 关闭混淆
-ignorewarnings                             # 忽略警告
-dontwarn com.squareup.okhttp.**            # 指定类不输出警告信息
-dontusemixedcaseclassnames                 # 混淆后类型都为小写
-dontskipnonpubliclibraryclasses            # 不跳过非公共的库的类
-dontskipnonpubliclibraryclassmembers       # 不跳过非公共的库的类
-printmapping mapping.txt                   # 生成原类名与混淆后类名的映射文件mapping.txt
-useuniqueclassmembernames                  # 把混淆类中的方法名也混淆
-allowaccessmodification                    # 优化时允许访问并修改有修饰符的类及类的成员
-renamesourcefileattribute SourceFile       # 将源码中有意义的类名转换成SourceFile，用于混淆具体崩溃代码
-keepattributes SourceFile,LineNumberTable  # 保留行号
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod # 避免混淆注解、内部类、泛型、匿名类
-optimizations !code/simplification/cast,!field/ ,!class/merging/   # 指定混淆时采用的算法

-keep                           # 防止类和类成员被移除或被混淆；
-keepnames                      # 防止类和类成员被混淆；
-keepclassmembers	            # 防止类成员被移除或被混淆；
-keepclassmembernames           # 防止类成员被混淆；
-keepclasseswithmembers         # 防止拥有该成员的类和类成员被移除或被混淆；
-keepclasseswithmembernames     # 防止拥有该成员的类和类成员被混淆；

# 不混淆某个类的类名，及类中的内容
-keep class cn.coderpig.myapp.example.Test { *; }

# 不混淆指定包名下的类名，不包括子包下的类名
-keep class cn.coderpig.myapp*

# 不混淆指定包名下的类名，及类里的内容
-keep class cn.coderpig.myapp* {*;}

# 不混淆指定包名下的类名，包括子包下的类名
-keep class cn.coderpig.myapp**

# 不混淆某个类的子类
-keep public class * extends cn.coderpig.myapp.base.BaseFragment

# 不混淆实现了某个接口的类
-keep class * implements cn.coderpig.myapp.dao.DaoImp

# 不混淆类名中包含了"entity"的类，及类中内容
-keep class **.*entity*.** {*;}

# 不混淆内部类中的所有public内容
-keep class cn.coderpig.myapp.widget.CustomView$OnClickInterface {
public *;
}

# 不混淆指定类的所有方法
-keep cn.coderpig.myapp.example.Test {
public <methods>;
}

# 不混淆指定类的所有字段
-keep cn.coderpig.myapp.example.Test {
public <fields>;
}

# 不混淆指定类的所有构造方法
-keep cn.coderpig.myapp.example.Test {
public <init>;
}

# 不混淆指定参数作为形参的方法
-keep cn.coderpig.myapp.example.Test {
public <methods>(java.lang.String);
}

# 不混淆类的特定方法
-keep cn.coderpig.myapp.example.Test {
public test(java.lang.String);
}

# 不混淆native方法
-keepclasseswithmembernames class * {
native <methods>;
}

# 不混淆枚举类
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

#不混淆资源类
-keepclassmembers class **.R$* {
public static <fields>;
}

# 不混淆自定义控件
-keep public class * entends android.view.View {
*** get*();
void set*(***);
public <init>;
}

# 不混淆实现了Serializable接口的类成员，此处只是演示，也可以直接 *;
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}

# 不混淆实现了parcelable接口的类成员
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

# 注意事项：
# 
# ① jni方法不可混淆，方法名需与native方法保持一致；
# ② 反射用到的类不混淆，否则反射可能出问题；
# ③ 四大组件、Application子类、Framework层下的类、自定义的View默认不会被混淆，无需另外配置；
# ④ WebView的JS调用接口方法不可混淆；
# ⑤ 注解相关的类不混淆；
# ⑥ GSON、Fastjson等解析的Bean数据类不可混淆；
# ⑦ 枚举enum类中的values和valuesof这两个方法不可混淆(反射调用)；
# ⑧ 继承Parceable和Serializable等可序列化的类不可混淆；
# ⑨ 第三方库或SDK，请参考第三方提供的混淆规则，没提供的话，建议第三方包全部不混淆；