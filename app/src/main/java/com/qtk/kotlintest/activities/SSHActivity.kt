package com.qtk.kotlintest.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.jcraft.jsch.*
import com.qtk.kotlintest.App
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseActivity
import com.qtk.kotlintest.databinding.ActivitySshBinding
import com.qtk.kotlintest.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executors
import java.io.*


class SSHActivity: BaseActivity<ActivitySshBinding>(R.layout.activity_ssh) {
    companion object {
        private const val USER = "root"
        private const val HOST = "101.132.222.146"
        private const val DEFAULT_SSH_PORT = 22
    }

    private var key = ""
    private val selectKeyFile = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            App.instance.copyUriToExternalFilesDir(uri, "key.pem")?.let { path ->
                println(path)
                key = path
            }
        }
    }
    private var session: Session? = null
    private var channel: Channel? = null

    private var mBufferedReader: BufferedReader? = null
    private var mDataOutputStream: DataOutputStream? = null
    private var ips: InputStream? = null

    private var mLastLine: String? = null

    private val executor = Executors.newFixedThreadPool(8)
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.presenter = SSHPresenter()
        binding.command.doAfterTextChanged {
            val sr: Array<String> = it.toString().split("\r\n").toTypedArray()
            val s = sr[sr.size - 1]
            mLastLine = s
        }
        binding.command.setOnEditorActionListener { v, actionId, event ->
            if (binding.command.text.isNullOrEmpty()) return@setOnEditorActionListener false
            if (event == null || event.action != KeyEvent.ACTION_DOWN) {
                return@setOnEditorActionListener false
            }
            // get the last line of terminal
            val command: String? = getLastLine()

            command?.let {
                binding.command.AddLastInput(it)
                executeCommand(it)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //close streams
        try {
            mDataOutputStream?.flush()
            mDataOutputStream?.close()
            mBufferedReader?.close()
            channel?.disconnect()
            session?.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun executeCommand(command: String) {
        synchronized(this) {
            writeToOutput(command)
        }
    }

    private fun writeToOutput(command: String) {
        executor.execute {
            if (mDataOutputStream != null) {
                try {
                    mDataOutputStream!!.writeBytes("$command\r\n")
                    mDataOutputStream!!.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getLastLine(): String? {
        val index: Int = binding.command.text.toString().lastIndexOf("\n")
        if (index == -1) {
            return binding.command.text.toString().trim()
        }
        if (mLastLine == null) {
            toast("no text to process")
            return ""
        }
        val lastLine: String? = mLastLine?.replace(binding.command.prompt.trim(), "")
        Log.d(
            "qtk",
            "command is " + lastLine + ", prompt is  " + binding.command.prompt
        )
        return lastLine?.trim { it <= ' ' }?.substring(lastLine.indexOf("# ") + 2)
    }

    inner class SSHPresenter{
        fun connect() {
            executor.execute {
                try {
                    JSch.setLogger(MyLogger())
                    val jsch = JSch()
                    jsch.addIdentity(key)
                    session = jsch.getSession(USER, HOST, DEFAULT_SSH_PORT)
                    session?.let {
                        it.setConfig("StrictHostKeyChecking", "no")
                        it.connect(30000)
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                while (it.isConnected) {
                                    println("连接成功")
                                    break
                                }
                            }
                        }
                    }
                    if (mBufferedReader == null) {
                        session?.let {
                            channel = it.openChannel("shell")
                            channel?.let { cl ->
                                cl.connect()
                                cl.inputStream = null
//                                mBufferedReader = BufferedReader(InputStreamReader(cl.inputStream))
                                mDataOutputStream = DataOutputStream(cl.outputStream)
                                ips = cl.inputStream
                            }
                            executor.execute {
                                var line: String?
                                val tmp = ByteArray(1024)
                                try {
                                    do {
                                        while (ips?.available() ?: 0 > 0) {
                                            val i: Int? = ips?.read(tmp, 0, 1024)
                                            if (i != null) {
                                                line = String(tmp, 0, i)
                                                val result: String = line
                                                handler.post {
                                                    synchronized(binding.command) {
                                                        binding.command.prompt = result
                                                        binding.command.setText(binding.command.text.toString() + "\r\n" + result)
                                                        Log.d("qtk", "LINE : $result")
                                                    }
                                                }
                                            }
                                        }
                                        if (channel?.isClosed == true) {
                                            if (ips?.available() ?: 0 > 0) continue
                                            println("exit-status: " + channel?.exitStatus)
                                            break
                                        }
                                        try {
                                            Thread.sleep(1000)
                                        } catch (ee: java.lang.Exception) {
                                        }
                                        /*line = mBufferedReader?.readLine()
                                        if (line?.isNotEmpty() == true) {
                                            val result: String = line
                                            handler.post {
                                                synchronized(binding.command) {
                                                    binding.command.prompt = result
                                                    binding.command.setText(binding.command.text.toString() + "\r\n" + result + "\r\n")
                                                    Log.d("qtk", "LINE : $result")
                                                }
                                            }
                                        }*/
                                    } while (true)
                                }catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun getKey() {
            selectKeyFile.launch("*/*")
        }
    }

    inner class MyLogger : Logger {
        var name = Hashtable<Int, String>()

        init {
            name[Logger.DEBUG] = "DEBUG: "
            name[Logger.INFO] = "INFO: "
            name[Logger.WARN] = "WARN: "
            name[Logger.ERROR] = "ERROR: "
            name[Logger.FATAL] = "FATAL: "
        }

        override fun isEnabled(level: Int): Boolean {
            return true
        }

        override fun log(level: Int, message: String) {
            handler.post {
                synchronized(binding.command) {
                    binding.command.setText(binding.command.text.toString() + "\r" + message + "\r")
                }
            }
            System.err.print(name[level])
            System.err.println(message)
        }
    }
}