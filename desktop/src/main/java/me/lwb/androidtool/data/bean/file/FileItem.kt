package me.lwb.androidtool.data.bean.file

interface FileItem {
    val path:String
    val parent: FileItem?
    val name: String
    val isDirectory: Boolean
    val hasChildren: Boolean
    val fileSize: Long
}