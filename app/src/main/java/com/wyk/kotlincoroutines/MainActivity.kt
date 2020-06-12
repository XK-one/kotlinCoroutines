package com.wyk.kotlincoroutines

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

/**
 *  https://kaixue.io/tag/kotlin-coroutines/
 *  练习题
 */
class MainActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var imageView1: ImageView
    lateinit var imageView2: ImageView
    lateinit var mLoading: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById<ImageView>(R.id.iv)
        imageView1 = findViewById<ImageView>(R.id.iv1)
        imageView2 = findViewById<ImageView>(R.id.iv2)
        mLoading = findViewById<TextView>(R.id.tv_loading_alert)
        //printThreadName()
        /*getImage(object: onCallShowImage{
            override fun onShowImage(bitmap: Bitmap) {
                runOnUiThread(object: Runnable{
                    override fun run() {
                        imageView.setImageBitmap(bitmap)
                    }
                })
            }
        })*/
        //getImageOfCoroutines()
        //coroutinesChapter2()
        coroutinesChapter3()
    }

    //使用协程实现一个网络请求：
    //
    //    等待时显示 Loading；
    //    请求成功或者出错让 Loading 消失；
    //    请求失败需要提示用户请求失败了;
    //    让你的协程写法上看上去像单线程。
    fun coroutinesChapter3(){
        CoroutineScope(Dispatchers.Main).launch {
            mLoading.visibility = View.VISIBLE
            val response = requestNetwork()
            mLoading.visibility = View.GONE
            if(!response.isSuccessful) Toast.makeText(applicationContext,"请求失败",Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun requestNetwork(): Response {
        return withContext(Dispatchers.IO){
            val beginTime = System.currentTimeMillis()
            //val url = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png"
            val url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1591940565876&di=05c28f0b48f93a876d3137725ee038a0&imgtype=0&src=http%3A%2F%2Fnews.winshang.com%2Fmember%2FFCK%2F2020%2F5%2F22%2F20205229320496936x.jpg"
            val client = OkHttpClient().newBuilder().build()
            val request = Request.Builder().url(url).build()
            val response =client.newCall(request).execute()
            delay(6000)
            response

        }
    }

//========================================================================================================================


    //使用协程下载一张图，并行进行两次切割
    //
    //    一次切成大小相同的 4 份，取其中的第一份
    //    一次切成大小相同的 9 份，取其中的最后一份
    //
    //得到结果后，将它们展示在两个 ImageView 上
    fun coroutinesChapter2(){
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = requestImg()

            val deferred1 =  async { Bitmap.createBitmap(bitmap, 0,0, bitmap.width / 2,bitmap.height / 2) }
            val deferred2 = async { Bitmap.createBitmap(bitmap, bitmap.width / 3 * 2, bitmap.height / 3 * 2, bitmap.width/3, bitmap.height/3) }

            imageView.setImageBitmap(bitmap)
            imageView1.setImageBitmap(deferred1.await())
            imageView2.setImageBitmap(deferred2.await())

            //串行
            //val bitmap1 = Bitmap.createBitmap(bitmap, 0,0, bitmap.width / 2,bitmap.height / 2)
            //val bitmap2 = Bitmap.createBitmap(bitmap, bitmap.width / 3 * 2, bitmap.height / 3 * 2, bitmap.width/3, bitmap.height/3)

            //imageView.setImageBitmap(bitmap)
            //imageView1.setImageBitmap(bitmap1)
            //imageView2.setImageBitmap(bitmap2)

        }
    }



//========================================================================================================================

    //通过协程下载一张网络图片并显示出来，协程的方式
    fun getImageOfCoroutines(){
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = requestImg()
            println("getImageOfCoroutines: Thread name = ${Thread.currentThread().name}")
            imageView.setImageBitmap(bitmap)
        }
    }

    private suspend fun requestImg(): Bitmap {
        return withContext(Dispatchers.IO){
            val beginTime = System.currentTimeMillis()
            //val url = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png"
            val url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1591940565876&di=05c28f0b48f93a876d3137725ee038a0&imgtype=0&src=http%3A%2F%2Fnews.winshang.com%2Fmember%2FFCK%2F2020%2F5%2F22%2F20205229320496936x.jpg"
            val client = OkHttpClient().newBuilder().build()
            val request = Request.Builder().url(url).build()
            val response =client.newCall(request).execute()
            val byteStream= response.body?.byteStream()
            val bitmap =  BitmapFactory.decodeStream(byteStream)
            println("withContext: Thread name = ${Thread.currentThread().name}")
            println("reponse: ${response.toString()}, time = ${System.currentTimeMillis() - beginTime}")
             bitmap

        }
    }

    //通过协程下载一张网络图片并显示出来,原来的方式
    fun getImage(callShow: onCallShowImage){
        val beginTime = System.currentTimeMillis()
        val url = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png";
        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {

            }
            override fun onResponse(call: Call, response: Response) {
                val byteStream= response.body?.byteStream()
                val bitmap =  BitmapFactory.decodeStream(byteStream)

                println("Thread name = ${Thread.currentThread().name}")
                println("reponse: ${response.toString()}, time = ${System.currentTimeMillis() - beginTime}")

                callShow.onShowImage(bitmap)
            }
        })
    }

//=======================================================================================================================

    //开启一个协程，并在协程中打印出当前线程名
    fun printThreadName(){
        CoroutineScope(Dispatchers.IO).launch {
            println("IO- curr thread name: ${Thread.currentThread().name}")
        }
        CoroutineScope(Dispatchers.Default).launch {
            println("Default- curr thread name: ${Thread.currentThread().name}")
        }
        CoroutineScope(Dispatchers.Main).launch {
            println("Main- curr thread name: ${Thread.currentThread().name}")
        }
        /** 输出：
         *   Default- curr thread name: DefaultDispatcher-worker-2
         *  IO- curr thread name: DefaultDispatcher-worker-1
         * Main- curr thread name: main
         */
    }


    fun testCoroutines(){
        CoroutineScope(Dispatchers.Main).launch {
            getImg()

        }
    }

    suspend fun getImg(){
        withContext(Dispatchers.IO){
            //...
        }
    }

    open interface onCallShowImage{
        fun onShowImage(bitmap: Bitmap)
    }

}
