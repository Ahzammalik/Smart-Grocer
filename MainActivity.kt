package com.yourcompany.smartgrocer // Replace with your actual package name

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.MobileAds
import com.yourcompany.smartgrocer.ui.GroceryScreen
import com.yourcompany.smartgrocer.ui.theme.SmartGrocerTheme // Use your project's theme name
import com.yourcompany.smartgrocer.viewmodel.GroceryViewModel
import com.yourcompany.smartgrocer.viewmodel.GroceryViewModelFactory

class MainActivity : ComponentActivity() {

    // Lazily initialize ViewModel using a factory
    private val groceryViewModel: GroceryViewModel by viewModels {
        val sharedPreferences = getSharedPreferences("SmartGrocerPrefs", Context.MODE_PRIVATE)
        GroceryViewModelFactory(sharedPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}

        // TODO: Implement and initialize the App Open Ad Manager here

        setContent {
            SmartGrocerTheme { // Make sure this matches your theme name
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GroceryScreen(viewModel = groceryViewModel)
                }
            }
        }
    }
}
