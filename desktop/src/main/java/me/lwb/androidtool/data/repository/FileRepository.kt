package me.lwb.androidtool.data.repository

import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.data.bean.file.AdbFileItem
import me.lwb.androidtool.data.bean.file.FileItem

class FileRepository() {

    suspend fun shell(command: String) = AdbManager.shell(command)

    suspend fun pull(remote: String, local: String) = AdbManager.pull(remote, local)

    suspend fun push(local: String, remote: String) = AdbManager.push(local, remote)

    suspend fun install(path: String): String = AdbManager.install(path)

    suspend fun loadFileList(
        parent: FileItem?,
        path: String,
    ): List<FileItem> = shell("ls -l $path")
        .output
        .lineSequence()
        .mapNotNull { AdbFileItem.parse(parent, it) }
        .sortedWith(compareBy<AdbFileItem> { !it.isDirectory }.then(compareBy { it.name }))
        .toList()



}


