package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.data.model.WalletType
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.AddWalletDialog
import com.example.ui.components.ScanReceiptModal
import com.example.ui.screens.AiAdvisorScreen
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.InvestmentsScreen
import com.example.ui.screens.NetWorthScreen
import com.example.ui.screens.ScanReceiptScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SystemValidationScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.WalletsScreen
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BrandBlueIce
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueVibrant
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.viewmodel.AppNavTab
import com.example.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                ApexFinanceApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ApexFinanceApp(viewModel: FinanceViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val wallets by viewModel.walletsWithBalance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val ledgerEntries by viewModel.ledgerEntries.collectAsState()
    val netWorth by viewModel.netWorthSummary.collectAsState()
    val budgets by viewModel.budgetStatuses.collectAsState()
    val goals by viewModel.goalStatuses.collectAsState()
    val portfolio by viewModel.portfolioSummary.collectAsState()
    val assetLiabilities by viewModel.assetLiabilities.collectAsState()
    val aiInsights by viewModel.aiInsights.collectAsState()
    val phase4Results by viewModel.phase4Results.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()
    val scannedReceipt by viewModel.scannedReceipt.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val userProfile by viewModel.userProfile.collectAsState()
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var showAddTxDialog by remember { mutableStateOf(false) }
    var initialTxType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var showAddWalletDialog by remember { mutableStateOf(false) }
    var showScanReceiptModal by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            FintechBottomNavigation(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    initialTxType = TransactionType.EXPENSE
                    showAddTxDialog = true
                },
                containerColor = BrandBluePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .testTag("main_add_fab")
                    .size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            // Sleek Top Nav Tabs for High Feature Density Navigation
            FintechTopTabRow(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )

            // Main Content Area
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition",
                modifier = Modifier.weight(1f)
            ) { targetTab ->
                when (targetTab) {
                    AppNavTab.DASHBOARD -> DashboardScreen(
                        userProfile = userProfile,
                        netWorth = netWorth,
                        wallets = wallets,
                        recentTransactions = transactions,
                        budgets = budgets,
                        aiInsights = aiInsights,
                        onNavigateTab = { viewModel.setTab(it) },
                        onAddIncomeClick = {
                            initialTxType = TransactionType.INCOME
                            showAddTxDialog = true
                        },
                        onAddExpenseClick = {
                            initialTxType = TransactionType.EXPENSE
                            showAddTxDialog = true
                        },
                        onTransferClick = {
                            initialTxType = TransactionType.TRANSFER
                            showAddTxDialog = true
                        },
                        onScanReceiptClick = {
                            viewModel.setTab(AppNavTab.SCAN_RECEIPT)
                        }
                    )

                    AppNavTab.WALLETS -> WalletsScreen(
                        wallets = wallets,
                        onAddWalletClick = { showAddWalletDialog = true },
                        onAuditAdjustmentClick = { walletId ->
                            initialTxType = TransactionType.ADJUSTMENT
                            showAddTxDialog = true
                        }
                    )

                    AppNavTab.TRANSACTIONS -> TransactionsScreen(
                        transactions = transactions,
                        ledgerEntries = ledgerEntries,
                        wallets = wallets,
                        onAddTransactionClick = {
                            initialTxType = TransactionType.EXPENSE
                            showAddTxDialog = true
                        }
                    )

                    AppNavTab.SCAN_RECEIPT -> ScanReceiptScreen(
                        scannedReceipt = scannedReceipt,
                        wallets = wallets,
                        expenseCategories = expenseCategories,
                        onScanFromUri = { uri ->
                            viewModel.scanReceiptFromUri(context, uri)
                        },
                        onScanFromText = { text ->
                            viewModel.scanReceiptFromText(text)
                        },
                        onConfirmExpense = { walletId, merchant, amount, category, note ->
                            viewModel.confirmReceiptToExpense(walletId, merchant, amount, category, note)
                        },
                        onClearReceipt = {
                            viewModel.clearScannedReceipt()
                        }
                    )

                    AppNavTab.BUDGET -> BudgetScreen(
                        budgets = budgets,
                        onCreateBudget = { cat, limit, thresh ->
                            viewModel.createBudget(cat, limit, thresh)
                        }
                    )

                    AppNavTab.GOALS -> GoalsScreen(
                        goals = goals,
                        wallets = wallets,
                        onCreateGoal = { title, target, dateMillis, cat ->
                            viewModel.createGoal(title, target, dateMillis, cat)
                        },
                        onContributeGoal = { goal, walletId, amount ->
                            viewModel.contributeGoal(goal, walletId, amount)
                        }
                    )

                    AppNavTab.INVESTMENTS -> InvestmentsScreen(
                        portfolio = portfolio,
                        onAddHolding = { sym, name, type, units, avg, current ->
                            viewModel.createInvestmentHolding(sym, name, type, units, avg, current)
                        }
                    )

                    AppNavTab.NET_WORTH -> NetWorthScreen(
                        netWorth = netWorth,
                        assetLiabilities = assetLiabilities,
                        onAddAssetLiability = { name, isLiab, cat, valAmount, interest, note ->
                            viewModel.createAssetLiability(name, isLiab, cat, valAmount, interest, note)
                        }
                    )

                    AppNavTab.AI_INSIGHTS -> AiAdvisorScreen(
                        netWorth = netWorth,
                        insights = aiInsights,
                        onMarkRead = { viewModel.markInsightRead(it) }
                    )

                    AppNavTab.SETTINGS -> SettingsScreen(
                        userProfile = userProfile,
                        wallets = wallets,
                        expenseCategories = expenseCategories,
                        incomeCategories = incomeCategories,
                        budgets = budgets,
                        onUpdateProfile = { name, email, phone, avatarId, currCode, currSym, pinEnabled, pin ->
                            viewModel.updateProfile(name, email, phone, avatarId, currCode, currSym, pinEnabled, pin)
                        },
                        onAddWalletClick = { showAddWalletDialog = true },
                        onEditWallet = { w -> viewModel.updateWallet(w) },
                        onDeleteWallet = { w -> viewModel.deleteWallet(w) },
                        onAdjustBalance = { walletId, newBal ->
                            viewModel.addAdjustment(walletId, newBal, "Koreksi saldo via Pengaturan")
                        },
                        onAddCategory = { cat, isIncome ->
                            viewModel.addCategory(cat, isIncome)
                        },
                        onEditCategory = { oldCat, newCat, isIncome ->
                            viewModel.editCategory(oldCat, newCat, isIncome)
                        },
                        onDeleteCategory = { cat, isIncome ->
                            viewModel.removeCategory(cat, isIncome)
                        },
                        onUpdateBudgetLimit = { b, newLimit, threshold ->
                            viewModel.updateBudget(b.copy(monthlyLimit = newLimit, alertThresholdPercent = threshold))
                        },
                        onDeleteBudget = { b ->
                            viewModel.deleteBudget(b)
                        },
                        onCreateBudgetForCategory = { cat, limit ->
                            viewModel.createBudget(cat, limit, 80)
                        },
                        onResetAllDatabaseData = {
                            viewModel.resetAllDatabaseData()
                        }
                    )

                    AppNavTab.VALIDATION -> SystemValidationScreen(
                        simulationResults = phase4Results,
                        isSimulating = isSimulating,
                        onRunSimulation = { viewModel.runPhase4LogicValidationSimulation() }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddTxDialog) {
        AddTransactionDialog(
            wallets = wallets,
            initialType = initialTxType,
            onDismiss = { showAddTxDialog = false },
            onConfirmIncome = { targetId, amt, cat, note ->
                viewModel.addIncome(targetId, amt, cat, note)
            },
            onConfirmExpense = { srcId, amt, cat, note ->
                viewModel.addExpense(srcId, amt, cat, note)
            },
            onConfirmTransfer = { srcId, targetId, amt, fee, note ->
                viewModel.addTransfer(srcId, targetId, amt, fee, note)
            },
            onConfirmAdjustment = { walletId, newBal, reason ->
                viewModel.addAdjustment(walletId, newBal, reason)
            }
        )
    }

    if (showAddWalletDialog) {
        AddWalletDialog(
            onDismiss = { showAddWalletDialog = false },
            onConfirm = { name, type, initialBal, masked ->
                viewModel.createWallet(name, type, initialBal, masked)
            }
        )
    }

    if (showScanReceiptModal) {
        ScanReceiptModal(
            scannedPreview = scannedReceipt,
            wallets = wallets,
            onDismiss = {
                showScanReceiptModal = false
                viewModel.clearScannedReceipt()
            },
            onConfirmExpense = { walletId ->
                viewModel.confirmReceiptToExpense(walletId)
                showScanReceiptModal = false
            }
        )
    }
}

@Composable
fun FintechTopTabRow(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = currentTab.ordinal,
        containerColor = DarkSurface,
        contentColor = BrandBlueLight,
        edgePadding = 12.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                color = BrandBlueVibrant,
                height = 2.5.dp
            )
        }
    ) {
        AppNavTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) BrandBlueLight else TextSecondaryDark
                    )
                }
            )
        }
    }
}

@Composable
fun FintechBottomNavigation(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit
) {
    val bottomTabs = listOf(
        Triple(AppNavTab.DASHBOARD, Icons.Default.Home, "Home"),
        Triple(AppNavTab.WALLETS, Icons.Default.CreditCard, "Bank/Kas"),
        Triple(AppNavTab.TRANSACTIONS, Icons.Default.SwapHoriz, "Mutasi"),
        Triple(AppNavTab.SCAN_RECEIPT, Icons.Default.DocumentScanner, "Scan Struk"),
        Triple(AppNavTab.SETTINGS, Icons.Default.Settings, "Pengaturan")
    )

    NavigationBar(
        containerColor = DarkSurfaceElevated,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
    ) {
        bottomTabs.forEach { (tab, icon, label) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) BrandBlueLight else TextSecondaryDark,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BrandBlueLight else TextSecondaryDark
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BrandBluePrimary.copy(alpha = 0.22f)
                )
            )
        }
    }
}
