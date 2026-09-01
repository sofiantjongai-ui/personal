package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.FinanceDatabase
import com.example.data.model.AiInsightEntity
import com.example.data.model.AssetLiabilityEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.GoalEntity
import com.example.data.model.InvestmentHoldingEntity
import com.example.data.model.InvestmentType
import com.example.data.model.LedgerEntryEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.WalletEntity
import com.example.data.model.WalletType
import com.example.data.pref.UserProfile
import com.example.data.pref.UserSessionManager
import com.example.data.repository.BudgetStatus
import com.example.data.repository.FinanceRepository
import com.example.data.repository.GoalStatus
import com.example.data.repository.NetWorthSummary
import com.example.data.repository.Phase4SimulationStepResult
import com.example.data.repository.PortfolioSummary
import com.example.data.repository.WalletWithComputedBalance
import com.example.util.ReceiptScannerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String, val iconName: String) {
    DASHBOARD("Dashboard", "dashboard"),
    WALLETS("Wallets", "account_balance_wallet"),
    TRANSACTIONS("Ledger", "receipt_long"),
    BUDGET("Budget", "pie_chart"),
    GOALS("Goals", "flag"),
    INVESTMENTS("Invest", "trending_up"),
    NET_WORTH("Net Worth", "account_balance"),
    SCAN_RECEIPT("Scan Struk", "document_scanner"),
    AI_INSIGHTS("AI Advisor", "auto_awesome"),
    SETTINGS("Pengaturan", "settings"),
    VALIDATION("Engine QA", "verified")
}

