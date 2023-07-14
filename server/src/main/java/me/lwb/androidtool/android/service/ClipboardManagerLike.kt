/**
 * Copyright (c) 2010, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.lwb.androidtool.android.service

import android.content.ClipData
import android.os.Build
import me.lwb.androidtool.android.ServiceRegistry
import me.lwb.androidtool.utils.IObjectReflector
import me.lwb.androidtool.utils.method

class ClipboardManagerLike(private val packageName: String,val userId:Int) {
    private val mService = IClipboardLegacy(ServiceRegistry.clipboard)
    private val mServiceQ = IClipboardQ(ServiceRegistry.clipboard)
    
    
    fun getPrimaryClip(): ClipData? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            mService.getPrimaryClip(packageName)
        }else{
            mServiceQ.getPrimaryClip(packageName, userId)
        }
    }

    fun setPrimaryClip(clip: ClipData) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            mService.setPrimaryClip(clip,packageName)
        }else{
            mServiceQ.setPrimaryClip(clip,packageName, userId)
        }
    }

    fun clearPrimaryClip() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            mService.clearPrimaryClip(packageName)
        }else{
            mServiceQ.clearPrimaryClip(packageName, userId)
        }
    }

    class IClipboardLegacy(override val delegateObject: Any) : IObjectReflector {
        val getPrimaryClip by method<ClipData>(String::class.java)
        val setPrimaryClip by method<Any>(ClipData::class.java, String::class.java)
        val clearPrimaryClip by method<Any>(String::class.java)
    }

    class IClipboardQ(override val delegateObject: Any) : IObjectReflector {
        val getPrimaryClip by method<ClipData>(String::class.java, Int::class.java)
        val setPrimaryClip by method<Any>(ClipData::class.java, String::class.java, Int::class.java)
        val clearPrimaryClip by method<Any>(String::class.java, Int::class.java)
    }
}

