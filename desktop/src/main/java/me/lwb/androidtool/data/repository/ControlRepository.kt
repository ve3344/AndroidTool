package me.lwb.androidtool.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.data.bean.ControlConfigBean

class ControlRepository() {

    suspend fun shell(command: String) = AdbManager.shell(command)

    suspend fun pull(remote: String, local: String) = AdbManager.pull(remote, local)

    suspend fun push(local: String, remote: String) = AdbManager.push(local, remote)


    suspend fun loadControlConfig(path: String): ControlConfigBean.Group {
        return withContext(Dispatchers.IO) {
            ControlConfigBean.loadGroup(path)
        }
    }


    suspend fun install(path: String): String = AdbManager.install(path)


}


