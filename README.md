# android-retrofit-demo

## 简介

本 demo 展示 Android 中 Retrofit 的基本用法，演示如何发起网络请求获取 GitHub 用户信息。

## 基本原理

Retrofit 是 Square 公司开发的类型安全的 HTTP 客户端，用于 Android 和 Java。它将 REST API 转换为 Java 接口，使得网络请求变得简单和类型安全。

主要特点：
- 注解驱动的 API 定义
- 自动处理 JSON 序列化/反序列化
- 支持多种转换器（Gson、Jackson、Moshi 等）
- 协程支持
- 错误处理

## 启动和使用

### 环境要求
- Android Studio 3.0+
- JDK 1.8+
- Android SDK 28

### 安装和运行
1. 用 Android Studio 打开此项目
2. 连接 Android 设备或启动模拟器
3. 点击 Run 运行项目
4. 点击按钮发起网络请求

## 教程

### 什么是 Retrofit？

Retrofit 是一个 REST 客户端，用于 Android 和 Java。它使用注解来定义 HTTP 请求接口，将 HTTP API 转换为 Kotlin/Java 接口。

### 基本用法

**1. 定义 API 接口：**

```kotlin
interface GitHubApi {
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<User>
}
```

常用注解：
- @GET：GET 请求
- @POST：POST 请求
- @PUT：PUT 请求
- @DELETE：DELETE 请求
- @Path：URL 路径参数
- @Query：URL 查询参数
- @Body：请求体

**2. 创建 Retrofit 实例：**

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")  // 基础 URL
    .addConverterFactory(GsonConverterFactory.create())  // JSON 转换器
    .build()

val api = retrofit.create(GitHubApi::class.java)
```

**3. 发起请求：**

```kotlin
val call = api.getUser("octocat")

call.enqueue(object : Callback<User> {
    override fun onResponse(call: Call<User>?, response: Response<User>?) {
        if (response?.isSuccessful == true) {
            val user = response.body()
        }
    }

    override fun onFailure(call: Call<User>?, t: Throwable?) {
        // 处理错误
    }
})
```

### OkHttp 与 Retrofit

Retrofit 底层使用 OkHttp 进行网络请求。可以在 Retrofit 中配置 OkHttpClient：

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()

val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .build()
```

### 数据模型

使用 data class 定义数据模型：

```kotlin
data class User(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val name: String?,
    val company: String?
)
```

Gson 会自动将 JSON 转换为 data class。

### 注意事项

1. **网络权限**：需要在 AndroidManifest.xml 中添加 `<uses-permission android:name="android.permission.INTERNET"/>`
2. **主线程**：网络请求不能在主线程执行，Retrofit 的回调在子线程
3. **线程切换**：可以使用 RxJava 或协程在主线程更新 UI
4. **错误处理**：检查 response.isSuccessful() 和处理异常
5. **Https**：Retrofit 支持 HTTPS，无需额外配置
