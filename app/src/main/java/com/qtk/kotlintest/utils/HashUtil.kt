package com.qtk.kotlintest.utils

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

fun String.md5(upperCase: Boolean = true): String = hash("MD5", this, upperCase)
fun String.sha1(upperCase: Boolean = true): String = hash("SHA-1", this, upperCase)
fun String.sha256(upperCase: Boolean = true): String = hash("SHA-256", this, upperCase)

private fun hash(type: String, input: String, upperCase: Boolean = true): String {
    val bytes = MessageDigest.getInstance(type).digest(input.toByteArray())
    return bytes.asHex(upperCase)
}

fun ByteArray.asHex(upperCase: Boolean = true): String {
    val hexChars = if (upperCase) "0123456789ABCDEF" else "0123456789abcdef"
    val result = StringBuilder(size * 2)
    forEach {
        val octet = it.toInt()
        result.append(hexChars[octet shr 4 and 0x0f])
        result.append(hexChars[octet and 0x0f])
    }
    return result.toString()
}

fun File.md5(upperCase: Boolean = true): String {
    if (!this.exists()) return ""

    var fis: FileInputStream? = null
    try {
        fis = this.inputStream()
        val digest = MessageDigest.getInstance("MD5")

        val byteArray = ByteArray(1024 * 10)
        var readLength = 0
        while (fis.read(byteArray).also { readLength = it } != -1) {
            digest.update(byteArray, 0, readLength)
        }
        return digest.digest().asHex(upperCase)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fis?.close()
        } catch (e: Exception) {
        }
    }
    return ""
}