data class ScannedReceiptPreview(
    val merchantName: String,
    val totalAmount: Double,
    val categorySuggested: String,
    val itemsDetected: List<String>,
    val dateDetected: String
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FinanceDatabase.getDatabase(application, viewModelScope)
    private val repository = FinanceRepository(database.financeDao())
    val sessionManager = UserSessionManager(application)

    val userProfile: StateFlow<UserProfile> = sessionManager.userProfile
    val expenseCategories: StateFlow<List<String>> = sessionManager.expenseCategories
    val incomeCategories: StateFlow<List<String>> = sessionManager.incomeCategories

    val walletsWithBalance: StateFlow<List<WalletWithComputedBalance>> = repository.walletsWithBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ledgerEntries: StateFlow<List<LedgerEntryEntity>> = repository.ledgerEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val netWorthSummary: StateFlow<NetWorthSummary> = repository.netWorthSummary
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetWorthSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        )

    val budgetStatuses: StateFlow<List<BudgetStatus>> = repository.budgetStatuses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goalStatuses: StateFlow<List<GoalStatus>> = repository.goalStatuses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolioSummary: StateFlow<PortfolioSummary> = repository.portfolioSummary
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PortfolioSummary(0.0, 0.0, 0.0, 0.0, emptyList())
        )

    val assetLiabilities: StateFlow<List<AssetLiabilityEntity>> = repository.assetLiabilities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiInsights: StateFlow<List<AiInsightEntity>> = repository.aiInsights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _selectedWalletFilter = MutableStateFlow<Long?>(null)
    val selectedWalletFilter: StateFlow<Long?> = _selectedWalletFilter.asStateFlow()

    private val _transactionTypeFilter = MutableStateFlow<TransactionType?>(null)
    val transactionTypeFilter: StateFlow<TransactionType?> = _transactionTypeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Phase 4 Simulation State
    private val _phase4Results = MutableStateFlow<List<Phase4SimulationStepResult>?>(null)
    val phase4Results: StateFlow<List<Phase4SimulationStepResult>?> = _phase4Results.asStateFlow()

    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    // Receipt OCR Scanning simulator state
    private val _scannedReceipt = MutableStateFlow<ScannedReceiptPreview?>(null)
    val scannedReceipt: StateFlow<ScannedReceiptPreview?> = _scannedReceipt.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setWalletFilter(walletId: Long?) {
        _selectedWalletFilter.value = walletId
    }

    fun setTransactionTypeFilter(type: TransactionType?) {
        _transactionTypeFilter.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // ==========================================
    // ACTIONS: TRANSACTION ENGINE
    // ==========================================

    fun addIncome(
        targetWalletId: Long,
        amount: Double,
        category: String,
        note: String
    ) {
        viewModelScope.launch {
            repository.recordIncome(targetWalletId, amount, category, note)
            _snackbarMessage.value = "Pemasukan berhasil dicatat di Ledger!"
        }
    }

    fun addExpense(
        sourceWalletId: Long,
        amount: Double,
        category: String,
        note: String,
        receiptData: String? = null
    ) {
        viewModelScope.launch {
            repository.recordExpense(sourceWalletId, amount, category, note, receiptData)
            _snackbarMessage.value = "Pengeluaran berhasil dicatat & saldo di-update!"
        }
    }

    fun addTransfer(
        sourceWalletId: Long,
        targetWalletId: Long,
        amount: Double,
        fee: Double = 0.0,
        note: String
    ) {
        viewModelScope.launch {
            repository.recordTransfer(sourceWalletId, targetWalletId, amount, fee, note)
            _snackbarMessage.value = "Transfer berhasil diproses via Double-Entry!"
        }
    }

    fun addAdjustment(
        walletId: Long,
        newActualBalance: Double,
        reason: String
    ) {
        viewModelScope.launch {
            repository.recordAdjustment(walletId, newActualBalance, reason)
            _snackbarMessage.value = "Penyesuaian saldo berhasil dicatat dengan audit trail!"
        }
    }

    // ==========================================
    // ACTIONS: WALLETS & MANAGEMENT
    // ==========================================

    fun createWallet(
        name: String,
        type: WalletType,
        initialBalance: Double,
        accountMasked: String = "",
        colorHex: Long = 0xFF00F0FF
    ) {
        viewModelScope.launch {
            val wallet = WalletEntity(
                name = name,
                type = type,
                accountNumberMasked = accountMasked,
                colorHex = colorHex
            )
            repository.addWallet(wallet, initialBalance)
            _snackbarMessage.value = "Akun/Bank '$name' berhasil ditambahkan!"
        }
    }

    fun updateWallet(wallet: WalletEntity) {
        viewModelScope.launch {
            repository.updateWallet(wallet)
            _snackbarMessage.value = "Data akun '${wallet.name}' berhasil diperbarui!"
        }
    }

    fun deleteWallet(wallet: WalletEntity) {
        viewModelScope.launch {
            repository.deleteWallet(wallet)
            _snackbarMessage.value = "Akun '${wallet.name}' berhasil dihapus!"
        }
    }

    // ==========================================
    // ACTIONS: BUDGET & GOALS
    // ==========================================

    fun createBudget(
        category: String,
        limitAmount: Double,
        alertThreshold: Int = 80
    ) {
        viewModelScope.launch {
            repository.addBudget(
                BudgetEntity(
                    category = category,
                    monthlyLimit = limitAmount,
                    alertThresholdPercent = alertThreshold
                )
            )
            _snackbarMessage.value = "Budget untuk '$category' berhasil disimpan!"
        }
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.updateBudget(budget)
            _snackbarMessage.value = "Budget '${budget.category}' berhasil diperbarui!"
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            _snackbarMessage.value = "Budget '${budget.category}' berhasil dihapus!"
        }
    }

    fun createGoal(
        title: String,
        targetAmount: Double,
        targetDateMillis: Long,
        category: String = "Umum",
        colorHex: Long = 0xFF10B981
    ) {
        viewModelScope.launch {
            repository.addGoal(
                GoalEntity(
                    title = title,
                    targetAmount = targetAmount,
                    currentSavedAmount = 0.0,
                    targetDateMillis = targetDateMillis,
                    category = category,
                    colorHex = colorHex
                )
            )
            _snackbarMessage.value = "Financial Goal '$title' berhasil dibuat!"
        }
    }

    fun contributeGoal(
        goal: GoalEntity,
        fromWalletId: Long,
        amount: Double
    ) {
        viewModelScope.launch {
            repository.recordExpense(
                sourceWalletId = fromWalletId,
                amount = amount,
                category = "Alokasi Goal Finansial",
                note = "Setoran ke goal: ${goal.title}"
            )
            val updated = goal.copy(
                currentSavedAmount = goal.currentSavedAmount + amount,
                isAchieved = (goal.currentSavedAmount + amount) >= goal.targetAmount
            )
            database.financeDao().updateGoal(updated)
            _snackbarMessage.value = "Setoran ke goal '${goal.title}' berhasil!"
        }
    }

    fun createAssetLiability(
        name: String,
        isLiability: Boolean,
        categoryName: String,
        value: Double,
        interestRate: Double = 0.0,
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.addAssetLiability(
                AssetLiabilityEntity(
                    name = name,
                    isLiability = isLiability,
                    categoryName = categoryName,
                    value = value,
                    interestRatePercent = interestRate,
                    note = note
                )
            )
            _snackbarMessage.value = "${if (isLiability) "Liabilitas" else "Aset"} '$name' berhasil disimpan!"
        }
    }

    fun createInvestmentHolding(
        symbol: String,
        name: String,
        type: InvestmentType,
        units: Double,
        avgBuyPrice: Double,
        currentPrice: Double
    ) {
        viewModelScope.launch {
            repository.addInvestment(
                InvestmentHoldingEntity(
                    symbol = symbol,
                    name = name,
                    assetType = type,
                    units = units,
                    averageBuyPrice = avgBuyPrice,
                    currentMarketPrice = currentPrice
                )
            )
            _snackbarMessage.value = "Aset investasi '$symbol' berhasil dicatat di Portofolio!"
        }
    }

    fun markInsightRead(id: Long) {
        viewModelScope.launch {
            repository.markInsightRead(id)
        }
    }

    // ==========================================
    // PROFILE & CATEGORIES
    // ==========================================

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        avatarId: Int,
        currencyCode: String,
        currencySymbol: String,
        isPinEnabled: Boolean,
        pinCode: String
    ) {
        sessionManager.updateProfile(
            name = name,
            email = email,
            phone = phone,
            avatarId = avatarId,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol,
            isPinEnabled = isPinEnabled,
            pinCode = pinCode
        )
        _snackbarMessage.value = "Profil & Pengaturan berhasil disimpan!"
    }

    fun addCategory(category: String, isIncome: Boolean) {
        if (isIncome) {
            sessionManager.addIncomeCategory(category)
        } else {
            sessionManager.addExpenseCategory(category)
        }
        _snackbarMessage.value = "Kategori '$category' berhasil ditambahkan!"
    }

    fun editCategory(oldCategory: String, newCategory: String, isIncome: Boolean) {
        if (isIncome) {
            sessionManager.editIncomeCategory(oldCategory, newCategory)
        } else {
            sessionManager.editExpenseCategory(oldCategory, newCategory)
        }
        _snackbarMessage.value = "Kategori berhasil diperbarui menjadi '$newCategory'!"
    }

    fun removeCategory(category: String, isIncome: Boolean) {
        if (isIncome) {
            sessionManager.removeIncomeCategory(category)
        } else {
            sessionManager.removeExpenseCategory(category)
        }
        _snackbarMessage.value = "Kategori '$category' telah dihapus!"
    }

    // ==========================================
    // CLEAR & RESET DATABASE (NO DUMMY DATA)
    // ==========================================

    fun resetAllDatabaseData() {
        viewModelScope.launch {
            repository.clearAllData()
            _scannedReceipt.value = null
            _phase4Results.value = null
            _snackbarMessage.value = "Database berhasil dikosongkan! Siap untuk input data baru."
        }
    }

    // ==========================================
    // OCR RECEIPT SCANNING ENGINE
    // ==========================================

    fun scanReceiptFromUri(context: Context, uri: Uri) {
        val result = ReceiptScannerHelper.processReceiptUri(context, uri)
        _scannedReceipt.value = result
    }

    fun scanReceiptFromText(text: String) {
        val result = ReceiptScannerHelper.parseRawText(text)
        _scannedReceipt.value = result
    }

    fun setScannedPreview(preview: ScannedReceiptPreview) {
        _scannedReceipt.value = preview
    }

    fun clearScannedReceipt() {
        _scannedReceipt.value = null
    }

    fun confirmReceiptToExpense(
        sourceWalletId: Long,
        merchantName: String? = null,
        amount: Double? = null,
        category: String? = null,
        note: String? = null
    ) {
        val r = _scannedReceipt.value ?: return
        val finalMerchant = merchantName ?: r.merchantName
        val finalAmount = amount ?: r.totalAmount
        val finalCategory = category ?: r.categorySuggested
        val finalNote = note ?: "Struk: $finalMerchant (${r.itemsDetected.joinToString(", ")})"

        addExpense(
            sourceWalletId = sourceWalletId,
            amount = finalAmount,
            category = finalCategory,
            note = finalNote,
            receiptData = finalMerchant
        )
        _scannedReceipt.value = null
        _snackbarMessage.value = "Struk $finalMerchant berhasil dicatat ke Ledger & saldo terpotong!"
    }

    // ==========================================
    // LOGIC SIMULATION
    // ==========================================

    fun runPhase4LogicValidationSimulation() {
        viewModelScope.launch {
            _isSimulating.value = true
            val results = repository.executePhase4Validation()
            _phase4Results.value = results
            _isSimulating.value = false
            _snackbarMessage.value = "Validasi Logika Fase 4 Berhasil Dieksekusi & Diverifikasi!"
        }
    }
}
