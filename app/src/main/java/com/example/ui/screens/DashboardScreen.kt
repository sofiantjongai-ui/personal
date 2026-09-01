package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiInsightEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.BudgetStatus
import com.example.data.repository.NetWorthSummary
import com.example.data.repository.WalletWithComputedBalance
import com.example.ui.components.AiInsightCardItem
import com.example.ui.components.BudgetCardItem
import com.example.ui.components.TransactionRowItem
import com.example.ui.components.VaultaCircleActionButton
import com.example.ui.components.VaultaGlassCardItem
import com.example.ui.components.VaultaQuickActionTile
import com.example.ui.components.VaultaUsageProgressCard
import com.example.ui.components.WalletCardItem
import com.example.ui.components.formatRupiah
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
import com.example.ui.theme.TextBlueAccent
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import com.example.viewmodel.AppNavTab

@Composable
fun DashboardScreen(
    userProfile: com.example.data.pref.UserProfile = com.example.data.pref.UserProfile(),
    netWorth: NetWorthSummary,
    wallets: List<WalletWithComputedBalance>,
    recentTransactions: List<TransactionEntity>,
    budgets: List<BudgetStatus>,
    aiInsights: List<AiInsightEntity>,
    onNavigateTab: (AppNavTab) -> Unit,
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onTransferClick: () -> Unit,
    onScanReceiptClick: () -> Unit
) {
    val walletsMap = remember(wallets) {
        wallets.associate { it.wallet.id to it.wallet.name }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Vaulta Glowing Header (Hello User! + Total Balance)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E3A8A).copy(alpha = 0.6f),
                                Color(0xFF1E293B).copy(alpha = 0.3f),
                                DarkBackground
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    // Profile & Notification row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onNavigateTab(AppNavTab.SETTINGS) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BrandBluePrimary.copy(alpha = 0.3f))
                                    .border(1.5.dp, BrandBlueVibrant, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (userProfile.name.isNotEmpty()) userProfile.name.take(2).uppercase() else "AP",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlueIce
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Hello ${userProfile.name}!",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Apex Finance • Tap untuk Edit",
                                    fontSize = 11.sp,
                                    color = TextBlueAccent
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // QA Engine Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                                    .clickable { onNavigateTab(AppNavTab.VALIDATION) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "QA Test",
                                        tint = AccentEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "QA",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentEmerald
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, DarkSurfaceBorder, CircleShape)
                                    .clickable { onNavigateTab(AppNavTab.AI_INSIGHTS) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = BrandBlueLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Total Balance Hero
                    Column {
                        Text(
                            text = "Total Balance",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandBluePastel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatRupiah(netWorth.netWorth),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimaryDark,
                            letterSpacing = (-0.5).sp
                        )
                    }
                }
            }
        }

        // 2. "Cards" Header & Horizontal Frosted Glass Cards Carousel
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cards",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    TextButton(onClick = { onNavigateTab(AppNavTab.WALLETS) }) {
                        Text(
                            text = "Add +",
                            color = BrandBlueLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (wallets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(DarkSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada kartu tersimpan.", color = TextSecondaryDark)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(wallets) { walletWithBal ->
                            VaultaGlassCardItem(
                                walletWithBalance = walletWithBal,
                                holderName = "VIKAS MORVADIYA",
                                onClick = { onNavigateTab(AppNavTab.WALLETS) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Action Buttons (Circular Electric Blue Buttons from Right Screen)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    VaultaCircleActionButton(
                        label = "Transfer",
                        icon = Icons.Default.SwapHoriz,
                        onClick = onTransferClick
                    )
                    VaultaCircleActionButton(
                        label = "Upgrade",
                        icon = Icons.Default.Upgrade,
                        onClick = { onNavigateTab(AppNavTab.INVESTMENTS) }
                    )
                    VaultaCircleActionButton(
                        label = "Pemasukan",
                        icon = Icons.Default.ArrowDownward,
                        onClick = onAddIncomeClick
                    )
                    VaultaCircleActionButton(
                        label = "Pengeluaran",
                        icon = Icons.Default.ArrowUpward,
                        onClick = onAddExpenseClick
                    )
                }
            }
        }

        // 4. Quick Services Tiles (Grid of 4)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    VaultaQuickActionTile(
                        label = "Crypto",
                        icon = Icons.Default.CurrencyBitcoin,
                        iconColor = AccentAmber,
                        onClick = { onNavigateTab(AppNavTab.INVESTMENTS) }
                    )
                    VaultaQuickActionTile(
                        label = "Scan Nota",
                        icon = Icons.Default.DocumentScanner,
                        iconColor = BrandBlueLight,
                        onClick = onScanReceiptClick
                    )
                    VaultaQuickActionTile(
                        label = "Goal",
                        icon = Icons.Default.TrendingUp,
                        iconColor = AccentEmerald,
                        onClick = { onNavigateTab(AppNavTab.GOALS) }
                    )
                    VaultaQuickActionTile(
                        label = "Anggaran",
                        icon = Icons.Default.Receipt,
                        iconColor = BrandBluePastel,
                        onClick = { onNavigateTab(AppNavTab.BUDGET) }
                    )
                }
            }
        }

        // 5. Card Usage Analytics & Limits (This Month Uses & Overall Uses)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Usage Analytics",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VaultaUsageProgressCard(
                        title = "This Month Uses",
                        spentText = formatRupiah(netWorth.totalLiabilities.coerceAtLeast(1500000.0)),
                        limitText = formatRupiah(3000000.0),
                        percentage = 50,
                        modifier = Modifier.weight(1f)
                    )
                    VaultaUsageProgressCard(
                        title = "Overall Uses",
                        spentText = formatRupiah(netWorth.totalLiquidCash.coerceAtLeast(8000000.0)),
                        limitText = formatRupiah(10000000.0),
                        percentage = 80,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Card Statement Button Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BrandBluePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = "Statement",
                                    tint = BrandBlueLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Card Statement",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryDark
                            )
                        }

                        Text(
                            text = "View",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlueLight,
                            modifier = Modifier.clickable { onNavigateTab(AppNavTab.TRANSACTIONS) }
                        )
                    }
                }
            }
        }

        // 7. Last Transactions with "View All >"
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Last Transactions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "View All >",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlueLight,
                        modifier = Modifier.clickable { onNavigateTab(AppNavTab.TRANSACTIONS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (recentTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceElevated)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada transaksi di ledger.",
                            fontSize = 13.sp,
                            color = TextSecondaryDark
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentTransactions.take(4).forEach { tx ->
                            TransactionRowItem(
                                tx = tx,
                                walletsMap = walletsMap,
                                onClick = { onNavigateTab(AppNavTab.TRANSACTIONS) }
                            )
                        }
                    }
                }
            }
        }

        // 8. AI Insights Preview
        if (aiInsights.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BrandBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Vaulta AI Advisor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }
                        TextButton(onClick = { onNavigateTab(AppNavTab.AI_INSIGHTS) }) {
                            Text("Analisis", color = BrandBlueLight, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    aiInsights.firstOrNull()?.let { insight ->
                        AiInsightCardItem(insight = insight)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

