package com.vmcplus.thermalprinter

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.OutputStream
import java.util.*

class Singleton: Application(){

    companion object {
        lateinit var context: Context
        private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        lateinit var outputStream: OutputStream

        fun connect(){
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled){
                try {
                    val bluetoothDevice = bluetoothAdapter?.getRemoteDevice("DC:0D:30:38:42:F9")
                    val bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                    )
                    bluetoothAdapter?.cancelDiscovery()
                    bluetoothSocket?.connect()
                    if (bluetoothSocket!!.isConnected) {
                        outputStream = bluetoothSocket!!.outputStream
                        Toast.makeText(context, "Printer Connected 👍", Toast.LENGTH_SHORT).show()
                        outputStream.write("\n".toByteArray())
                    }
                }catch (e: Exception){
                    Log.d("Riyas", e.toString())
                }
            }else{
                Toast.makeText(context, "Enable Bluetooth!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(){
        super.onCreate()
        context = baseContext
    }
}