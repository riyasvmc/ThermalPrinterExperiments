package com.vmcplus.thermalprinter

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap
    private val mDeviceList = ArrayList<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.mrbigbean, BitmapFactory.Options().apply { inScaled = false })
        //imageView.setImageBitmap(bitmap)

        // Singleton.connect(this)
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            showToast("No Bluetooth Adapter")
        } else {
            if (!bluetoothAdapter.isEnabled) {
                showToast("BT disabled")
            } else {
                val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices)
                    updateDeviceList()
                }
            }
            mProgressDlg = ProgressDialog(this)
            mProgressDlg.setMessage("Scanning...")
            mProgressDlg.setCancelable(false)
            mProgressDlg.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    bluetoothAdapter.cancelDiscovery()
                })
            mConnectingDlg = ProgressDialog(this)
            mConnectingDlg.setMessage("Connecting...")
            mConnectingDlg.setCancelable(false)
            mConnector = P25Connector(object : P25ConnectionListener() {
                fun onStartConnecting() {
                    mConnectingDlg.show()
                }

                fun onConnectionSuccess() {
                    mConnectingDlg.dismiss()
                    showConnected()
                }

                fun onConnectionFailed(error: String?) {
                    mConnectingDlg.dismiss()
                }

                fun onConnectionCancelled() {
                    mConnectingDlg.dismiss()
                }

                fun onDisconnected() {
                    showDisonnected()
                }
            })

            //enable bluetooth
            mEnableBtn.setOnClickListener(View.OnClickListener {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 1000)
            })

            //connect/disconnect
            mConnectBtn.setOnClickListener(View.OnClickListener { connect() })
            btnPrint.setOnClickListener(View.OnClickListener { printMessage() })
            btnCancel.setOnClickListener(View.OnClickListener { finish() })
        }


        val filter = IntentFilter()

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)

        registerReceiver(mReceiver, filter)
        /*editTextNumber.setOnLongClickListener{
            GlobalScope.launch(Dispatchers.Main) {
                // print(getData())
                printCode(Integer.valueOf(editTextNumber.text.toString()))
                *//*while(true) { // this code is used to print data with a delay of 5 secs
                    print("Hello Riyas".toByteArray())
                    // print(getData())
                    delay(5000)
                }*//*
            }
            true
        }*/
    }

    private fun updateDeviceList() {
        val adapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, R.layout.simple_spinner_item, getArray(mDeviceList))
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        mDeviceSp.setAdapter(adapter)
        mDeviceSp.setSelection(0)
    }

    private fun getArray(data: ArrayList<BluetoothDevice>): Array<String?> {
        var list = arrayOfNulls<String>(0)

        if (data == null) return list

        val size = data.size
        list = arrayOfNulls(size)

        for (i in 0 until size) {
            list[i] = data[i].name
        }

        return list
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun printCode(code: Int) {
        when (code) {
            0 -> print("00 Index\n01 Daily routine\n02 Friday\n10 Concrete\n20 Family aadhaar\n30 ICICI Account\n55 Site Measurment CheckList\n61 Wood Work with Chain Saw".toByteArray())// measurement
            1 -> print("Daily Routine\n---------------\nWake Up 3:00 AM\nDrink Water\nCold Shower\nThahajudh\nBowExcercise\nStudyProgramming\nGoMasjid\nBreakfast\nZuhr\nAsr\nMagrib\nIsha\n10:00 Sleep\n".toByteArray())// daily routine
            2 -> print("Friday\n----------\nNail Cut with Intention\nRecite Kahf\nBath with Intention\nPerfume\nWhite Dress\nWatch\nWalk to Masjid".toByteArray())
            10 -> print("Concrete Checklist:\n------------------\nCement\nMetal\nMSand\nWater\nBengalees\nFood\nVibrator + wire (extra vibrator)\nElectricity\nMixerMachine or thoni\nBucket\nkathi\nDiesel\nSheet\nMuram\nKooni + spanner\nElectricalPiping\nHooks\n".toByteArray())// concrete
            20 -> print("Moideen V:\nKadeeja:\nRasheed:0\nRubeena\nRaseena\nRiyas\n".toByteArray()) // aadhaar
            30 -> print("ICICI Bank\nName: Riyas Valiyadan\nManjeri Branch\nA/c: 022201508775\nIFSC: ICIC0000222\n".toByteArray())// icici account
            55 -> print("Site Measurement : 55\n------------------\n30m meter\n5m meter\nNote\nPlan\nPen\nCalculator\nChecklist-56\n".toByteArray())// measurement
            61 -> print("Wood Work : 61\n------------------\nChain Saw with Blade\nPetrol with Oil\nSpanner size -\nScrew driver\nCoir\nSteel pieces for gap\nKnife\nLever\n".toByteArray())// measurement
        }
    }

    private suspend fun getData(): ByteArray{
        return withContext(Dispatchers.Default) { // Computation dispatcher is used
            RasterPrinting.decodeBitmap(bitmap)
        }
    }

    private fun print(data: ByteArray){
        val os = Singleton.outputStream
        try{
            os.write(PrinterCommands.INIT)
            os.write(PrinterCommands.ESC_ALIGN_LEFT)
            os.write(data)
            os.write(PrinterCommands.FEED_LINE)
            os.write(PrinterCommands.FEED_LINE)
            os.write(PrinterCommands.FEED_PAPER_AND_CUT)
        }catch (e: IOException){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_connect ->
                Singleton.connect(this)
        }
        return true
    }
}