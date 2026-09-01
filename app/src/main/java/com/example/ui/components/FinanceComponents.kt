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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiInsightEntity
import com.example.data.model.GoalEntity
import com.example.data.model.InvestmentHoldingEntity
import com.example.data.model.LedgerEntryEntity
import com.example.data.model.LedgerEntryType
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.WalletEntity
import com.example.data.model.WalletType
import com.example.data.repository.BudgetStatus
import com.example.data.repository.GoalStatus
import com.example.data.repository.HoldingWithPnl
import com.example.data.repository.NetWorthSummary
import com.example.data.repository.WalletWithComputedBalance
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BrandBlueFrosted
import com.example.ui.theme.BrandBlueIce
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandBluePastel
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueVibrant
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.CardGradientEnd
import com.example.ui.theme.CardGradientMiddle
import com.example.ui.theme.CardGradientStart
import com.example.ui.theme.CardTextDark
import com.example.ui.theme.ColorBcaBlue
import com.example.ui.theme.ColorCashGreen
import com.example.ui.theme.ColorCreditCardGold
import com.example.ui.theme.ColorDanaBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceBorderLight
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextBlueAccent
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount).replace("Rp", "Rp ").trim()
}

fun formatPercent(value: Double): String {
    return String.format(Locale.US, "%.1f%%", value)
}

fun getWalletIcon(type: WalletType): ImageVector {
    return when (type) {
        WalletType.CASH -> Icons.Default.Payments
        WalletType.BANK -> Icons.Default.AccountBalance
        WalletType.E_WALLET -> Icons.Default.AccountBalanceWallet
        WalletType.CREDIT_CARD -> Icons.Default.CreditCard
        WalletType.INVESTMENT -> Icons.Default.TrendingUp
        WalletType.CRYPTO -> Icons.Default.CurrencyBitcoin
    }
}

fun getWalletColor(wallet: WalletEntity): Color {
    return when {
        wallet.name.contains("BCA", ignoreCase = true) -> ColorBcaBlue
        wallet.name.contains("DANA", ignoreCase = true) -> ColorDanaBlue
        wallet.type == WalletType.CASH -> ColorCashGreen
        wallet.type == WalletType.CREDIT_CARD -> ColorCreditCardGold
        wallet.type == WalletType.CRYPTO -> AccentAmber
        else -> Color(wallet.colorHex)
    }
}

/**
 * Vaulta Pearlescent Sky-Blue Frosted Glass Card Item
 */
@Composable
fun VaultaGlassCardItem(
    walletWithBalance: WalletWithComputedBalance,
    holderName: String = "VIKAS MORVADIYA",
    expiryDate: String = "12/36",
    onClick: () -> Unit = {}
) {
    val wallet = walletWithBalance.wallet
    val isCredit = wallet.type == WalletType.CREDIT_CARD
    val cardType = if (isCredit) "Mastercard" else "VISA"

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CardGradientStart,
                            CardGradientMiddle,
                            CardGradientEnd
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Brand & Contactless
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isCredit) {
                        // Mastercard intersecting circles
                        Row {
                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFFEB001B)))
                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFFF79E1B).copy(alpha = 0.8f)).padding(start = 6.dp))
                        }
                    } else {
                        Text(
                            text = "VISA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = CardTextDark
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Contactless",
                        tint = CardTextDark.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Middle: Balance / Card Info
                Column {
                    Text(
                        text = if (isCredit) "Credit Card" else "Total Balance",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = CardTextDark.copy(alpha = 0.7f)
                    )
                    Text(
                        text = formatRupiah(walletWithBalance.balance),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CardTextDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Row: Masked Number & Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (wallet.accountNumberMasked.isNotEmpty()) wallet.accountNumberMasked else "****8160",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CardTextDark.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = expiryDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CardTextDark.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

/**
 * Vaulta Circular Action Button (Electric Blue circle with white icon)
 */
@Composable
fun VaultaCircleActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(BrandBluePrimary)
                .border(1.dp, BrandBlueLight.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondaryDark
        )
    }
}

/**
 * Vaulta Quick Action Tile (Dark rounded-rectangle tile with clean icon)
 */
@Composable
fun VaultaQuickActionTile(
    label: String,
    icon: ImageVector,
    iconColor: Color = BrandBlueLight,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondaryDark
        )
    }
}

