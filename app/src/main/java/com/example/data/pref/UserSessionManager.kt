package com.example.data.pref

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val isLoggedIn: Boolean = false,
    val name: String = "Pengguna",
    val email: String = "pengguna@apexfinance.id",
    val phone: String = "0812-3456-7890",
    val avatarId: Int = 0,
    val currencyCode: String = "IDR",
    val currencySymbol: String = "Rp",
    val isPinEnabled: Boolean = false,
    val pinCode: String = ""
)

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("apex_user_session", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _expenseCategories = MutableStateFlow(loadExpenseCategories())
    val expenseCategories: StateFlow<List<String>> = _expenseCategories.asStateFlow()

    private val _incomeCategories = MutableStateFlow(loadIncomeCategories())
    val incomeCategories: StateFlow<List<String>> = _incomeCategories.asStateFlow()

    private fun loadProfile(): UserProfile {
        val isLoggedIn = prefs.getBoolean("is_logged_in", true) // Default true for seamless experience or setup
        val name = prefs.getString("user_name", "Pengguna Apex") ?: "Pengguna Apex"
        val email = prefs.getString("user_email", "user@apexfinance.id") ?: "user@apexfinance.id"
        val phone = prefs.getString("user_phone", "0812-8888-9999") ?: "0812-8888-9999"
        val avatarId = prefs.getInt("avatar_id", 0)
        val currencyCode = prefs.getString("currency_code", "IDR") ?: "IDR"
        val currencySymbol = prefs.getString("currency_symbol", "Rp") ?: "Rp"
        val isPinEnabled = prefs.getBoolean("is_pin_enabled", false)
        val pinCode = prefs.getString("pin_code", "") ?: ""

        return UserProfile(
            isLoggedIn = isLoggedIn,
            name = name,
            email = email,
            phone = phone,
            avatarId = avatarId,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol,
            isPinEnabled = isPinEnabled,
            pinCode = pinCode
        )
    }

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
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("user_phone", phone)
            .putInt("avatar_id", avatarId)
            .putString("currency_code", currencyCode)
            .putString("currency_symbol", currencySymbol)
            .putBoolean("is_pin_enabled", isPinEnabled)
            .putString("pin_code", pinCode)
            .apply()

        _userProfile.value = UserProfile(
            isLoggedIn = true,
            name = name,
            email = email,
            phone = phone,
            avatarId = avatarId,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol,
            isPinEnabled = isPinEnabled,
            pinCode = pinCode
        )
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
        _userProfile.value = _userProfile.value.copy(isLoggedIn = loggedIn)
    }

    private fun loadExpenseCategories(): List<String> {
        val raw = prefs.getString("expense_categories", null)
        return if (raw != null) {
            raw.split("|||").filter { it.isNotBlank() }
        } else {
            listOf(
                "Makanan & Minuman",
                "Transportasi & Bensin",
                "Belanja & Hiburan",
                "Tagihan & Utilitas",
                "Kesehatan & Obat",
                "Pendidikan",
                "Donasi & Zakat",
                "Lain-lain"
            )
        }
    }

    fun saveExpenseCategories(list: List<String>) {
        prefs.edit().putString("expense_categories", list.joinToString("|||")).apply()
        _expenseCategories.value = list
    }

    fun addExpenseCategory(cat: String) {
        val current = _expenseCategories.value.toMutableList()
        if (!current.contains(cat.trim())) {
            current.add(cat.trim())
            saveExpenseCategories(current)
        }
    }

    fun removeExpenseCategory(cat: String) {
        val current = _expenseCategories.value.toMutableList()
        current.remove(cat)
        saveExpenseCategories(current)
    }

    fun editExpenseCategory(oldCat: String, newCat: String) {
        val current = _expenseCategories.value.toMutableList()
        val idx = current.indexOf(oldCat)
        if (idx != -1) {
            current[idx] = newCat.trim()
            saveExpenseCategories(current)
        }
    }

    private fun loadIncomeCategories(): List<String> {
        val raw = prefs.getString("income_categories", null)
        return if (raw != null) {
            raw.split("|||").filter { it.isNotBlank() }
        } else {
            listOf(
                "Gaji / Payroll",
                "Bonus & Tunjangan",
                "Hasil Bisnis / Freelance",
                "Dividen & Imbal Hasil",
                "Hadiah / Cashback",
                "Penjualan Aset"
            )
        }
    }

    fun saveIncomeCategories(list: List<String>) {
        prefs.edit().putString("income_categories", list.joinToString("|||")).apply()
        _incomeCategories.value = list
    }

    fun addIncomeCategory(cat: String) {
        val current = _incomeCategories.value.toMutableList()
        if (!current.contains(cat.trim())) {
            current.add(cat.trim())
            saveIncomeCategories(current)
        }
    }

    fun removeIncomeCategory(cat: String) {
        val current = _incomeCategories.value.toMutableList()
        current.remove(cat)
        saveIncomeCategories(current)
    }

    fun editIncomeCategory(oldCat: String, newCat: String) {
        val current = _incomeCategories.value.toMutableList()
        val idx = current.indexOf(oldCat)
        if (idx != -1) {
            current[idx] = newCat.trim()
            saveIncomeCategories(current)
        }
    }
}
