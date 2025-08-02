package com.yourcompany.smartgrocer.data // Replace with your actual package name

import java.util.UUID

data class GroceryItem(
    val id: String = UUID.randomUUID().toString(), // Truly unique ID
    var name: String,
    var quantity: Int,
    var price: Double
)```

---

### **File 4: `viewmodel/GroceryViewModel.kt`**

Create this file in the `viewmodel` package. This is the brain of your app.

```kotlin
package com.yourcompany.smartgrocer.viewmodel // Replace with your package name

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yourcompany.smartgrocer.data.GroceryItem

class GroceryViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    
    val groceryList = mutableStateOf<List<GroceryItem>>(emptyList())
    val totalAmount = mutableStateOf(0.0)

    init {
        loadListFromMemory()
    }

    fun addItem(item: GroceryItem) {
        val currentList = groceryList.value.toMutableList()
        currentList.add(item)
        updateList(currentList)
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
        val gson = Gson()
        val jsonList = gson.toJson(groceryList.value)
        sharedPreferences.edit().putString("last_grocery_list", jsonList).apply()
    }

    private fun loadListFromMemory() {
        val gson = Gson()
        val jsonList = sharedPreferences.getString("last_grocery_list", null)
        if (jsonList != null) {
            val type = object : TypeToken<List<GroceryItem>>() {}.type
            val savedList: List<GroceryItem> = gson.fromJson(jsonList, type)
            updateList(savedList)
        }
    }
}

// Factory to create the ViewModel
class GroceryViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
