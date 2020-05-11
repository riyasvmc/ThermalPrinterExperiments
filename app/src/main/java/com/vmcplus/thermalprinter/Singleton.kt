package com.vmcplus.thermalprinter

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.OutputStream
import java.util.*

class Singleton: Application(){
    companion object {
        private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //private val bluetoothDevice = bluetoothAdapter?.getRemoteDevice("00:01:90:AE:CD:7B")
        private val bluetoothDevice = bluetoothAdapter?.getRemoteDevice("DC:0D:30:38:42:F9") // HOIN
        private val bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        lateinit var outputStream: OutputStream

        fun connect(activity: Activity){

            if (bluetoothAdapter == null){
                return
            }

            if (bluetoothAdapter.isEnabled){
                Thread(Runnable {
                    try {
                        bluetoothAdapter?.cancelDiscovery()
                        bluetoothSocket?.connect()
                        if (bluetoothSocket!!.isConnected) {
                            outputStream = bluetoothSocket!!.outputStream
                            outputStream.write(PrinterCommands.FEED_LINE)
                        }
                    }catch (e: Exception){
                        activity.runOnUiThread(Runnable {
                            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                        })
                        Log.d("Riyas", e.toString())
                    }
                }).start()
            }else{
                Toast.makeText(activity, "Enable Bluetooth!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(){
        super.onCreate()
    }
}