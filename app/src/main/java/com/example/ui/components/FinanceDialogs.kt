package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GoalEntity
import com.example.data.model.TransactionType
import com.example.data.model.WalletType
import com.example.data.repository.WalletWithComputedBalance
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BrandBlueIce
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueVibrant
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import com.example.viewmodel.ScannedReceiptPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    wallets: List<WalletWithComputedBalance>,
    initialType: TransactionType = TransactionType.EXPENSE,
    onDismiss: () -> Unit,
    onConfirmIncome: (targetWalletId: Long, amount: Double, category: String, note: String) -> Unit,
    onConfirmExpense: (sourceWalletId: Long, amount: Double, category: String, note: String) -> Unit,
    onConfirmTransfer: (sourceWalletId: Long, targetWalletId: Long, amount: Double, fee: Double, note: String) -> Unit,
    onConfirmAdjustment: (walletId: Long, newBalance: Double, reason: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf("") }
    var feeText by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    var selectedSourceWalletId by remember {
        mutableStateOf(wallets.firstOrNull()?.wallet?.id ?: 1L)
    }
    var selectedTargetWalletId by remember {
        mutableStateOf(wallets.getOrNull(1)?.wallet?.id ?: wallets.firstOrNull()?.wallet?.id ?: 1L)
    }

    val expenseCategories = listOf(
        "Makanan & Minuman", "Transportasi & Bensin", "Belanja & Hiburan",
        "Tagihan & Utilitas", "Kesehatan & Obat", "Pendidikan", "Donasi & Zakat", "Lain-lain"
    )
    val incomeCategories = listOf(
        "Gaji / Payroll", "Bonus & Tunjangan", "Hasil Bisnis / Freelance",
        "Dividen & Imbal Hasil", "Hadiah / Cashback", "Penjualan Aset"
    )

    if (categoryText.isEmpty()) {
        categoryText = if (selectedType == TransactionType.INCOME) incomeCategories.first() else expenseCategories.first()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Catat Transaksi Ledger",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Type Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TransactionType.values().forEach { type ->
                        val isSelected = selectedType == type
                        val activeColor = when (type) {
                            TransactionType.INCOME -> AccentEmerald
                            TransactionType.EXPENSE -> AccentCoral
                            TransactionType.TRANSFER -> BrandBlueLight
                            TransactionType.ADJUSTMENT -> AccentAmber
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    selectedType = type
                                    categoryText = if (type == TransactionType.INCOME) incomeCategories.first() else expenseCategories.first()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (type) {
                                    TransactionType.INCOME -> "Income"
                                    TransactionType.EXPENSE -> "Expense"
                                    TransactionType.TRANSFER -> "Transfer"
                                    TransactionType.ADJUSTMENT -> "Audit"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) activeColor else TextSecondaryDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Double Entry Live Preview Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandBluePrimary.copy(alpha = 0.12f))
                        .border(1.dp, BrandBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = BrandBlueLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DOUBLE-ENTRY ENGINE SPECIFICATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlueLight
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val doubleEntryRule = when (selectedType) {
                            TransactionType.INCOME -> "DEBIT: Wallet Penerima (+Saldo) | CREDIT: Revenue (+Pendapatan)"
                            TransactionType.EXPENSE -> "DEBIT: Expense Category (+Beban) | CREDIT: Wallet Sumber (-Saldo)"
                            TransactionType.TRANSFER -> "DEBIT: Target Wallet (+Saldo) | CREDIT: Source Wallet (-Saldo)"
                            TransactionType.ADJUSTMENT -> "Audit Trail: Menyesuaikan selisih langsung pada ledger"
                        }
                        Text(
                            text = doubleEntryRule,
                            fontSize = 10.sp,
                            color = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = {
                        Text(if (selectedType == TransactionType.ADJUSTMENT) "Saldo Aktual Sebenarnya (Rp)" else "Jumlah Nominal (Rp)")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Wallet Selection
                if (selectedType == TransactionType.INCOME || selectedType == TransactionType.ADJUSTMENT) {
                    Text(
                        text = "Pilih Akun / Wallet:",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    WalletPickerDropdown(
                        wallets = wallets,
                        selectedId = selectedTargetWalletId,
                        onSelected = { selectedTargetWalletId = it }
                    )
                } else if (selectedType == TransactionType.EXPENSE) {
                    Text(
                        text = "Sumber Dana / Wallet:",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    WalletPickerDropdown(
                        wallets = wallets,
                        selectedId = selectedSourceWalletId,
                        onSelected = { selectedSourceWalletId = it }
                    )
                } else if (selectedType == TransactionType.TRANSFER) {
                    Text(
                        text = "Dari Rekening (Source):",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    WalletPickerDropdown(
                        wallets = wallets,
                        selectedId = selectedSourceWalletId,
                        onSelected = { selectedSourceWalletId = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ke Rekening (Tujuan):",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    WalletPickerDropdown(
                        wallets = wallets,
                        selectedId = selectedTargetWalletId,
                        onSelected = { selectedTargetWalletId = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = feeText,
                        onValueChange = { feeText = it },
                        label = { Text("Biaya Admin / Transfer Fee (Opsional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlueVibrant,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selection (for Income & Expense)
                if (selectedType == TransactionType.INCOME || selectedType == TransactionType.EXPENSE) {
                    Text(
                        text = "Kategori:",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val categories = if (selectedType == TransactionType.INCOME) incomeCategories else expenseCategories
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            val isSel = categoryText == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) BrandBluePrimary.copy(alpha = 0.2f) else DarkSurface)
                                    .border(1.dp, if (isSel) BrandBlueLight else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { categoryText = cat }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    color = if (isSel) BrandBlueLight else TextSecondaryDark
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = categoryText,
                        onValueChange = { categoryText = it },
                        label = { Text("Nama Kategori Lain") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlueVibrant,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Note Field
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Catatan / Memo Transaksi") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        val fee = feeText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            when (selectedType) {
                                TransactionType.INCOME -> {
                                    onConfirmIncome(selectedTargetWalletId, amount, categoryText, noteText)
                                }
                                TransactionType.EXPENSE -> {
                                    onConfirmExpense(selectedSourceWalletId, amount, categoryText, noteText)
                                }
                                TransactionType.TRANSFER -> {
                                    onConfirmTransfer(selectedSourceWalletId, selectedTargetWalletId, amount, fee, noteText)
                                }
                                TransactionType.ADJUSTMENT -> {
                                    onConfirmAdjustment(selectedTargetWalletId, amount, noteText.ifEmpty { "Koreksi Saldo Manual" })
                                }
                            }
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_transaction_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                ) {
                    Text(
                        text = "Simpan ke Double-Entry Ledger",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WalletPickerDropdown(
    wallets: List<WalletWithComputedBalance>,
    selectedId: Long,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedWallet = wallets.find { it.wallet.id == selectedId } ?: wallets.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedWallet?.wallet?.name ?: "Pilih Wallet",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark
            )
            Text(
                text = formatRupiah(selectedWallet?.balance ?: 0.0),
                fontSize = 13.sp,
                color = AccentEmerald
            )
        }
    }

    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            title = { Text("Pilih Akun / Wallet", color = TextPrimaryDark) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    wallets.forEach { w ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelected(w.wallet.id)
                                    expanded = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = w.wallet.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = w.wallet.type.displayName,
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }
                            Text(
                                text = formatRupiah(w.balance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { expanded = false }) {
                    Text("Batal", color = BrandBlueLight)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun AddWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: WalletType, initialBalance: Double, accountMasked: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(WalletType.BANK) }
    var initialBalanceText by remember { mutableStateOf("0") }
    var accountMasked by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tambah Akun / Wallet Baru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Akun (misal: Bank Mandiri, GoPay, Tabungan Emas)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tipe Wallet:",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(WalletType.BANK, WalletType.E_WALLET, WalletType.CASH).forEach { t ->
                        val isSel = selectedType == t
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) BrandBluePrimary.copy(alpha = 0.2f) else DarkSurface)
                                .border(1.dp, if (isSel) BrandBlueLight else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedType = t }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSel) BrandBlueLight else TextSecondaryDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = initialBalanceText,
                    onValueChange = { initialBalanceText = it },
                    label = { Text("Saldo Awal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = accountMasked,
                    onValueChange = { accountMasked = it },
                    label = { Text("Nomor Rekening Masked (Opsional, cth: •••• 1234)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    name,
                                    selectedType,
                                    initialBalanceText.toDoubleOrNull() ?: 0.0,
                                    accountMasked
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tambah Wallet", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ScanReceiptModal(
    scannedPreview: ScannedReceiptPreview?,
    wallets: List<WalletWithComputedBalance>,
    onDismiss: () -> Unit,
    onConfirmExpense: (walletId: Long) -> Unit
) {
    var selectedWalletId by remember {
        mutableStateOf(wallets.firstOrNull()?.wallet?.id ?: 1L)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = BrandBlueLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Smart Receipt OCR",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (scannedPreview != null) {
                    // Receipt visual card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .border(1.dp, AccentAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = scannedPreview.merchantName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "OCR Match 98%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentEmerald
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${scannedPreview.categorySuggested} • ${scannedPreview.dateDetected}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Item Terdeteksi:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondaryDark
                            )
                            scannedPreview.itemsDetected.forEach { item ->
                                Text(
                                    text = "• $item",
                                    fontSize = 12.sp,
                                    color = TextPrimaryDark
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Transaksi:",
                                    fontSize = 13.sp,
                                    color = TextSecondaryDark
                                )
                                Text(
                                    text = formatRupiah(scannedPreview.totalAmount),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AccentCoral
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bayar Menggunakan Wallet:",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    WalletPickerDropdown(
                        wallets = wallets,
                        selectedId = selectedWalletId,
                        onSelected = { selectedWalletId = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onConfirmExpense(selectedWalletId)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                    ) {
                        Text(
                            text = "Konfirmasi & Catat Pengeluaran",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
