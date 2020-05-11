package com.vmcplus.thermalprinter.printer

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*

class BTConnector(private val listener: ConnectionListener) {
    private var mSocket: BluetoothSocket? = null
    private var mOutputStream: OutputStream? = null
    private var mConnectTask: ConnectTask? = null
    var isConnecting = false
        private set

    val isConnected: Boolean
        get() = if (mSocket == null) false else true

    @Throws(ConnectionException::class)
    fun connect(device: BluetoothDevice?) {
        if (isConnecting && mConnectTask != null) {
            throw ConnectionException("Connection in progress")
        }
        if (mSocket != null) {
            throw ConnectionException("Socket already connected")
        }
        ConnectTask(device!!).also { mConnectTask = it }.execute()
    }

    @Throws(ConnectionException::class)
    fun disconnect() {
        if (mSocket == null) {
            throw ConnectionException("Socket is not connected")
        }
        try {
            mSocket!!.close()
            mSocket = null
            listener.onDisconnected()
        } catch (e: IOException) {
            throw ConnectionException(e.message ?: "")
        }
    }

    @Throws(ConnectionException::class)
    fun cancel() {
        if (isConnecting && mConnectTask != null) {
            mConnectTask!!.cancel(true)
        } else {
            throw ConnectionException("No connection is in progress")
        }
    }

    @Throws(ConnectionException::class)
    fun sendData(msg: ByteArray?) {
        if (mSocket == null) {
            throw ConnectionException("Socket is not connected, try to call connect() first")
        }
        try {
            mOutputStream!!.write(msg)
            mOutputStream!!.flush()
        } catch (e: Exception) {
            throw ConnectionException(e.message ?: "")
        }
    }

    inner class ConnectTask(var device: BluetoothDevice) : AsyncTask<URL?, Int?, Long>() {
        var error = ""

        override fun onCancelled() {
            isConnecting = false
            listener.onConnectionCancelled()
        }

        override fun onPreExecute() {
            listener.onStartConnecting()
            isConnecting = true
        }

        override fun doInBackground(vararg params: URL?): Long {
            var result: Long = 0
            try {
                mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
                mSocket!!.connect()
                mOutputStream = mSocket!!.outputStream
                result = 1
            } catch (e: IOException) {
                e.printStackTrace()
                error = e.message ?: ""
            }
            return result
        }

        override fun onProgressUpdate(vararg progress: Int?) {}

        override fun onPostExecute(result: Long) {
            isConnecting = false
            if (mSocket != null && result == 1L) {
                listener.onConnectionSuccess()
            } else {
                listener.onConnectionFailed("Connection failed $error")
            }
        }
    }

    companion object {
        private const val TAG = "P25"
        private const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    }

}