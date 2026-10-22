package com.example.snapflyerclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val Teal = Color(0xFF0F6B5C)
private val PageBg = Color(0xFFF5F7F6)
private data class Flyer(val store: String, val title: String, val validity: String, val deals: Int)
private data class Offer(val name: String, val store: String, val price: String, val category: String)
private val flyers = listOf(
    Flyer("Shop N Save", "Weekly Specials", "Sample validity dates", 42),
    Flyer("NewWorld IGA", "Fresh Deals", "Sample flyer", 36),
    Flyer("RB Patel", "Everyday Savings", "Sample flyer", 28),
    Flyer("Extra Supermarket", "Weekly Offers", "Sample flyer", 31)
)
private val offers = listOf(
    Offer("Pantry special", "Shop N Save", "FJ$3.45", "Pantry"),
    Offer("Family essentials", "NewWorld IGA", "FJ$5.99", "Household"),
    Offer("Fresh produce deal", "RB Patel", "FJ$2.99", "Fresh food"),
    Offer("Snack multipack", "Extra Supermarket", "FJ$4.50", "Snacks")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FlyerFinderApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlyerFinderApp() {
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var screen by remember { mutableStateOf("Home") }
    var query by remember { mutableStateOf("") }
    val groups = listOf(
        "Browse" to listOf("Home", "Active Flyers & Deals", "Offers", "Stores"),
        "My Shopping & Account" to listOf("Shopping list", "Loyalty cards", "Favourites", "Price Alerts", "Store notifications", "Notifications", "My Profile"),
        "Settings & Support" to listOf("General Settings & Theme", "Support")
    )
    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.fillMaxWidth().background(Teal).padding(20.dp)) {
                    Text("Flyer Finder", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Compare local specials", color = Color.White)
                }
                groups.forEach { (group, entries) ->
                    Text(group, Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp), fontWeight = FontWeight.Bold)
                    entries.forEach { entry ->
                        NavigationDrawerItem(
                            label = { Text(entry) },
                            selected = screen == entry,
                            onClick = { screen = entry; scope.launch { drawer.close() } },
                            icon = {
                                Icon(when (entry) {
                                    "Home" -> Icons.Default.Home
                                    "Offers", "Active Flyers & Deals" -> Icons.Default.LocalOffer
                                    "Stores" -> Icons.Default.Store
                                    "Favourites" -> Icons.Default.Favorite
                                    "Shopping list" -> Icons.Default.Checklist
                                    "Price Alerts", "Notifications", "Store notifications" -> Icons.Default.Notifications
                                    "Loyalty cards" -> Icons.Default.CreditCard
                                    else -> Icons.Default.Settings
                                }, contentDescription = null)
                            },
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = PageBg,
            topBar = {
                TopAppBar(
                    title = { Column {
                        Text("Flyer Finder", fontWeight = FontWeight.Bold)
                        Text(screen, fontSize = 12.sp, color = Color.Gray)
                    } },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawer.open() } }) { Icon(Icons.Default.Menu, "Menu") } },
                    actions = { IconButton(onClick = { screen = "Notifications" }) { Icon(Icons.Default.NotificationsNone, "Notifications") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBg)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    listOf("Home", "Offers", "Stores", "Favourites").forEach { entry ->
                        NavigationBarItem(
                            selected = screen == entry,
                            onClick = { screen = entry },
                            icon = { Icon(when (entry) {
                                "Home" -> Icons.Default.Home
                                "Offers" -> Icons.Default.LocalOffer
                                "Stores" -> Icons.Default.Store
                                else -> Icons.Default.Favorite
                            }, contentDescription = entry) },
                            label = { Text(entry, fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { pad ->
            when (screen) {
                "Home", "Active Flyers & Deals" -> HomeScreen(Modifier.padding(pad), query, { query = it }, { screen = it })
                "Offers" -> OffersScreen(Modifier.padding(pad), query)
                "Stores" -> StoresScreen(Modifier.padding(pad)) { screen = "Active Flyers & Deals" }
                else -> PlaceholderScreen(Modifier.padding(pad), screen)
            }
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier, query: String, onQuery: (String) -> Unit, open: (String) -> Unit) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(Modifier.fillMaxWidth().background(Teal, RoundedCornerShape(20.dp)).padding(18.dp)) {
                Text("Find the best local deals", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Browse flyers, explore offers and plan your shopping.", color = Color.White)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search products, stores, or flyers…") },
                    leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }
        }
        item { SectionTitle("Active Flyers & Deals", "View all") { open("Active Flyers & Deals") } }
        items(flyers.filter { it.store.contains(query, true) || it.title.contains(query, true) }) { FlyerCard(it) { open("Active Flyers & Deals") } }
        item { SectionTitle("Popular offers", "Compare") { open("Offers") } }
        items(offers.filter { it.name.contains(query, true) || it.store.contains(query, true) }) { OfferCard(it) }
    }
}

@Composable
private fun SectionTitle(title: String, action: String, click: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(action, color = Teal, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { click() })
    }
}

@Composable
private fun FlyerCard(flyer: Flyer, click: () -> Unit) {
    Card(onClick = click, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(76.dp, 94.dp).background(Color(0xFFE3F0EC), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Description, null, tint = Teal, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(flyer.store, color = Teal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(flyer.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(flyer.validity, color = Color.Gray, fontSize = 12.sp)
                Text("${flyer.deals} sample offers", color = Color.DarkGray, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
private fun OfferCard(offer: Offer) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(50.dp).background(Color(0xFFE3F0EC), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocalOffer, null, tint = Teal)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(offer.name, fontWeight = FontWeight.Bold)
                Text("${offer.store} • ${offer.category}", color = Color.Gray, fontSize = 12.sp)
            }
            Text(offer.price, color = Teal, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OffersScreen(modifier: Modifier, query: String) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Compare offers", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
        item { Text("Sample data only. Real retailer prices are not connected yet.", color = Color.Gray) }
        items(offers.filter { it.name.contains(query, true) || it.store.contains(query, true) }) { OfferCard(it) }
    }
}

@Composable
private fun StoresScreen(modifier: Modifier, open: () -> Unit) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Stores", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
        items(flyers.distinctBy { it.store }) { flyer ->
            Card(onClick = open, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, null, tint = Teal, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(flyer.store, fontWeight = FontWeight.Bold)
                        Text("${flyer.deals} sample offers", color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(modifier: Modifier, title: String) {
    Column(modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Construction, null, tint = Teal, modifier = Modifier.size(52.dp))
        Spacer(Modifier.height(16.dp))
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("This section is scaffolded for the next implementation step.", color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Text("Starter screen — not connected to a backend yet.", color = Teal)
    }
}
