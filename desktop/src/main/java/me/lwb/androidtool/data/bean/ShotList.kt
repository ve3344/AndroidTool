package me.lwb.androidtool.data.bean

/**
 * Created by ve3344 .
 */
class SnapShotList<T>(internal val delegate: List<T>) : List<T> by delegate


fun <T>List<T>.snapshot():List<T>{
    if (this is SnapShotList){
        return SnapShotList(this.delegate)
    }
    return SnapShotList(this)
}