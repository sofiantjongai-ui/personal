package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.WalletWithComputedBalance
import com.example.ui.components.formatRupiah
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
fun ScanReceiptScreen(
    scannedReceipt: ScannedReceiptPreview?,
    wallets: List<WalletWithComputedBalance>,
    expenseCategories: List<String>,
    onScanFromUri: (Uri) -> Unit,
    onScanFromText: (String) -> Unit,
    onConfirmExpense: (walletId: Long, merchant: String, amount: Double, category: String, note: String) -> Unit,
    onClearReceipt: () -> Unit
) {
    val context = LocalContext.current

    // State for receipt details (editable)
    var editMerchant by remember(scannedReceipt?.merchantName) {
        mutableStateOf(scannedReceipt?.merchantName ?: "")
    }
    var editAmountStr by remember(scannedReceipt?.totalAmount) {
        mutableStateOf(scannedReceipt?.totalAmount?.toLong()?.toString() ?: "")
    }
    var editCategory by remember(scannedReceipt?.categorySuggested) {
        mutableStateOf(scannedReceipt?.categorySuggested ?: "Makanan & Minuman")
    }
    var selectedWalletId by remember(wallets) {
        mutableStateOf(wallets.firstOrNull()?.wallet?.id ?: 1L)
    }

    var manualTextReceipt by remember { mutableStateOf("") }
    var showManualTextInput by remember { mutableStateOf(false) }

    // Android Photo Picker launcher (Google Play compliant zero-permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { onScanFromUri(it) }
        }
    )

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
                    text = "AI SMART OCR SCANNER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlueLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Pindai Struk & Nota Pembelian",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Ekstraksi otomatis nama toko, item belanja, total tagihan dan sinkronisasi ke buku besar kas/bank.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
            }
        }

        // Action Trigger Cards
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
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandBluePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = BrandBlueLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Pilih Foto Struk / Nota",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("pick_receipt_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buka Galeri Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { showManualTextInput = !showManualTextInput },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkSurfaceBorder))
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, tint = BrandBlueLight)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Input / Tempel Teks", fontSize = 12.sp, color = BrandBlueLight)
                        }
                    }

                    // Preset Quick Sample OCR Scans
                    Text("Atau coba struk otomatis:", fontSize = 11.sp, color = TextTertiaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val samples = listOf(
                            "Indomaret" to "INDOMARET POINT\n1x Roti Tawar 18000\n1x Susu UHT 22000\nTOTAL RP 40.000",
                            "SPBU Pertamina" to "SPBU PERTAMINA 31.129\nPertamax Turbo 20L\nTOTAL RP 270.000",
                            "Starbucks" to "STARBUCKS RESERVE\n1x Caramel Macchiato 62000\n1x Croissant 26000\nTOTAL RP 88.000"
                        )
                        samples.forEach { (label, raw) ->
                            OutlinedButton(
                                onClick = { onScanFromText(raw) },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(label, fontSize = 10.sp, maxLines = 1, color = TextPrimaryDark)
                            }
                        }
                    }
                }
            }
        }

        // Manual Text Input Box (collapsible)
        if (showManualTextInput) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Tempel Teks Struk / Nota:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        OutlinedTextField(
                            value = manualTextReceipt,
                            onValueChange = { manualTextReceipt = it },
                            placeholder = { Text("Contoh:\nIndomaret Point\n1x Snack 15000\nTOTAL RP 15.000") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlueVibrant,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )
                        Button(
                            onClick = {
                                if (manualTextReceipt.isNotBlank()) {
                                    onScanFromText(manualTextReceipt)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Proses & Ekstraksi AI", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // SCAN RESULT PREVIEW & CONFIRMATION
        // ==========================================
        if (scannedReceipt != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AccentAmber.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Hasil Deteksi Struk",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AccentEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OCR Verified",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald
                                )
                            }
                        }

                        Divider(color = DarkSurfaceBorder, thickness = 0.5.dp)

                        // Editable Merchant
                        OutlinedTextField(
                            value = editMerchant,
                            onValueChange = { editMerchant = it },
                            label = { Text("Nama Merchant / Toko") },
                            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = BrandBlueLight) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlueVibrant,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        // Editable Amount
                        OutlinedTextField(
                            value = editAmountStr,
                            onValueChange = { editAmountStr = it },
                            label = { Text("Total Tagihan / Pengeluaran (Rp)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCoral,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        // Items list detected
                        if (scannedReceipt.itemsDetected.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurface)
                                    .padding(12.dp)
                            ) {
                                Text("Rincian Item Terdeteksi:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                scannedReceipt.itemsDetected.forEach { item ->
                                    Text("• $item", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                            }
                        }

                        // Category Selector
                        var categoryExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded }
                        ) {
                            OutlinedTextField(
                                value = editCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori Pengeluaran") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandBlueVibrant,
                                    unfocusedBorderColor = DarkSurfaceBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                expenseCategories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            editCategory = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Wallet Selector
                        Text("Dipotong Dari Akun / Dompet:", fontSize = 12.sp, color = TextSecondaryDark)
                        if (wallets.isEmpty()) {
                            Text("Belum ada dompet/bank. Harap buat dompet terlebih dahulu.", color = AccentCoral, fontSize = 11.sp)
                        } else {
                            var walletExpanded by remember { mutableStateOf(false) }
                            val currentWalletName = wallets.find { it.wallet.id == selectedWalletId }?.wallet?.name ?: wallets.first().wallet.name

                            ExposedDropdownMenuBox(
                                expanded = walletExpanded,
                                onExpandedChange = { walletExpanded = !walletExpanded }
                            ) {
                                OutlinedTextField(
                                    value = currentWalletName,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandBlueVibrant,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedTextColor = TextPrimaryDark,
                                        unfocusedTextColor = TextPrimaryDark
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = walletExpanded,
                                    onDismissRequest = { walletExpanded = false }
                                ) {
                                    wallets.forEach { w ->
                                        DropdownMenuItem(
                                            text = {
                                                Text("${w.wallet.name} (${formatRupiah(w.balance)})")
                                            },
                                            onClick = {
                                                selectedWalletId = w.wallet.id
                                                walletExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Confirm & Clear Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onClearReceipt,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Batal", color = TextSecondaryDark)
                            }

                            Button(
                                onClick = {
                                    val amountVal = editAmountStr.toDoubleOrNull() ?: scannedReceipt.totalAmount
                                    val merchantVal = if (editMerchant.isNotBlank()) editMerchant else scannedReceipt.merchantName
                                    onConfirmExpense(
                                        selectedWalletId,
                                        merchantVal,
                                        amountVal,
                                        editCategory,
                                        "Struk: $merchantVal (${scannedReceipt.itemsDetected.joinToString(", ")})"
                                    )
                                },
                                enabled = wallets.isNotEmpty(),
                                modifier = Modifier.weight(2f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan ke Ledger", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
