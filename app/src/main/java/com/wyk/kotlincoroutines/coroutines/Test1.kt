package com.wyk.kotlincoroutines.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//开启一个协程，并在协程中打印出当前线程名。

fun newCoroutines(){
    CoroutineScope(Dispatchers.IO).launch {
        println("IO- curr thread name: ${Thread.currentThread().name}")
    }
    CoroutineScope(Dispatchers.Default).launch {
        println("Default- curr thread name: ${Thread.currentThread().name}")
    }
    CoroutineScope(Dispatchers.Main).launch {
        println("Main- curr thread name: ${Thread.currentThread().name}")
    }
}
//通过协程下载一张网络图片并显示出来。



fun main(){
    newCoroutines()
}
