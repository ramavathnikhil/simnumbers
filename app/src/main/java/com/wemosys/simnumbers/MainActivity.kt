package com.wemosys.simnumbers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wemosys.simnumbers.ui.theme.SimNumbersTheme


class MainActivity : ComponentActivity() {
    private val READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAndSetSimNumbers()
    }

    private fun getAndSetSimNumbers() {
        val simNumbers = getSimNumbers()

        setContent {
            SimNumbersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(simNumbers)
                }
            }
        }
    }

    private fun getSimNumbers(): String {
        var simNumbers = "";
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_NUMBERS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val subscriptionManager =
                getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subsInfoList = subscriptionManager.activeSubscriptionInfoList
            var i = 1;
            Log.d("sim", "Current list = $subsInfoList")
            simNumbers = "Current list = $subsInfoList\n\n\n"
            for (subscriptionInfo in subsInfoList) {
                val subscriptionId = subscriptionInfo.subscriptionId
                val phoneNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    subscriptionManager.getPhoneNumber(subscriptionId) + " " + subscriptionInfo.iccId
                } else {
                    subscriptionInfo.number + " " + subscriptionInfo.iccId
                }
                val number = "sim $i Number is  $phoneNumber"
                simNumbers = simNumbers + number + "\n"
                Log.d("sim", number)
                i++
            }
        } else {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_NUMBERS
                ),
                READ_PHONE_STATE_PERMISSION_REQUEST_CODE
            )
        }
        return simNumbers
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAndSetSimNumbers()
            } else {
                // Permission denied, handle this case (e.g., show explanation, disable functionality, etc.)
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Sim numbers of this device are\n\n$name\n",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimNumbersTheme {
        Greeting("Android")
    }
}