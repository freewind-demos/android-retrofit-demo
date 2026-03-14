package com.example.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// API 接口定义
interface GitHubApi {
    // 获取用户信息
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<User>

    // 获取用户列表（模拟）
    @GET("users")
    fun getUsers(): Call<List<User>>
}

// 用户数据模型
data class User(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val html_url: String,
    val name: String?,
    val company: String?,
    val blog: String?
)

class MainActivity : AppCompatActivity() {

    private lateinit var api: GitHubApi
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 Retrofit
        initRetrofit()

        textViewResult = findViewById(R.id.textViewResult)

        // GET 请求按钮
        findViewById<Button>(R.id.buttonGet).setOnClickListener {
            fetchUserData()
        }
    }

    private fun initRetrofit() {
        // 创建 OkHttpClient
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // 创建 Retrofit 实例
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")  // API 基础 URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()

        // 创建 API 接口实例
        api = retrofit.create(GitHubApi::class.java)
    }

    private fun fetchUserData() {
        textViewResult.text = "请求中..."

        // 创建请求调用
        val call = api.getUser("octocat")

        // 异步执行请求
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                if (response?.isSuccessful == true) {
                    val user = response.body()
                    user?.let {
                        val result = """
                            登录名: ${it.login}
                            ID: ${it.id}
                            名称: ${it.name ?: "无"}
                            公司: ${it.company ?: "无"}
                            博客: ${it.blog ?: "无"}
                            GitHub: ${it.html_url}
                        """.trimIndent()
                        textViewResult.text = result
                    }
                } else {
                    textViewResult.text = "请求失败: ${response?.code()}"
                }
            }

            override fun onFailure(call: Call<User>?, t: Throwable?) {
                textViewResult.text = "请求错误: ${t?.message}"
            }
        })
    }
}
