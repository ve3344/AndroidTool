package me.lwb.androidtool.impl

import android.app.ActivityManager
import android.content.Context
import com.google.auto.service.AutoService
import me.lwb.jsonrpc.RpcMethod
import me.lwb.jsonrpc.RpcService
import me.lwb.androidtool.android.FakeApp
import me.lwb.androidtool.common.services.TaskService

/**
 * Created by ve3344 .
 */
@AutoService(RpcService::class)
open class TaskServiceImpl : TaskService {
    @RpcMethod
    override fun tasks(): String {
        val am = FakeApp.application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return am.getRecentTasks(10000, 0).map {
            it.toString()
        }.toString()

    }
}