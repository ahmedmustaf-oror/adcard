package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.service.RequestsMonitoringService
import com.example.ui.MainViewModel
import com.example.ui.components.HeaderSection
import com.example.ui.components.StatsGrid
import com.example.ui.components.TabNavigationRow
import com.example.ui.dialogs.PointsDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OrorBackground
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create system notification channel
        NotificationHelper.createNotificationChannel(this)

        // Request notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Start background monitoring service so notifications work even when app is closed
        try {
            RequestsMonitoringService.start(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val context = LocalContext.current
                    val uiState by viewModel.uiState.collectAsState()

                    // Toast message handler
                    LaunchedEffect(uiState.userMessage) {
                        uiState.userMessage?.let { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            viewModel.clearUserMessage()
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = OrorBackground
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(OrorBackground)
                                .padding(innerPadding)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Header Section
                                HeaderSection(
                                    isMaintenance = uiState.isMaintenanceMode,
                                    currentTime = uiState.currentTimeString
                                )

                                // 2. Quick Stats Grid
                                StatsGrid(
                                    totalUsers = uiState.stats.totalUsers,
                                    totalPoints = uiState.stats.totalPoints,
                                    pendingRequests = uiState.stats.pendingRequests,
                                    todayPurchases = uiState.todayPurchases,
                                    totalPurchases = uiState.totalPurchases
                                )

                                // 3. Tab Navigation Row
                                TabNavigationRow(
                                    selectedTab = uiState.activeTab,
                                    pendingRequestsCount = uiState.requests.size,
                                    onTabSelected = { viewModel.selectTab(it) }
                                )

                                // 4. Active Tab Content Screen
                                AnimatedContent(
                                    targetState = uiState.activeTab,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "tabTransition"
                                ) { tab ->
                                    when (tab) {
                                        0 -> RequestsTabScreen(
                                            requests = uiState.requests,
                                            isLoading = uiState.isLoadingRequests,
                                            pointsMap = uiState.requestPointsMap,
                                            onPointsChanged = { id, pts -> viewModel.updateRequestPointsInput(id, pts) },
                                            onApprove = { viewModel.approveRequest(it) },
                                            onReject = { viewModel.rejectRequest(it) },
                                            onRefresh = { viewModel.loadRequests() }
                                        )

                                        1 -> UsersTabScreen(
                                            users = uiState.filteredUsers,
                                            isLoading = uiState.isLoadingUsers,
                                            searchQuery = uiState.userSearchQuery,
                                            onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                                            currentPage = uiState.currentPage,
                                            usersPerPage = uiState.usersPerPage,
                                            onPageSelected = { viewModel.setPage(it) },
                                            onOpenPointsModal = { viewModel.openPointsModal(it) },
                                            onOpenExpiryModal = { viewModel.openExpiryModal(it) },
                                            onDeleteUser = { viewModel.deleteUser(it) },
                                            onRefresh = { viewModel.loadUsers() }
                                        )

                                        2 -> HistoryTabScreen(
                                            history = uiState.history,
                                            isLoading = uiState.isLoadingHistory,
                                            onRefresh = { viewModel.loadHistory() }
                                        )

                                        3 -> NotificationsTabScreen(
                                            notifications = uiState.notifications,
                                            isLoading = uiState.isLoadingNotifications,
                                            users = uiState.users,
                                            targetUser = uiState.notifTarget,
                                            onTargetUserChanged = { viewModel.setNotifTarget(it) },
                                            title = uiState.notifTitle,
                                            onTitleChanged = { viewModel.setNotifTitle(it) },
                                            type = uiState.notifType,
                                            onTypeChanged = { viewModel.setNotifType(it) },
                                            message = uiState.notifMessage,
                                            onMessageChanged = { viewModel.setNotifMessage(it) },
                                            onSendNotification = { viewModel.sendNotification() },
                                            onDeleteNotification = { viewModel.deleteNotification(it) },
                                            onRefresh = { viewModel.loadNotifications() }
                                        )

                                        4 -> AppControlTabScreen(
                                            isMaintenanceMode = uiState.isMaintenanceMode,
                                            onMaintenanceModeChanged = { viewModel.setMaintenanceMode(it) },
                                            maintenanceMessage = uiState.maintenanceMessage,
                                            onMaintenanceMessageChanged = { viewModel.setMaintenanceMessage(it) },
                                            maintenanceContact = uiState.maintenanceContact,
                                            onMaintenanceContactChanged = { viewModel.setMaintenanceContact(it) },
                                            onSaveMaintenance = { viewModel.saveMaintenanceSettings() },
                                            isPopupEnabled = uiState.isPopupEnabled,
                                            onPopupEnabledChanged = { viewModel.setPopupEnabled(it) },
                                            popupTitle = uiState.popupTitle,
                                            onPopupTitleChanged = { viewModel.setPopupTitle(it) },
                                            popupImageUrl = uiState.popupImageUrl,
                                            onPopupImageUrlChanged = { viewModel.setPopupImageUrl(it) },
                                            popupMessage = uiState.popupMessage,
                                            onPopupMessageChanged = { viewModel.setPopupMessage(it) },
                                            popupButtonText = uiState.popupButtonText,
                                            onPopupButtonTextChanged = { viewModel.setPopupButtonText(it) },
                                            popupButtonUrl = uiState.popupButtonUrl,
                                            onPopupButtonUrlChanged = { viewModel.setPopupButtonUrl(it) },
                                            isPopupShowOnce = uiState.isPopupShowOnce,
                                            onPopupShowOnceChanged = { viewModel.setPopupShowOnce(it) },
                                            onSavePopup = { viewModel.savePopupSettings() },
                                            
                                            // App Updates
                                            latestVersionCode = uiState.latestVersionCode,
                                            onLatestVersionCodeChanged = { viewModel.setLatestVersionCode(it) },
                                            latestVersionName = uiState.latestVersionName,
                                            onLatestVersionNameChanged = { viewModel.setLatestVersionName(it) },
                                            minRequiredVersion = uiState.minRequiredVersion,
                                            onMinRequiredVersionChanged = { viewModel.setMinRequiredVersion(it) },
                                            isForceUpdate = uiState.isForceUpdate,
                                            onForceUpdateChanged = { viewModel.setIsForceUpdate(it) },
                                            updateTitle = uiState.updateTitle,
                                            onUpdateTitleChanged = { viewModel.setUpdateTitle(it) },
                                            updateNotes = uiState.updateNotes,
                                            onUpdateNotesChanged = { viewModel.setUpdateNotes(it) },
                                            downloadUrl = uiState.downloadUrl,
                                            onDownloadUrlChanged = { viewModel.setDownloadUrl(it) },
                                            onSaveAppUpdate = { viewModel.saveAppUpdateSettings() },

                                            onRefresh = { viewModel.loadAppConfig() },
                                            isLoading = uiState.isLoadingConfig
                                        )
                                    }
                                }
                            }

                            // Modals / Dialogs
                            uiState.pointsDialogUser?.let { user ->
                                PointsDialog(
                                    user = user,
                                    onDismiss = { viewModel.closePointsModal() },
                                    onManagePoints = { isAdd, amount -> viewModel.manageUserPoints(isAdd, amount) }
                                )
                            }

                            uiState.expiryDialogUser?.let { user ->
                                ExpiryDialog(
                                    user = user,
                                    onDismiss = { viewModel.closeExpiryModal() },
                                    onConfirm = { expiryDate -> viewModel.updateUserExpiry(expiryDate) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
