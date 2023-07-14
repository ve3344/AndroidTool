package me.lwb.androidtool.common.rpc

import java.io.BufferedReader
import java.io.BufferedWriter

fun BufferedWriter.writeStringPackage(packageData: String) {
    val toCharArray = packageData.toCharArray()
    for (c in toCharArray) {
        if (c == '\r' || c == '\n') {
            continue
        }
        write(c.code)
    }
    write('\n'.code)
    flush()
}

fun BufferedReader.readStringPackage(): String {
    while (true) {
        val line = readLine()
        if (line.isNullOrEmpty()) {
            continue
        }
        return line
    }
}

