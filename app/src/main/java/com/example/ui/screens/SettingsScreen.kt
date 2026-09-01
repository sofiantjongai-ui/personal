package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetEntity
import com.example.data.model.WalletEntity
import com.example.data.model.WalletType
import com.example.data.pref.UserProfile
import com.example.data.repository.BudgetStatus
import com.example.data.repository.WalletWithComputedBalance
import com.example.ui.components.formatRupiah
import com.example.ui.components.getWalletIcon
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BrandBlueIce
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandBluePastel
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueVibrant
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark

enum class SettingsSubSection(val title: String) {
    PROFILE("Edit Profil"),
    WALLETS("Kelola Bank & Kas"),
    CATEGORIES("Kelola Kategori"),
    BUDGETS("Kelola Nominal Budget"),
    DANGER_ZONE("Kosongkan Data")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userProfile: UserProfile,
    wallets: List<WalletWithComputedBalance>,
    expenseCategories: List<String>,
    incomeCategories: List<String>,
    budgets: List<BudgetStatus>,
    onUpdateProfile: (name: String, email: String, phone: String, avatarId: Int, currencyCode: String, currencySymbol: String, isPinEnabled: Boolean, pinCode: String) -> Unit,
    onAddWalletClick: () -> Unit,
    onEditWallet: (WalletEntity) -> Unit,
    onDeleteWallet: (WalletEntity) -> Unit,
    onAdjustBalance: (walletId: Long, currentBalance: Double) -> Unit,
    onAddCategory: (category: String, isIncome: Boolean) -> Unit,
    onEditCategory: (oldCat: String, newCat: String, isIncome: Boolean) -> Unit,
    onDeleteCategory: (category: String, isIncome: Boolean) -> Unit,
    onUpdateBudgetLimit: (BudgetEntity, newLimit: Double, threshold: Int) -> Unit,
    onDeleteBudget: (BudgetEntity) -> Unit,
    onCreateBudgetForCategory: (category: String, limit: Double) -> Unit,
    onResetAllDatabaseData: () -> Unit
) {
    var selectedSection by remember { mutableStateOf(SettingsSubSection.PROFILE) }

    // Edit Profile form state
    var editName by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var editEmail by remember(userProfile.email) { mutableStateOf(userProfile.email) }
    var editPhone by remember(userProfile.phone) { mutableStateOf(userProfile.phone) }
    var editAvatarId by remember(userProfile.avatarId) { mutableIntStateOf(userProfile.avatarId) }
    var editCurrencyCode by remember(userProfile.currencyCode) { mutableStateOf(userProfile.currencyCode) }
    var editCurrencySymbol by remember(userProfile.currencySymbol) { mutableStateOf(userProfile.currencySymbol) }
    var editIsPinEnabled by remember(userProfile.isPinEnabled) { mutableStateOf(userProfile.isPinEnabled) }
    var editPinCode by remember(userProfile.pinCode) { mutableStateOf(userProfile.pinCode) }

    // Dialog States
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryDialogIsIncome by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }

    var categoryToEdit by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var editCategoryInput by remember { mutableStateOf("") }

    var walletToEdit by remember { mutableStateOf<WalletEntity?>(null) }
    var editWalletName by remember { mutableStateOf("") }
    var editWalletMasked by remember { mutableStateOf("") }
    var editWalletType by remember { mutableStateOf(WalletType.BANK) }

    var walletForAdjustment by remember { mutableStateOf<WalletWithComputedBalance?>(null) }
    var adjustmentBalanceInput by remember { mutableStateOf("") }

    var budgetToEdit by remember { mutableStateOf<BudgetEntity?>(null) }
    var editBudgetLimitInput by remember { mutableStateOf("") }
    var editBudgetThresholdInput by remember { mutableStateOf("80") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "PENGATURAN & KELOLA FINANSIAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlueLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Personal Hub & Config",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryDark
                )
            }
        }

        // Sub-section Tab Selector
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSection.ordinal,
                containerColor = DarkSurfaceElevated,
                contentColor = BrandBlueLight,
                edgePadding = 8.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSection.ordinal]),
                        color = BrandBlueVibrant,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
            ) {
                SettingsSubSection.values().forEach { section ->
                    val isSelected = selectedSection == section
                    Tab(
                        selected = isSelected,
                        onClick = { selectedSection = section },
                        text = {
                            Text(
                                text = section.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandBlueLight else TextSecondaryDark
                            )
                        }
                    )
                }
            }
        }

        // ==========================================
        // SECTION 1: EDIT PROFIL
        // ==========================================
        if (selectedSection == SettingsSubSection.PROFILE) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Profil Akun Pengguna",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )

                        // Avatar Picker
                        Text("Pilih Avatar & Inisial:", fontSize = 12.sp, color = TextSecondaryDark)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val avatarColors = listOf(BrandBluePrimary, AccentEmerald, AccentAmber, AccentCoral, Color(0xFF8B5CF6))
                            avatarColors.forEachIndexed { index, color ->
                                val isSelected = editAvatarId == index
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(color.copy(alpha = if (isSelected) 0.9f else 0.3f))
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) Color.White else color.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable { editAvatarId = index },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (editName.isNotEmpty()) editName.take(2).uppercase() else "AP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Name Field
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Nama Lengkap / Panggilan") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BrandBlueLight) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlueVibrant,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        // Email Field
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Alamat Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlueVibrant,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        // Phone Field
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Nomor Handphone / WhatsApp") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlueVibrant,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        // Currency Selector
                        Text("Mata Uang Utama:", fontSize = 12.sp, color = TextSecondaryDark)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("IDR" to "Rp", "USD" to "$", "EUR" to "€", "SGD" to "S$").forEach { (code, sym) ->
                                val isSelected = editCurrencyCode == code
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        editCurrencyCode = code
                                        editCurrencySymbol = sym
                                    },
                                    label = { Text("$code ($sym)") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandBluePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // PIN Security
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Kunci Keamanan PIN", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                                Text("Amankan data finansial dari akses lain", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Switch(
                                checked = editIsPinEnabled,
                                onCheckedChange = { editIsPinEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = BrandBlueLight, checkedTrackColor = BrandBluePrimary)
                            )
                        }

                        if (editIsPinEnabled) {
                            OutlinedTextField(
                                value = editPinCode,
                                onValueChange = { if (it.length <= 6) editPinCode = it },
                                label = { Text("Masukkan PIN 4-6 Digit") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentAmber) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentAmber,
                                    unfocusedBorderColor = DarkSurfaceBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                onUpdateProfile(
                                    editName, editEmail, editPhone, editAvatarId,
                                    editCurrencyCode, editCurrencySymbol, editIsPinEnabled, editPinCode
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Perubahan Profil", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 2: KELOLA BANK, KAS & E-WALLET
        // ==========================================
        if (selectedSection == SettingsSubSection.WALLETS) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Akun & Rekening (${wallets.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Button(
                        onClick = onAddWalletClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah Akun", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (wallets.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum Ada Akun Bank atau Kas", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("Tambah akun dompet / bank pertama Anda untuk mulai mencatat.", fontSize = 12.sp, color = TextSecondaryDark)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(onClick = onAddWalletClick) {
                                Text("Tambah Akun Sekarang")
                            }
                        }
                    }
                }
            } else {
                items(wallets) { walletWithBal ->
                    val w = walletWithBal.wallet
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(BrandBluePrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = getWalletIcon(w.type),
                                            contentDescription = w.name,
                                            tint = BrandBlueLight,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = w.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                        Text(
                                            text = "${w.type.displayName} ${if (w.accountNumberMasked.isNotEmpty()) "• ${w.accountNumberMasked}" else ""}",
                                            fontSize = 11.sp,
                                            color = TextSecondaryDark
                                        )
                                    }
                                }

                                Text(
                                    text = formatRupiah(walletWithBal.balance),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (walletWithBal.balance >= 0) TextPrimaryDark else AccentCoral
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = DarkSurfaceBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons: Edit, Koreksi Saldo, Hapus
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        walletForAdjustment = walletWithBal
                                        adjustmentBalanceInput = walletWithBal.balance.toLong().toString()
                                    }
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Koreksi Saldo", color = AccentAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = {
                                        walletToEdit = w
                                        editWalletName = w.name
                                        editWalletMasked = w.accountNumberMasked
                                        editWalletType = w.type
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = BrandBlueLight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Info", color = BrandBlueLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { onDeleteWallet(w) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = AccentCoral, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 3: KELOLA KATEGORI
        // ==========================================
        if (selectedSection == SettingsSubSection.CATEGORIES) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kategori Pengeluaran (Expense)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Button(
                        onClick = {
                            categoryDialogIsIncome = false
                            newCategoryInput = ""
                            showAddCategoryDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah", fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            items(expenseCategories) { cat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = BrandBlueLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = cat, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    categoryToEdit = cat to false
                                    editCategoryInput = cat
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandBlueLight, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onDeleteCategory(cat, false) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = AccentCoral, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kategori Pemasukan (Income)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Button(
                        onClick = {
                            categoryDialogIsIncome = true
                            newCategoryInput = ""
                            showAddCategoryDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah", fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            items(incomeCategories) { cat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = cat, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    categoryToEdit = cat to true
                                    editCategoryInput = cat
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandBlueLight, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onDeleteCategory(cat, true) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = AccentCoral, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 4: KELOLA NOMINAL & BUDGET
        // ==========================================
        if (selectedSection == SettingsSubSection.BUDGETS) {
            item {
                Text(
                    text = "Daftar Anggaran Bulanan Per Kategori",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }

            if (budgets.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.PieChart, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum Ada Batas Anggaran", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("Pilih kategori pengeluaran untuk menetapkan limit bulanan.", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                }
            } else {
                items(budgets) { budgetStatus ->
                    val b = budgetStatus.budget
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = b.category,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Limit: ${formatRupiah(b.monthlyLimit)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandBlueLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Peringatan Alert aktif di: ${b.alertThresholdPercent}% penggunaan",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        budgetToEdit = b
                                        editBudgetLimitInput = b.monthlyLimit.toLong().toString()
                                        editBudgetThresholdInput = b.alertThresholdPercent.toString()
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = BrandBlueLight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Limit Nominal", color = BrandBlueLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { onDeleteBudget(b) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = AccentCoral, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 5: DANGER ZONE - KOSONGKAN DATA
        // ==========================================
        if (selectedSection == SettingsSubSection.DANGER_ZONE) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AccentCoral.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = AccentCoral, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Zona Bersih & Kosongkan Data",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCoral
                            )
                        }

                        Text(
                            text = "Gunakan fitur ini jika Anda ingin mengosongkan semua data (hapus semua transaksi, mutasi ledger, dompet/rekening, anggaran, target & aset) agar aplikasi bersih dari data awal dan siap untuk diinput data Anda sendiri secara riil.",
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { showResetConfirmDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCoral),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kosongkan Semua Data Sekarang", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // ==========================================
    // DIALOGS
    // ==========================================

    // 1. Reset Confirm Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Kosongkan Semua Data?", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Semua transaksi, ledger, dompet, target, dan anggaran akan dihapus bersih. Aplikasi akan menjadi kosong 0 rupiah.",
                    color = TextSecondaryDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAllDatabaseData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCoral)
                ) {
                    Text("Ya, Kosongkan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // 2. Add Category Dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = {
                Text(
                    if (categoryDialogIsIncome) "Tambah Kategori Pemasukan" else "Tambah Kategori Pengeluaran",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = { newCategoryInput = it },
                    label = { Text("Nama Kategori") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            onAddCategory(newCategoryInput.trim(), categoryDialogIsIncome)
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                ) {
                    Text("Tambah", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // 3. Edit Category Dialog
    categoryToEdit?.let { (oldCat, isIncome) ->
        AlertDialog(
            onDismissRequest = { categoryToEdit = null },
            title = { Text("Edit Nama Kategori", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editCategoryInput,
                    onValueChange = { editCategoryInput = it },
                    label = { Text("Nama Kategori Baru") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editCategoryInput.isNotBlank()) {
                            onEditCategory(oldCat, editCategoryInput.trim(), isIncome)
                            categoryToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToEdit = null }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // 4. Edit Wallet Info Dialog
    walletToEdit?.let { w ->
        AlertDialog(
            onDismissRequest = { walletToEdit = null },
            title = { Text("Edit Informasi Akun", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editWalletName,
                        onValueChange = { editWalletName = it },
                        label = { Text("Nama Akun / Bank") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlueVibrant,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    OutlinedTextField(
                        value = editWalletMasked,
                        onValueChange = { editWalletMasked = it },
                        label = { Text("Nomor Rekening Masked (Contoh: •••• 8821)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlueVibrant,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Text("Jenis Akun:", fontSize = 12.sp, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(WalletType.CASH, WalletType.BANK, WalletType.E_WALLET, WalletType.CREDIT_CARD).forEach { type ->
                            val isSelected = editWalletType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { editWalletType = type },
                                label = { Text(type.displayName, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandBluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editWalletName.isNotBlank()) {
                            onEditWallet(
                                w.copy(
                                    name = editWalletName.trim(),
                                    accountNumberMasked = editWalletMasked.trim(),
                                    type = editWalletType
                                )
                            )
                            walletToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { walletToEdit = null }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // 5. Koreksi Saldo Langsung Dialog
    walletForAdjustment?.let { w ->
        AlertDialog(
            onDismissRequest = { walletForAdjustment = null },
            title = { Text("Koreksi / Set Nominal Saldo", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Akun: ${w.wallet.name}\nSaldo Tercatat: ${formatRupiah(w.balance)}",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    OutlinedTextField(
                        value = adjustmentBalanceInput,
                        onValueChange = { adjustmentBalanceInput = it },
                        label = { Text("Nominal Saldo Sebenarnya (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentAmber,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )
                    Text(
                        "Sistem akan otomatis membuat transaksi penyesuaian di Double-Entry Ledger untuk menjaga konsistensi.",
                        fontSize = 10.sp,
                        color = TextTertiaryDark
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newBal = adjustmentBalanceInput.toDoubleOrNull()
                        if (newBal != null) {
                            onAdjustBalance(w.wallet.id, newBal)
                            walletForAdjustment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
                ) {
                    Text("Terapkan Saldo Baru", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { walletForAdjustment = null }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // 6. Edit Budget Limit Dialog
    budgetToEdit?.let { b ->
        AlertDialog(
            onDismissRequest = { budgetToEdit = null },
            title = { Text("Edit Limit Anggaran: ${b.category}", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editBudgetLimitInput,
                        onValueChange = { editBudgetLimitInput = it },
                        label = { Text("Limit Nominal Bulanan (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlueVibrant,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )
                    OutlinedTextField(
                        value = editBudgetThresholdInput,
                        onValueChange = { editBudgetThresholdInput = it },
                        label = { Text("Batas Peringatan / Alert (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlueVibrant,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = editBudgetLimitInput.toDoubleOrNull()
                        val threshold = editBudgetThresholdInput.toIntOrNull() ?: 80
                        if (limit != null && limit > 0) {
                            onUpdateBudgetLimit(b, limit, threshold)
                            budgetToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { budgetToEdit = null }) {
                    Text("Batal", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
