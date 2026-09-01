package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.FinanceDao
import com.example.data.model.AiInsightEntity
import com.example.data.model.AiInsightType
import com.example.data.model.AssetCategory
import com.example.data.model.AssetLiabilityEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.GoalEntity
import com.example.data.model.InvestmentHoldingEntity
import com.example.data.model.InvestmentType
import com.example.data.model.LedgerEntryEntity
import com.example.data.model.LedgerEntryType
import com.example.data.model.LiabilityCategory
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.WalletEntity
import com.example.data.model.WalletType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WalletEntity::class,
        TransactionEntity::class,
        LedgerEntryEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        AssetLiabilityEntity::class,
        InvestmentHoldingEntity::class,
        AiInsightEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "apex_finance.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
