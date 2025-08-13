package com.adprofitx.smartgrocer

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.yourcompany.smartgrocer.R // IMPORTANT: Make sure you have an icon in drawable
import com.yourcompany.smartgrocer.data.GroceryItem
import com.yourcompany.smartgrocer.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GroceryScreen(viewModel: GroceryViewModel) {
    val groceryList by viewModel.groceryList
    val totalAmount by viewModel.totalAmount
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartGrocer", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // This simulates adding an item. You can replace this with a barcode scanner call.
                    viewModel.addItem(
                        GroceryItem(
                            name = "New Scanned Item",
                            quantity = 1,
                            price = (1..10).random() + 0.99
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            // UPDATED: Show error messages to the user
            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (groceryList.isEmpty()) {
                // UPDATED: More engaging empty state
                EmptyCartView(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(groceryList, key = { it.id }) { item ->
                        GroceryListItem(
                            item = item,
                            onQuantityChange = { newQuantity -> viewModel.updateQuantity(item.id, newQuantity) },
                            onDelete = { viewModel.deleteItem(item.id) },
                            // NEW: Animate item placement
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(durationMillis = 300)
                            )
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
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // UPDATED: Card with a more modern feel
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    String.format("$%.2f each", item.price),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                    Icon(Icons.Default.RemoveCircleOutline, "Decrease Quantity")
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                    Icon(Icons.Default.AddCircleOutline, "Increase Quantity")
                }
            }
            Text(
                String.format("$%.2f", item.price * item.quantity),
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier.width(70.dp),
                textAlign = TextAlign.End
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete Item", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun TotalBillCard(total: Double) {
    // UPDATED: Total card with a gradient background for a professional look
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Bill:", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    String.format("$%.2f", total),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EmptyCartView(modifier: Modifier = Modifier) {
    // NEW: Attractive screen for when the cart is empty
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(
                // IMPORTANT: Add an icon named 'ic_shopping_cart_off' to your res/drawable folder
                // You can get one from Material Icons: https://fonts.google.com/icons
                painter = painterResource(id = R.drawable.ic_shopping_cart_off),
                contentDescription = "Empty Shopping Cart",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Your Cart is Empty",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Press the scan button below\nto add your first item!",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BannerAd() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
            AdView(context).apply {
                adSize = AdSize.BANNER
                // !! IMPORTANT !!
                // Replace this with your own Ad Unit ID from AdMob for production
                adUnitId = "ca-app-pub-3940256099942544/6300978111" // This is a TEST ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

