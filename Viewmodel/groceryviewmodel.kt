package com.adprofitx.smartgrocer

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

    val groceryList = mutableStateOf<List<GroceryItem>>(emptyList())
    val totalAmount = mutableStateOf(0.0)

    // NEW: A state to show a loading indicator or handle errors.
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)


    init {
        loadListFromMemory()
    }

    fun addItem(item: GroceryItem) {
        val currentList = groceryList.value.toMutableList()
        // Prevent adding duplicate items, instead, increase quantity
        val existingItem = currentList.find { it.name.equals(item.name, ignoreCase = true) }
        if (existingItem != null) {
            updateQuantity(existingItem.id, existingItem.quantity + 1)
        } else {
            currentList.add(item)
            updateList(currentList)
        }
    }

    fun updateQuantity(id: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            deleteItem(id)
        } else {
            val currentList = groceryList.value.map {
                if (it.id == id) it.copy(quantity = newQuantity) else it
            }
            updateList(currentList)
        }
    }

    fun deleteItem(id: String) {
        val currentList = groceryList.value.filterNot { it.id == id }
        updateList(currentList)
    }

    private fun updateList(newList: List<GroceryItem>) {
        groceryList.value = newList.sortedBy { it.name }
        calculateTotal()
        saveListToMemory()
    }

    private fun calculateTotal() {
        totalAmount.value = groceryList.value.sumOf { it.price * it.quantity }
    }

    private fun saveListToMemory() {
        try {
            val gson = Gson()
            val jsonList = gson.toJson(groceryList.value)
            sharedPreferences.edit().putString("grocery_list_v2", jsonList).apply()
        } catch (e: Exception) {
            // UPDATED: Handle potential errors during saving
            errorMessage.value = "Error: Could not save your list."
            Log.e("GroceryViewModel", "Failed to save list to SharedPreferences", e)
        }
    }

    private fun loadListFromMemory() {
        isLoading.value = true
        errorMessage.value = null
        try {
            val gson = Gson()
            val jsonList = sharedPreferences.getString("grocery_list_v2", null)
            if (jsonList != null) {
                val type = object : TypeToken<List<GroceryItem>>() {}.type
                val savedList: List<GroceryItem> = gson.fromJson(jsonList, type)
                updateList(savedList)
            } else {
                // If no list is saved, just initialize with an empty one.
                updateList(emptyList())
            }
        } catch (e: JsonSyntaxException) {
            // UPDATED: Handle corrupted data gracefully
            errorMessage.value = "Could not load previous list. Starting fresh."
            Log.e("GroceryViewModel", "Corrupted JSON data in SharedPreferences", e)
            updateList(emptyList()) // Start with a clean slate
            sharedPreferences.edit().remove("grocery_list_v2").apply() // Clear corrupted data
        } catch (e: Exception) {
            errorMessage.value = "An unexpected error occurred."
            Log.e("GroceryViewModel", "Failed to load list from SharedPreferences", e)
            updateList(emptyList())
        } finally {
            isLoading.value = false
        }
    }
}

// Factory to create the ViewModel (No changes needed here)
class GroceryViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
