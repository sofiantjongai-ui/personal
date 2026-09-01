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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LedgerEntryEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.repository.WalletWithComputedBalance
import com.example.ui.components.LedgerEntryRowItem
import com.example.ui.components.TransactionRowItem
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueVibrant
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    ledgerEntries: List<LedgerEntryEntity>,
    wallets: List<WalletWithComputedBalance>,
    onAddTransactionClick: () -> Unit
) {
    var isLedgerMode by remember { mutableStateOf(false) }
    var selectedTypeFilter by remember { mutableStateOf<TransactionType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val walletsMap = remember(wallets) {
        wallets.associate { it.wallet.id to it.wallet.name }
    }

    val filteredTransactions = remember(transactions, selectedTypeFilter, searchQuery) {
        transactions.filter { tx ->
            val matchType = selectedTypeFilter == null || tx.type == selectedTypeFilter
            val matchQuery = searchQuery.isBlank() ||
                    tx.category.contains(searchQuery, ignoreCase = true) ||
                    tx.note.contains(searchQuery, ignoreCase = true)
            matchType && matchQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TRANSACTION ENGINE & LEDGER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlueLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Buku Transaksi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                }

                Button(
                    onClick = onAddTransactionClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // View Mode Switcher: User Friendly vs Double Entry Accounting View
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isLedgerMode) BrandBluePrimary.copy(alpha = 0.3f) else Color.Transparent)
                        .clickable { isLedgerMode = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tampilan Transaksi (${transactions.size})",
                        fontSize = 12.sp,
                        fontWeight = if (!isLedgerMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isLedgerMode) BrandBlueLight else TextSecondaryDark
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLedgerMode) AccentEmerald.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { isLedgerMode = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (isLedgerMode) AccentEmerald else TextSecondaryDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ledger Debit/Kredit (${ledgerEntries.size})",
                            fontSize = 12.sp,
                            fontWeight = if (isLedgerMode) FontWeight.Bold else FontWeight.Normal,
                            color = if (isLedgerMode) AccentEmerald else TextSecondaryDark
                        )
                    }
                }
            }
        }

        if (!isLedgerMode) {
            // Search & Filter
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari kategori, merchant, atau memo...", color = TextSecondaryDark, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlueVibrant,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    singleLine = true
                )
            }

            // Filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            label = "Semua",
                            isSelected = selectedTypeFilter == null,
                            onClick = { selectedTypeFilter = null }
                        )
                    }
                    items(TransactionType.values()) { type ->
                        FilterChip(
                            label = when (type) {
                                TransactionType.INCOME -> "Income"
                                TransactionType.EXPENSE -> "Expense"
                                TransactionType.TRANSFER -> "Transfer"
                                TransactionType.ADJUSTMENT -> "Audit"
                            },
                            isSelected = selectedTypeFilter == type,
                            onClick = { selectedTypeFilter = type }
                        )
                    }
                }
            }

            // List of transactions
            if (filteredTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada transaksi yang cocok.",
                            color = TextSecondaryDark,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredTransactions) { tx ->
                    TransactionRowItem(tx = tx, walletsMap = walletsMap)
                }
            }
        } else {
            // Raw Double Entry Ledger Mode
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentEmerald.copy(alpha = 0.1f))
                        .border(1.dp, AccentEmerald.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "AUDIT TRAIL VIEW: Menampilkan setiap record jurnal buku besar secara granular. DEBIT = Penambahan Aset/Beban, CREDIT = Pengurangan Aset/Pendapatan.",
                        fontSize = 11.sp,
                        color = TextPrimaryDark,
                        lineHeight = 16.sp
                    )
                }
            }

            items(ledgerEntries) { entry ->
                LedgerEntryRowItem(
                    entry = entry,
                    walletName = walletsMap[entry.walletId] ?: "Wallet #${entry.walletId}"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) BrandBluePrimary.copy(alpha = 0.25f) else DarkSurfaceElevated)
            .border(1.dp, if (isSelected) BrandBlueVibrant else DarkSurfaceBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) BrandBlueLight else TextSecondaryDark
        )
    }
}

