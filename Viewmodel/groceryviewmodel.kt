package com.yourcompany.smartgrocer.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.yourcompany.smartgrocer.data.GroceryItem

class GroceryViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    // ... (other state variables are the same)
    
    // NEW: Placeholder for user state.
    // In a real app, this would be updated by your authentication logic.
    val isLoggedIn = mutableStateOf(false)


    init {
        // TODO: Here you would check the user's login state from Firebase
        // For example: if (FirebaseAuth.getInstance().currentUser != null) { ... }
        
        if (isLoggedIn.value) {
            loadListFromCloud()
        } else {
            loadListFromMemory()
        }
    }

    private fun saveList() {
        if (isLoggedIn.value) {
            saveListToCloud()
        } else {
            saveListToMemory()
        }
    }

    private fun updateList(newList: List<GroceryItem>) {
        groceryList.value = newList.sortedBy { it.name }
        calculateTotal()
        saveList() // UPDATED: Calls the new unified save function
    }
    
    // --- Cloud Functions (Placeholders) ---
    private fun loadListFromCloud() {
        // TODO: Implement data fetching from Firestore.
        // This would look something like:
        // firestore.collection("users").document(userId).get().addOnSuccessListener { ... }
        Log.d("ViewModel", "Placeholder: Loading data from cloud...")
        // For now, load from memory as a fallback.
        loadListFromMemory()
    }

    private fun saveListToCloud() {
         // TODO: Implement data saving to Firestore.
         // This would look something like:
         // val data = mapOf("groceryList" to groceryList.value)
         // firestore.collection("users").document(userId).set(data)
         Log.d("ViewModel", "Placeholder: Saving data to cloud...")
    }

    // ... (All other functions: addItem, updateQuantity, deleteItem, calculateTotal, saveListToMemory, loadListFromMemory remain the same)
// ...
}

// ... (ViewModelFactory is unchanged)
