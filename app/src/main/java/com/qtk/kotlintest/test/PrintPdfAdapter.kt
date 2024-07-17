package com.qtk.kotlintest.test

import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class PrintPdfAdapter(private val mFilePath: String) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal,
        callback: LayoutResultCallback,
        extras: Bundle
    ) {
        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }
        val info = PrintDocumentInfo.Builder(jobName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()
        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {
        var input: InputStream? = null
        var output: OutputStream? = null
        try {
            input = FileInputStream(mFilePath)
            output = FileOutputStream(destination.fileDescriptor)
            val buf = ByteArray(1024)
            var bytesRead: Int

            while ((input.read(buf).also { bytesRead = it }) > 0) {
                output.write(buf, 0, bytesRead)
            }
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                input?.close()
                output?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private val jobName: String
        get() {
            try {
                val filePaths =
                    mFilePath.split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                return filePaths[filePaths.size - 1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return System.currentTimeMillis().toString()
        }
}


