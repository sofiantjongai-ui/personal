package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.AiInsightEntity
import com.example.data.model.AssetLiabilityEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.GoalEntity
import com.example.data.model.InvestmentHoldingEntity
import com.example.data.model.LedgerEntryEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.WalletEntity
import kotlinx.coroutines.flow.Flow

data class WalletBalanceAggregate(
    val walletId: Long,
    val totalDebit: Double?,
    val totalCredit: Double?
) {
    val currentBalance: Double
        get() = (totalDebit ?: 0.0) - (totalCredit ?: 0.0)
}

data class CategorySpendingAggregate(
    val category: String,
    val totalSpent: Double
)

@Dao
interface FinanceDao {

    // === WALLETS ===
    @Query("SELECT * FROM wallets WHERE isActive = 1 ORDER BY id ASC")
    fun getAllWalletsFlow(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets WHERE id = :id LIMIT 1")
    suspend fun getWalletById(id: Long): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Delete
    suspend fun deleteWallet(wallet: WalletEntity)

    // === LEDGER ENTRIES (Double Entry Source of Truth) ===
    @Query("SELECT * FROM ledger_entries ORDER BY timestamp DESC, id DESC")
    fun getAllLedgerEntriesFlow(): Flow<List<LedgerEntryEntity>>

    @Query("SELECT * FROM ledger_entries WHERE walletId = :walletId ORDER BY timestamp DESC, id DESC")
    fun getLedgerEntriesByWalletFlow(walletId: Long): Flow<List<LedgerEntryEntity>>

    @Query("SELECT * FROM ledger_entries WHERE walletId = :walletId ORDER BY timestamp ASC, id ASC")
    suspend fun getLedgerEntriesForWalletAsc(walletId: Long): List<LedgerEntryEntity>

    @Query("""
        SELECT 
            walletId,
            SUM(CASE WHEN entryType = 'DEBIT' THEN amount ELSE 0 END) as totalDebit,
            SUM(CASE WHEN entryType = 'CREDIT' THEN amount ELSE 0 END) as totalCredit
        FROM ledger_entries
        GROUP BY walletId
    """)
    fun getWalletBalancesFlow(): Flow<List<WalletBalanceAggregate>>

    @Query("""
        SELECT 
            SUM(CASE WHEN entryType = 'DEBIT' THEN amount ELSE 0 END) - 
            SUM(CASE WHEN entryType = 'CREDIT' THEN amount ELSE 0 END)
        FROM ledger_entries
        WHERE walletId = :walletId
    """)
    suspend fun calculateWalletBalance(walletId: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: LedgerEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntries(entries: List<LedgerEntryEntity>)

    // === TRANSACTIONS ===
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC, id DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // Category Spending for Budgets
    @Query("""
        SELECT category, SUM(amount) as totalSpent 
        FROM transactions 
        WHERE type = 'EXPENSE'
        GROUP BY category
    """)
    fun getSpendingByCategoryFlow(): Flow<List<CategorySpendingAggregate>>

    // === BUDGETS ===
    @Query("SELECT * FROM budgets ORDER BY monthlyLimit DESC")
    fun getAllBudgetsFlow(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // === GOALS ===
    @Query("SELECT * FROM goals ORDER BY targetDateMillis ASC")
    fun getAllGoalsFlow(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // === ASSETS & LIABILITIES (NET WORTH) ===
    @Query("SELECT * FROM asset_liabilities ORDER BY isLiability ASC, value DESC")
    fun getAllAssetLiabilitiesFlow(): Flow<List<AssetLiabilityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssetLiability(item: AssetLiabilityEntity): Long

    @Update
    suspend fun updateAssetLiability(item: AssetLiabilityEntity)

    @Delete
    suspend fun deleteAssetLiability(item: AssetLiabilityEntity)

    // === INVESTMENTS ===
    @Query("SELECT * FROM investment_holdings ORDER BY (units * currentMarketPrice) DESC")
    fun getAllInvestmentsFlow(): Flow<List<InvestmentHoldingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(holding: InvestmentHoldingEntity): Long

    @Update
    suspend fun updateInvestment(holding: InvestmentHoldingEntity)

    @Delete
    suspend fun deleteInvestment(holding: InvestmentHoldingEntity)

    // === AI INSIGHTS ===
    @Query("SELECT * FROM ai_insights ORDER BY timestamp DESC")
    fun getAllAiInsightsFlow(): Flow<List<AiInsightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiInsight(insight: AiInsightEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiInsights(insights: List<AiInsightEntity>)

    @Query("UPDATE ai_insights SET isRead = 1 WHERE id = :id")
    suspend fun markInsightAsRead(id: Long)

    // Bulk delete for reset/seeding
    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM ledger_entries")
    suspend fun clearLedger()

    @Query("DELETE FROM wallets")
    suspend fun clearWallets()

    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()

    @Query("DELETE FROM goals")
    suspend fun clearGoals()

    @Query("DELETE FROM asset_liabilities")
    suspend fun clearAssetLiabilities()

    @Query("DELETE FROM investment_holdings")
    suspend fun clearInvestments()

    @Query("DELETE FROM ai_insights")
    suspend fun clearAiInsights()
}
