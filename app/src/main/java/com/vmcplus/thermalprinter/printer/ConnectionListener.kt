package com.vmcplus.thermalprinter.printer


interface ConnectionListener {
    fun onStartConnecting()
    fun onConnectionCancelled()
    fun onConnectionSuccess()
    fun onConnectionFailed(error: String?)
    fun onDisconnected()
}