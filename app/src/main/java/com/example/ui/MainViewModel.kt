package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MainUiState(
    val activeTab: Int = 0,
    val currentTimeString: String = "",
    val stats: Stats = Stats(),
    val todayPurchases: Int = 0,
    val totalPurchases: Int = 0,
    
    // Requests
    val requests: List<ActivationRequest> = emptyList(),
    val isLoadingRequests: Boolean = false,
    val requestPointsMap: Map<String, String> = emptyMap(),
    val requestExpiryMap: Map<String, String> = emptyMap(),
    
    // Users
    val users: List<UserItem> = emptyList(),
    val filteredUsers: List<UserItem> = emptyList(),
    val isLoadingUsers: Boolean = false,
    val userSearchQuery: String = "",
    val currentPage: Int = 1,
    val usersPerPage: Int = 15,
    
    // History
    val history: List<HistoryItem> = emptyList(),
    val isLoadingHistory: Boolean = false,
    
    // Notifications
    val notifications: List<NotificationItem> = emptyList(),
    val isLoadingNotifications: Boolean = false,
    val notifTarget: String = "all",
    val notifTitle: String = "",
    val notifType: String = "info",
    val notifMessage: String = "",
    
    // App Control
    val isLoadingConfig: Boolean = false,
    val appConfig: AppConfig = AppConfig(),
    val popupConfig: PopupConfig = PopupConfig(),
    val updateConfig: AppUpdateConfig = AppUpdateConfig(),
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "",
    val maintenanceContact: String = "",
    val isPopupEnabled: Boolean = false,
    val popupTitle: String = "",
    val popupImageUrl: String = "",
    val popupMessage: String = "",
    val popupButtonText: String = "",
    val popupButtonUrl: String = "",
    val isPopupShowOnce: Boolean = true,

    // App Updates Control
    val latestVersionCode: String = "1",
    val latestVersionName: String = "1.0",
    val minRequiredVersion: String = "1",
    val isForceUpdate: Boolean = false,
    val updateTitle: String = "",
    val updateNotes: String = "",
    val downloadUrl: String = "",
    
    // Modals / Feedback
    val pointsDialogUser: UserItem? = null,
    val expiryDialogUser: UserItem? = null,
    val userMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val api = OrorApiService.create()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val prefs = application.getSharedPreferences("oror_admin_prefs", android.content.Context.MODE_PRIVATE)
    private val knownRequestIds = mutableSetOf<String>()

    init {
        // Load previously notified request IDs
        val saved = prefs.getStringSet("known_request_ids", emptySet()) ?: emptySet()
        knownRequestIds.addAll(saved)

        startClock()
        loadAllData()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(3500) // Poll for new requests every 3.5 seconds
                checkNewRequests()
            }
        }
    }

    private suspend fun checkNewRequests() {
        try {
            val res = api.getRequests()
            if (res.success && res.requests != null) {
                processIncomingRequests(res.requests)
                loadStats()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processIncomingRequests(newRequests: List<ActivationRequest>) {
        val newlyAdded = newRequests.filter { it.id !in knownRequestIds }
        for (req in newlyAdded) {
            knownRequestIds.add(req.id)
            NotificationHelper.showRequestNotification(
                context = getApplication(),
                username = req.username,
                phoneOrDetails = req.phone,
                requestId = req.id
            )
        }

        // Persist known IDs
        if (newlyAdded.isNotEmpty()) {
            prefs.edit().putStringSet("known_request_ids", knownRequestIds).apply()
        }

        _uiState.update {
            it.copy(requests = newRequests)
        }
    }

    private fun startClock() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale("ar", "EG"))
            while (true) {
                val nowStr = sdf.format(Date())
                _uiState.update { it.copy(currentTimeString = nowStr) }
                delay(1000)
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
        when (tabIndex) {
            0 -> loadRequests()
            1 -> loadUsers()
            2 -> loadHistory()
            3 -> loadNotifications()
            4 -> loadAppConfig()
        }
    }

    fun loadAllData() {
        loadStats()
        loadRequests()
        loadUsers()
        loadHistory()
        loadNotifications()
        loadAppConfig()
    }

    // ===== STATS & HISTORY =====
    fun loadStats() {
        viewModelScope.launch {
            try {
                val res = api.getStats()
                if (res.success && res.stats != null) {
                    _uiState.update {
                        it.copy(
                            stats = res.stats,
                            todayPurchases = if (res.stats.todayPurchases > 0) res.stats.todayPurchases else it.todayPurchases,
                            totalPurchases = if (res.stats.totalPurchases > 0) res.stats.totalPurchases else it.totalPurchases
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }
            try {
                val res = api.getHistory()
                if (res.success && res.history != null) {
                    val list = res.history
                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val todayCount = list.count { item ->
                        item.purchaseTime.startsWith(todayStr)
                    }
                    _uiState.update {
                        it.copy(
                            history = list,
                            isLoadingHistory = false,
                            todayPurchases = if (todayCount > 0) todayCount else it.todayPurchases,
                            totalPurchases = if (list.isNotEmpty()) list.size else it.totalPurchases
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoadingHistory = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingHistory = false) }
            }
        }
    }

    // ===== REQUESTS =====
    fun loadRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRequests = true) }
            try {
                val res = api.getRequests()
                if (res.success && res.requests != null) {
                    processIncomingRequests(res.requests)
                    _uiState.update { it.copy(isLoadingRequests = false) }
                } else {
                    _uiState.update { it.copy(requests = emptyList(), isLoadingRequests = false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoadingRequests = false) }
            }
        }
    }

    fun updateRequestPointsInput(requestId: String, points: String) {
        _uiState.update {
            val updatedMap = it.requestPointsMap.toMutableMap()
            updatedMap[requestId] = points
            it.copy(requestPointsMap = updatedMap)
        }
    }

    fun updateRequestExpiryInput(requestId: String, expiry: String) {
        _uiState.update {
            val updatedMap = it.requestExpiryMap.toMutableMap()
            updatedMap[requestId] = expiry
            it.copy(requestExpiryMap = updatedMap)
        }
    }

    fun approveRequest(requestId: String) {
        val pointsStr = _uiState.value.requestPointsMap[requestId]?.trim() ?: "0"
        val pointsVal = if (pointsStr.isBlank()) 0 else (pointsStr.toIntOrNull() ?: 0)
        val expiryVal = _uiState.value.requestExpiryMap[requestId]?.trim()?.ifBlank { "forever" } ?: "forever"
        if (pointsVal < 0) {
            showMessage("❌ عدد النقاط غير صالح")
            return
        }

        viewModelScope.launch {
            try {
                val res = api.approveRequest(ApproveRequestBody(requestId = requestId, points = pointsVal, expiry = expiryVal))
                showMessage(res.message ?: if (res.success) "✅ تم قبول الطلب" else "❌ فشل قبول الطلب")
                if (res.success) {
                    loadRequests()
                    loadUsers()
                    loadStats()
                }
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val res = api.rejectRequest(RejectRequestBody(requestId))
                showMessage(res.message ?: if (res.success) "✅ تم رفض الطلب" else "❌ فشل الرفض")
                if (res.success) {
                    loadRequests()
                    loadStats()
                }
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    // ===== USERS =====
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUsers = true) }
            try {
                val res = api.getUsers()
                if (res.success && res.users != null) {
                    val all = res.users
                    _uiState.update {
                        it.copy(
                            users = all,
                            isLoadingUsers = false
                        )
                    }
                    applyUserSearch(uiState.value.userSearchQuery)
                } else {
                    _uiState.update { it.copy(users = emptyList(), filteredUsers = emptyList(), isLoadingUsers = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingUsers = false) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(userSearchQuery = query, currentPage = 1) }
        applyUserSearch(query)
    }

    private fun applyUserSearch(query: String) {
        val q = query.trim().lowercase()
        val all = _uiState.value.users
        val filtered = if (q.isEmpty()) {
            all
        } else {
            all.filter {
                it.username.lowercase().contains(q) || it.phone.lowercase().contains(q)
            }
        }
        _uiState.update { it.copy(filteredUsers = filtered) }
    }

    fun setPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    fun openPointsModal(user: UserItem) {
        _uiState.update { it.copy(pointsDialogUser = user) }
    }

    fun closePointsModal() {
        _uiState.update { it.copy(pointsDialogUser = null) }
    }

    fun openExpiryModal(user: UserItem) {
        _uiState.update { it.copy(expiryDialogUser = user) }
    }

    fun closeExpiryModal() {
        _uiState.update { it.copy(expiryDialogUser = null) }
    }

    fun updateUserExpiry(expiryDate: String) {
        val targetUser = _uiState.value.expiryDialogUser ?: return
        viewModelScope.launch {
            try {
                val body = UpdateExpiryBody(deviceId = targetUser.deviceId, expiryDate = expiryDate)
                val res = api.updateExpiry(body)
                showMessage(res.message ?: "✅ تم تحديث الصلاحية بنجاح")
                if (res.success) {
                    closeExpiryModal()
                    loadUsers()
                }
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    fun manageUserPoints(isAdd: Boolean, amountStr: String) {
        val amount = amountStr.toIntOrNull() ?: 0
        val targetUser = _uiState.value.pointsDialogUser ?: return
        if (amount <= 0) {
            showMessage("❌ أدخل عدد النقاط")
            return
        }

        viewModelScope.launch {
            try {
                val body = ManagePointsBody(deviceId = targetUser.deviceId, points = amount)
                val res = if (isAdd) api.addPoints(body) else api.removePoints(body)
                showMessage(res.message ?: "✅ تم التحديث")
                if (res.success) {
                    closePointsModal()
                    loadUsers()
                    loadStats()
                }
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    fun deleteUser(user: UserItem) {
        viewModelScope.launch {
            try {
                val res = api.deleteUser(DeleteUserBody(deviceId = user.deviceId))
                showMessage(res.message ?: "✅ تم حذف المستخدم")
                if (res.success) {
                    loadUsers()
                    loadStats()
                }
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    // ===== NOTIFICATIONS =====
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingNotifications = true) }
            try {
                val res = api.getNotifications()
                if (res.success && res.notifications != null) {
                    val notifList = mutableListOf<NotificationItem>()
                    val moshi = com.squareup.moshi.Moshi.Builder()
                        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                        .build()
                    val itemAdapter = moshi.adapter(NotificationItem::class.java)

                    for (item in res.notifications) {
                        if (item is Map<*, *>) {
                            try {
                                val mapAdapter = moshi.adapter(Map::class.java)
                                val jsonStr = mapAdapter.toJson(item)
                                val notif = itemAdapter.fromJson(jsonStr)
                                if (notif != null && (notif.title.isNotBlank() || notif.message.isNotBlank())) {
                                    notifList.add(notif)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    _uiState.update { it.copy(notifications = notifList, isLoadingNotifications = false) }
                } else {
                    _uiState.update { it.copy(notifications = emptyList(), isLoadingNotifications = false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoadingNotifications = false) }
            }
        }
    }

    fun setNotifTarget(target: String) {
        _uiState.update { it.copy(notifTarget = target) }
    }

    fun setNotifTitle(title: String) {
        _uiState.update { it.copy(notifTitle = title) }
    }

    fun setNotifType(type: String) {
        _uiState.update { it.copy(notifType = type) }
    }

    fun setNotifMessage(msg: String) {
        _uiState.update { it.copy(notifMessage = msg) }
    }

    fun sendNotification() {
        val s = _uiState.value
        if (s.notifTitle.isBlank() || s.notifMessage.isBlank()) {
            showMessage("❌ يرجى كتابة عنوان ونص الإشعار")
            return
        }

        viewModelScope.launch {
            try {
                val body = SendNotificationBody(
                    targetUser = s.notifTarget,
                    title = s.notifTitle,
                    message = s.notifMessage,
                    type = s.notifType
                )
                val res = api.sendNotification(body)
                showMessage(res.message ?: if (res.success) "✅ تم نشر الإشعار بنجاح!" else "❌ فشل الإرسال")
                if (res.success) {
                    _uiState.update { it.copy(notifTitle = "", notifMessage = "") }
                    loadNotifications()
                }
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    fun deleteNotification(notifId: String) {
        viewModelScope.launch {
            try {
                val res = api.deleteNotification(DeleteNotificationBody(notificationId = notifId))
                showMessage(res.message ?: "✅ تم حذف الإشعار")
                if (res.success) {
                    loadNotifications()
                }
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    // ===== APP CONFIG & CONTROL =====
    fun loadAppConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingConfig = true) }
            try {
                val res = api.getAppConfig()
                if (res.success) {
                    val cfg = res.config
                    val pop = res.popup
                    val upd = res.update
                    _uiState.update {
                        it.copy(
                            appConfig = cfg ?: AppConfig(),
                            popupConfig = pop ?: PopupConfig(),
                            updateConfig = upd ?: AppUpdateConfig(),
                            isMaintenanceMode = cfg?.isMaintenanceEnabled ?: false,
                            maintenanceMessage = cfg?.maintenanceMessage ?: "",
                            maintenanceContact = cfg?.maintenanceContact ?: "",
                            isPopupEnabled = pop?.isPopupEnabled ?: false,
                            popupTitle = pop?.title ?: "",
                            popupImageUrl = pop?.imageUrl ?: "",
                            popupMessage = pop?.message ?: "",
                            popupButtonText = pop?.buttonText ?: "",
                            popupButtonUrl = pop?.buttonUrl ?: "",
                            isPopupShowOnce = pop?.isShowOnceEnabled ?: true,
                            latestVersionCode = (upd?.versionCodeInt ?: 1).toString(),
                            latestVersionName = upd?.latestVersionName ?: "1.0",
                            minRequiredVersion = (upd?.minVersionInt ?: 1).toString(),
                            isForceUpdate = upd?.isForceUpdate ?: false,
                            updateTitle = upd?.updateTitle ?: "🚀 تحديث جديد متوفر!",
                            updateNotes = upd?.updateNotes ?: "",
                            downloadUrl = upd?.downloadUrl ?: "https://t.me/oror_dev",
                            isLoadingConfig = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoadingConfig = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingConfig = false) }
            }
        }
    }

    fun setMaintenanceMode(enabled: Boolean) {
        _uiState.update { it.copy(isMaintenanceMode = enabled) }
    }

    fun setMaintenanceMessage(msg: String) {
        _uiState.update { it.copy(maintenanceMessage = msg) }
    }

    fun setMaintenanceContact(contact: String) {
        _uiState.update { it.copy(maintenanceContact = contact) }
    }

    fun saveMaintenanceSettings() {
        val s = _uiState.value
        viewModelScope.launch {
            try {
                val body = UpdateAppConfigBody(
                    maintenanceMode = if (s.isMaintenanceMode) 1 else 0,
                    maintenanceMessage = s.maintenanceMessage,
                    maintenanceContact = s.maintenanceContact
                )
                val res = api.updateAppConfig(body)
                showMessage(res.message ?: "✅ تم حفظ إعدادات الصيانة")
                loadAppConfig()
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    fun setPopupEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isPopupEnabled = enabled) }
    }

    fun setPopupTitle(title: String) {
        _uiState.update { it.copy(popupTitle = title) }
    }

    fun setPopupImageUrl(url: String) {
        _uiState.update { it.copy(popupImageUrl = url) }
    }

    fun setPopupMessage(msg: String) {
        _uiState.update { it.copy(popupMessage = msg) }
    }

    fun setPopupButtonText(text: String) {
        _uiState.update { it.copy(popupButtonText = text) }
    }

    fun setPopupButtonUrl(url: String) {
        _uiState.update { it.copy(popupButtonUrl = url) }
    }

    fun setPopupShowOnce(showOnce: Boolean) {
        _uiState.update { it.copy(isPopupShowOnce = showOnce) }
    }

    fun savePopupSettings() {
        val s = _uiState.value
        viewModelScope.launch {
            try {
                val body = UpdatePopupConfigBody(
                    enabled = if (s.isPopupEnabled) 1 else 0,
                    title = s.popupTitle,
                    imageUrl = s.popupImageUrl,
                    message = s.popupMessage,
                    buttonText = s.popupButtonText,
                    buttonUrl = s.popupButtonUrl,
                    showOnce = if (s.isPopupShowOnce) 1 else 0,
                    id = "popup_${System.currentTimeMillis()}"
                )
                val res = api.updatePopupConfig(body)
                showMessage(res.message ?: "✅ تم تحديث الواجهة المنبثقة")
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    // ===== APP UPDATE SETTINGS =====
    fun setLatestVersionCode(code: String) {
        _uiState.update { it.copy(latestVersionCode = code) }
    }

    fun setLatestVersionName(name: String) {
        _uiState.update { it.copy(latestVersionName = name) }
    }

    fun setMinRequiredVersion(minVer: String) {
        _uiState.update { it.copy(minRequiredVersion = minVer) }
    }

    fun setIsForceUpdate(force: Boolean) {
        _uiState.update { it.copy(isForceUpdate = force) }
    }

    fun setUpdateTitle(title: String) {
        _uiState.update { it.copy(updateTitle = title) }
    }

    fun setUpdateNotes(notes: String) {
        _uiState.update { it.copy(updateNotes = notes) }
    }

    fun setDownloadUrl(url: String) {
        _uiState.update { it.copy(downloadUrl = url) }
    }

    fun saveAppUpdateSettings() {
        val s = _uiState.value
        val vCode = s.latestVersionCode.toIntOrNull() ?: 1
        val minVer = s.minRequiredVersion.toIntOrNull() ?: 1

        viewModelScope.launch {
            try {
                val body = UpdateAppUpdateConfigBody(
                    latestVersionCode = vCode,
                    latestVersionName = s.latestVersionName.ifBlank { "1.0" },
                    minRequiredVersion = minVer,
                    forceUpdate = if (s.isForceUpdate) 1 else 0,
                    updateTitle = s.updateTitle.ifBlank { "🚀 تحديث جديد متوفر!" },
                    updateNotes = s.updateNotes,
                    downloadUrl = s.downloadUrl.ifBlank { "https://t.me/oror_dev" }
                )
                val res = api.updateAppUpdateConfig(body)
                showMessage(res.message ?: "✅ تم حفظ إعدادات التحديث بنجاح")
                loadAppConfig()
            } catch (e: Exception) {
                showMessage("❌ حدث خطأ: ${e.localizedMessage}")
            }
        }
    }

    fun showMessage(msg: String) {
        _uiState.update { it.copy(userMessage = msg) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