/**
 * Vaulta Usage Progress Card ("This Month Uses", "Overall Uses")
 */
@Composable
fun VaultaUsageProgressCard(
    title: String,
    spentText: String,
    limitText: String,
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$spentText / $limitText",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Percentage pill badge & progress bar
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                LinearProgressIndicator(
                    progress = { (percentage / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = BrandBlueVibrant,
                    trackColor = DarkSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandBluePrimary.copy(alpha = 0.3f))
                        .border(0.5.dp, BrandBlueLight.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlueIce
                    )
                }
            }
        }
    }
}

@Composable
fun WalletCardItem(
    walletWithBalance: WalletWithComputedBalance,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val wallet = walletWithBalance.wallet
    val color = getWalletColor(wallet)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkSurfaceElevated else DarkSurface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) BrandBlueVibrant else DarkSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandBluePrimary.copy(alpha = 0.15f))
                        .border(1.dp, BrandBlueVibrant.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getWalletIcon(wallet.type),
                        contentDescription = wallet.name,
                        tint = BrandBlueLight,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = wallet.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${wallet.type.displayName} ${if (wallet.accountNumberMasked.isNotEmpty()) "• ${wallet.accountNumberMasked}" else ""}",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatRupiah(walletWithBalance.balance),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (walletWithBalance.balance >= 0) TextPrimaryDark else AccentCoral
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ledger Verified",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentEmerald
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    tx: TransactionEntity,
    walletsMap: Map<Long, String>,
    onClick: () -> Unit = {}
) {
    val isExpense = tx.type == TransactionType.EXPENSE
    val isIncome = tx.type == TransactionType.INCOME

    val amountColor = when (tx.type) {
        TransactionType.INCOME -> AccentEmerald
        TransactionType.EXPENSE -> Color.White
        TransactionType.TRANSFER -> BrandBlueLight
        TransactionType.ADJUSTMENT -> AccentAmber
    }

    val iconVector = when (tx.type) {
        TransactionType.INCOME -> Icons.Default.ArrowDownward
        TransactionType.EXPENSE -> Icons.Default.ArrowUpward
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
        TransactionType.ADJUSTMENT -> Icons.Default.Tune
    }

    val dateFormatted = remember(tx.timestamp) {
        java.text.SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
            .format(java.util.Date(tx.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isExpense) Color(0xFF1E293B)
                        else if (isIncome) AccentEmerald.copy(alpha = 0.15f)
                        else BrandBluePrimary.copy(alpha = 0.2f)
                    )
                    .border(
                        1.dp,
                        if (isExpense) DarkSurfaceBorder
                        else if (isIncome) AccentEmerald.copy(alpha = 0.3f)
                        else BrandBlueLight.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = tx.type.displayName,
                    tint = if (isExpense) TextPrimaryDark else amountColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.category,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                val walletInfo = when (tx.type) {
                    TransactionType.INCOME -> "Masuk: ${walletsMap[tx.targetWalletId] ?: "Wallet"}"
                    TransactionType.EXPENSE -> "Keluar: ${walletsMap[tx.sourceWalletId] ?: "Wallet"}"
                    TransactionType.TRANSFER -> "${walletsMap[tx.sourceWalletId] ?: "?"} ➔ ${walletsMap[tx.targetWalletId] ?: "?"}"
                    TransactionType.ADJUSTMENT -> "Audit: ${walletsMap[tx.targetWalletId ?: tx.sourceWalletId] ?: "Wallet"}"
                }
                Text(
                    text = "$walletInfo • $dateFormatted",
                    fontSize = 11.sp,
                    color = TextSecondaryDark
                )
                if (tx.note.isNotEmpty()) {
                    Text(
                        text = tx.note,
                        fontSize = 11.sp,
                        color = TextTertiaryDark,
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (isIncome) "+ " else if (isExpense) "- " else ""
                Text(
                    text = "$prefix${formatRupiah(tx.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) Color.White else amountColor
                )
                if (tx.fee > 0) {
                    Text(
                        text = "Fee: ${formatRupiah(tx.fee)}",
                        fontSize = 10.sp,
                        color = TextTertiaryDark
                    )
                }
            }
        }
    }
}

@Composable
fun LedgerEntryRowItem(
    entry: LedgerEntryEntity,
    walletName: String
) {
    val isDebit = entry.entryType == LedgerEntryType.DEBIT
    val typeColor = if (isDebit) AccentEmerald else AccentCoral
    val dateFormatted = remember(entry.timestamp) {
        java.text.SimpleDateFormat("dd MMM, HH:mm:ss", Locale("id", "ID"))
            .format(java.util.Date(entry.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(typeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = entry.entryType.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = walletName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${entry.category} • ${entry.note.ifEmpty { "Entry #${entry.id}" }}",
                fontSize = 11.sp,
                color = TextSecondaryDark
            )
            Text(
                text = dateFormatted,
                fontSize = 10.sp,
                color = TextTertiaryDark
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${if (isDebit) "+" else "-"} ${formatRupiah(entry.amount)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = typeColor
            )
            if (entry.runningBalance > 0) {
                Text(
                    text = "Bal: ${formatRupiah(entry.runningBalance)}",
                    fontSize = 10.sp,
                    color = TextSecondaryDark
                )
            }
        }
    }
}

@Composable
fun BudgetCardItem(
    status: BudgetStatus,
    onClick: () -> Unit = {}
) {
    val b = status.budget
    val progress = (status.percentageUsed / 100.0).toFloat().coerceIn(0f, 1f)
    val indicatorColor = when {
        status.isOverBudget -> AccentCoral
        status.isNearLimit -> AccentAmber
        else -> BrandBlueVibrant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(18.dp))
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandBluePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = b.category,
                            tint = BrandBlueLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = b.category,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                }

                if (status.isOverBudget) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentCoral.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "OVER BUDGET",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCoral
                        )
                    }
                } else if (status.isNearLimit) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentAmber.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "MENDEKATI LIMIT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = indicatorColor,
                trackColor = DarkSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Terpakai: ${formatRupiah(status.spentAmount)} (${formatPercent(status.percentageUsed)})",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
                Text(
                    text = "Limit: ${formatRupiah(b.monthlyLimit)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark
                )
            }
        }
    }
}

