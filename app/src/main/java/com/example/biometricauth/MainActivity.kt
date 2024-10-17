package com.example.biometricauth


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometricauth.BiometricPromptManager.BiometricResult
import com.example.biometricauth.ui.theme.BiometricAuthTheme



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val promptManager = BiometricPromptManager(this)
        setContent {
            BiometricAuthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background.Marron) {
                    val biometricResult by promptManager.promptResult.collectAsState(
                        initial = null)

                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            println("Activity result $it")
                        }
                    )

                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricResult.AuthenticationNotSet){
                            if (Build.VERSION.SDK_INT >= 30){
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                    )
                                }
                                enrollLauncher.launch(enrollIntent)
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = {
                            promptManager.showBiometricPrompt(
                                tittle = "Simple prompt",
                                description = "Simple prompt description"
                            )
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEBC999)
                            )
                        ) {
                            Text(
                                text = "Authenticate",
                                fontSize = 20.sp,
                                color = Color(0xFFCD7700)
                            )
                        }

                        Spacer(modifier = Modifier.padding(15.dp))

                        biometricResult?.let { result ->
                            Text(
                                text = when(result){
                                    is BiometricResult.AuthenticationError -> {
                                        result.error
                                    }
                                    BiometricResult.AuthenticationFailed -> {
                                        "Authentication failed"
                                    }
                                    BiometricResult.AuthenticationNotSet -> {
                                        "Authentication not set"
                                    }
                                    BiometricResult.AuthenticationSuccess -> {
                                        "Authentication success"
                                    }
                                    BiometricResult.FeatureUnavailable -> {
                                        "Feature unavailable"
                                    }
                                    BiometricResult.HarwareUnavailable -> {
                                        "Harware unavailable"
                                    }
                                },
                                fontSize = 20.sp,
                                color = Color(0xFFCD7700)
                            )
                        }
                    }
                }
            }
        }
    }
}

private val Color.Marron: Color
    get() {
        return Color(0xFF4d3227)
    }

