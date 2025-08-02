package com.yourcompany.smartgrocer.ui // Replace with your package name

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.yourcompany.smartgrocer.data.GroceryItem
import com.yourcompany.smartgrocer.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(viewModel: GroceryViewModel) {
    val groceryList by viewModel.groceryList
    val totalAmount by viewModel.totalAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartGrocer") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // This simulates a barcode scan for demonstration.
                    // TODO: Replace this with a call to launch a real camera scanner screen.
                    viewModel.addItem(GroceryItem(name = "Sample Item", quantity = 1, price = 2.49))
                }
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (groceryList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your cart is empty.\nPress the scan button to start.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(groceryList, key = { it.id }) { item ->
                        GroceryListItem(
                            item = item,
                            onQuantityChange = { newQuantity -> viewModel.updateQuantity(item.id, newQuantity) },
                            onDelete = { viewModel.deleteItem(item.id) }
                        )
                    }
                }
            }

            TotalBillCard(total = totalAmount)
            BannerAd()
        }
    }
}

@Composable
fun GroceryListItem(
    item: GroceryItem,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(String.format("$%.2f each", item.price), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                    Icon(Icons.Default.Remove, "Decrease Quantity")
                }
                Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                    Icon(Icons.Default.Add, "Increase Quantity")
                }
            }
            Text(String.format("$%.2f", item.price * item.quantity), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun TotalBillCard(total: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total Bill:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(String.format("$%.2f", total), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun BannerAd() {
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        factory = { context ->
            AdView(context).apply {
                adSize = AdSize.BANNER
                // Use your real Ad Unit ID for release
                adUnitId = "ca-app-pub-4313544696010088/3719457347"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
