package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class WalletType(val displayName: String, val defaultIcon: String) {
    CASH("Cash / Tunai", "payments"),
    BANK("Bank Account", "account_balance"),
    E_WALLET("E-Wallet", "account_balance_wallet"),
    CREDIT_CARD("Credit Card", "credit_card"),
    INVESTMENT("Investment Account", "trending_up"),
    CRYPTO("Crypto Wallet", "currency_bitcoin")
}

enum class TransactionType(val displayName: String) {
    INCOME("Pemasukan (Income)"),
    EXPENSE("Pengeluaran (Expense)"),
    TRANSFER("Transfer Antar Akun"),
    ADJUSTMENT("Penyesuaian Saldo")
}

enum class LedgerEntryType {
    DEBIT,  // Penambahan aset / Pembayaran beban
    CREDIT  // Pengurangan aset / Penerimaan pendapatan / Kewajiban
}

enum class InvestmentType(val displayName: String) {
    STOCK("Saham (IDX/US)"),
    CRYPTO("Aset Kripto"),
    MUTUAL_FUND("Reksa Dana"),
    BOND("Obligasi / SBN")
}

enum class AssetCategory(val displayName: String) {
    REAL_ESTATE("Properti & Tanah"),
    VEHICLE("Kendaraan"),
    PRECIOUS_METAL("Emas & Logam Mulia"),
    ELECTRONICS("Elektronik & Gadget"),
    RECEIVABLE("Piutang (Dipinjamkan)"),
    OTHER("Aset Lainnya")
}

enum class LiabilityCategory(val displayName: String) {
    MORTGAGE("KPR / Properti"),
    AUTO_LOAN("Kredit Kendaraan"),
    CREDIT_CARD_DEBT("Tagihan Kartu Kredit"),
    PERSONAL_LOAN("Pinjaman Pribadi / Hutang"),
    PAYLATER("Paylater & Fintech Lending")
}

enum class AiInsightType(val title: String) {
    SPENDING_ANOMALY("Deteksi Anomali Pengeluaran"),
    BUDGET_ADVICE("Rekomendasi Optimalisasi Budget"),
    GOAL_PROJECTION("Peluang Percepatan Financial Goal"),
    SAVINGS_OPPORTUNITY("Peluang Penghematan Rutin"),
    PORTFOLIO_REBALANCE("Saran Diversifikasi Portofolio")
}

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: WalletType,
    val currency: String = "IDR",
    val accountNumberMasked: String = "",
    val colorHex: Long = 0xFF00F0FF,
    val iconName: String = "account_balance_wallet",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "transactions",
    indices = [Index("sourceWalletId"), Index("targetWalletId"), Index("timestamp")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val sourceWalletId: Long?,
    val targetWalletId: Long?,
    val amount: Double,
    val fee: Double = 0.0,
    val category: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val receiptData: String? = null,
    val isRecurring: Boolean = false
)

@Entity(
    tableName = "ledger_entries",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("walletId"), Index("transactionId"), Index("timestamp")]
)
data class LedgerEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val walletId: Long,
    val amount: Double,
    val entryType: LedgerEntryType,
    val category: String,
    val note: String = "",
    val runningBalance: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val monthlyLimit: Double,
    val alertThresholdPercent: Int = 80,
    val iconName: String = "category",
    val periodMonth: String = "2026-08"
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentSavedAmount: Double = 0.0,
    val targetDateMillis: Long,
    val category: String = "Umum",
    val colorHex: Long = 0xFF10B981,
    val isAchieved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "asset_liabilities")
data class AssetLiabilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isLiability: Boolean, // false = Asset, true = Liability/Debt
    val categoryName: String,
    val value: Double,
    val interestRatePercent: Double = 0.0,
    val dueDateMillis: Long? = null,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "investment_holdings")
data class InvestmentHoldingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val name: String,
    val assetType: InvestmentType,
    val units: Double,
    val averageBuyPrice: Double,
    val currentMarketPrice: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_insights")
data class AiInsightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: AiInsightType,
    val summary: String,
    val actionableAdvice: String,
    val potentialSavingsMonthly: Double = 0.0,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
