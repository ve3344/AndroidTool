package me.lwb.androidtool.data.bean.file

import me.lwb.androidtool.library.adb.FilePathUtils
import me.lwb.logger.Logger

data class AdbFileItem internal constructor(
    override val path: String,
    override val parent: FileItem?,
    override val name: String,
    override val isDirectory: Boolean,
    override val hasChildren: Boolean,
    override val fileSize: Long,
    ) : FileItem {

    companion object {
        //dr-xr-xr-x 294 root   root          0 1970-09-04 00:25 acct
        //[lrw-r--r--, 1, root, root, 21, 2009-01-01, 08:00, /sdcard, ->, /storage/self/primary]
        fun parse(parent: FileItem?, line: String): AdbFileItem? {
            if (line.isBlank() || line.startsWith("total ")) {
                return null
            }
            return runCatching {
                val propList = line.split("\\s+".toRegex())
                if (propList[1] == "?") {
                    return null
                }
                val name = propList[7]
                val fileSize = propList[7].toLongOrNull() ?: 0L

                val isDirectory = line[0] == 'd' || line[0] == 'l'

                val path = FilePathUtils.getChildPath(parent?.path, name)

                AdbFileItem(path, parent, name, isDirectory, isDirectory, fileSize)
            }.onFailure {
                Logger.w { "Parse '${line}' fail:$it " }
            }.getOrNull()

        }

    }
}





