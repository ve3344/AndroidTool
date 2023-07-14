package me.lwb.androidtool.common.services

import me.lwb.jsonrpc.RpcService

interface TaskService: RpcService {
    override val serviceName: String  get() = TaskService::class.java.simpleName
    fun tasks(): String
}