@Composable
fun GoalCardItem(
    status: GoalStatus,
    onContributeClick: () -> Unit
) {
    val g = status.goal
    val progress = (status.progressPercentage / 100.0).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
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
                            imageVector = Icons.Default.Flag,
                            contentDescription = g.title,
                            tint = BrandBlueLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = g.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Target: ${status.estimatedCompletionDateFormatted}",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )
                    }
                }

                Text(
                    text = formatPercent(status.progressPercentage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandBlueLight
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = BrandBlueVibrant,
                trackColor = DarkSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${formatRupiah(g.currentSavedAmount)} / ${formatRupiah(g.targetAmount)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Wajib nabung: ${formatRupiah(status.monthlyRequiredSaving)} / bln",
                        fontSize = 11.sp,
                        color = AccentEmerald
                    )
                }

                Button(
                    onClick = onContributeClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Setor Tabungan",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Setor",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun HoldingRowItem(item: HoldingWithPnl) {
    val h = item.holding
    val isProfit = item.unrealizedGainLoss >= 0
    val pnlColor = if (isProfit) AccentEmerald else AccentCoral

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandBluePrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = h.symbol.take(3),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandBlueLight
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = h.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${h.units} unit • Avg @ ${formatRupiah(h.averageBuyPrice)}",
                    fontSize = 11.sp,
                    color = TextSecondaryDark
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatRupiah(item.marketValue),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${if (isProfit) "+" else ""}${formatPercent(item.returnPercentage)} (${formatRupiah(item.unrealizedGainLoss)})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = pnlColor
                )
            }
        }
    }
}

@Composable
fun AiInsightCardItem(
    insight: AiInsightEntity,
    onApplyClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            BrandBluePrimary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .border(1.dp, BrandBlueVibrant.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(BrandBluePrimary.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Insight",
                            tint = BrandBlueLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = insight.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = insight.summary,
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface.copy(alpha = 0.8f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Rekomendasi",
                            tint = AccentAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = insight.actionableAdvice,
                            fontSize = 11.sp,
                            color = TextPrimaryDark,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

