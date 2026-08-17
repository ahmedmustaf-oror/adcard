package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class Stats(
    @Json(name = "total_users") val totalUsers: Long = 0,
    @Json(name = "total_points") val totalPoints: Long = 0,
    @Json(name = "pending_requests") val pendingRequests: Int = 0,
    @Json(name = "today_purchases") val todayPurchases: Int = 0,
    @Json(name = "total_purchases") val totalPurchases: Int = 0
)

@JsonClass(generateAdapter = true)
data class StatsResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "stats") val stats: Stats? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ActivationRequest(
    @Json(name = "id") val idObj: Any? = null,
    @Json(name = "username") val usernameObj: Any? = null,
    @Json(name = "phone") val phoneObj: Any? = null,
    @Json(name = "device_id") val deviceId: String? = null,
    @Json(name = "computer") val computer: String? = null,
    @Json(name = "ip") val ip: String? = null,
    @Json(name = "points") val points: Any? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "time") val timeObj: Any? = null
) {
    val id: String get() = idObj?.toString() ?: ""
    val username: String get() = usernameObj?.toString()?.takeIf { it.isNotBlank() } ?: "مستخدم"
    val phone: String get() = phoneObj?.toString() ?: ""
    val time: String get() = timeObj?.toString() ?: ""
}

@JsonClass(generateAdapter = true)
data class RequestsResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "requests") val requests: List<ActivationRequest>? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class UserItem(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "device_id") val deviceId: String = "",
    @Json(name = "username") val username: String = "",
    @Json(name = "phone") val phone: String = "",
    @Json(name = "points") val points: Long = 0,
    @Json(name = "is_active") val isActive: Boolean? = true,
    @Json(name = "expiry_date") val expiryDate: String? = null,
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class UsersResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "users") val users: List<UserItem>? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class HistoryItem(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "device_id") val deviceId: String? = null,
    @Json(name = "username") val username: String = "",
    @Json(name = "product_name") val productName: String = "",
    @Json(name = "receiver_number") val receiverNumber: String = "",
    @Json(name = "points_cost") val pointsCost: Long = 0,
    @Json(name = "remaining_points") val remainingPoints: Long = 0,
    @Json(name = "purchase_time") val purchaseTime: String = ""
)

@JsonClass(generateAdapter = true)
data class HistoryResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "history") val history: List<HistoryItem>? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class NotificationItem(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "title") val title: String = "",
    @Json(name = "message") val message: String = "",
    @Json(name = "type") val type: String? = null,
    @Json(name = "target_user") val targetUser: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "time") val time: String? = null
) {
    val rawId: String
        get() = id?.toString() ?: "${title}_${createdAt ?: time ?: ""}"
}

@JsonClass(generateAdapter = true)
data class NotificationsResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "notifications") val notifications: List<Any>? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class AppConfig(
    @Json(name = "maintenance_mode") val maintenanceMode: Any? = null,
    @Json(name = "maintenance_message") val maintenanceMessage: String? = null,
    @Json(name = "maintenance_contact") val maintenanceContact: String? = null
) {
    val isMaintenanceEnabled: Boolean
        get() = when (maintenanceMode) {
            is Boolean -> maintenanceMode
            is Number -> maintenanceMode.toInt() == 1
            is String -> maintenanceMode == "1" || maintenanceMode.equals("true", ignoreCase = true)
            else -> false
        }
}

@JsonClass(generateAdapter = true)
data class PopupConfig(
    @Json(name = "enabled") val enabled: Any? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "button_text") val buttonText: String? = null,
    @Json(name = "button_url") val buttonUrl: String? = null,
    @Json(name = "show_once") val showOnce: Any? = null,
    @Json(name = "id") val id: String? = null
) {
    val isPopupEnabled: Boolean
        get() = when (enabled) {
            is Boolean -> enabled
            is Number -> enabled.toInt() == 1
            is String -> enabled == "1" || enabled.equals("true", ignoreCase = true)
            else -> false
        }

    val isShowOnceEnabled: Boolean
        get() = when (showOnce) {
            is Boolean -> showOnce
            is Number -> showOnce.toInt() == 1
            is String -> showOnce == "1" || showOnce.equals("true", ignoreCase = true)
            else -> false
        }
}

@JsonClass(generateAdapter = true)
data class AppUpdateConfig(
    @Json(name = "latest_version_code") val latestVersionCode: Any? = 1,
    @Json(name = "latest_version_name") val latestVersionName: String? = "1.0",
    @Json(name = "version_code") val versionCodeAlt: Any? = null,
    @Json(name = "version_name") val versionNameAlt: String? = null,
    @Json(name = "min_required_version") val minRequiredVersion: Any? = 1,
    @Json(name = "min_version") val minVersionAlt: Any? = null,
    @Json(name = "force_update") val forceUpdate: Any? = 0,
    @Json(name = "is_force") val isForceAlt: Any? = null,
    @Json(name = "update_title") val updateTitle: String? = null,
    @Json(name = "title") val titleAlt: String? = null,
    @Json(name = "update_notes") val updateNotes: String? = null,
    @Json(name = "notes") val notesAlt: String? = null,
    @Json(name = "download_url") val downloadUrl: String? = null,
    @Json(name = "url") val urlAlt: String? = null,
    @Json(name = "apk_url") val apkUrlAlt: String? = null
) {
    val versionCodeInt: Int
        get() = when (val v = latestVersionCode ?: versionCodeAlt) {
            is Number -> v.toInt()
            is String -> v.toIntOrNull() ?: 1
            else -> 1
        }
    val versionNameString: String
        get() = latestVersionName ?: versionNameAlt ?: "1.0"

    val minVersionInt: Int
        get() = when (val m = minRequiredVersion ?: minVersionAlt) {
            is Number -> m.toInt()
            is String -> m.toIntOrNull() ?: 1
            else -> 1
        }
    val isForceUpdate: Boolean
        get() = when (val f = forceUpdate ?: isForceAlt) {
            is Boolean -> f
            is Number -> f.toInt() == 1
            is String -> f == "1" || f.equals("true", ignoreCase = true)
            else -> false
        }
    val directDownloadUrl: String
        get() = downloadUrl?.takeIf { it.isNotBlank() }
            ?: urlAlt?.takeIf { it.isNotBlank() }
            ?: apkUrlAlt?.takeIf { it.isNotBlank() }
            ?: "https://t.me/oror_dev"

    val effectiveTitle: String
        get() = updateTitle?.takeIf { it.isNotBlank() }
            ?: titleAlt?.takeIf { it.isNotBlank() }
            ?: "🚀 تحديث جديد متوفر!"

    val effectiveNotes: String
        get() = updateNotes?.takeIf { it.isNotBlank() }
            ?: notesAlt?.takeIf { it.isNotBlank() }
            ?: ""
}

@JsonClass(generateAdapter = true)
data class AppConfigResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "config") val config: AppConfig? = null,
    @Json(name = "popup") val popup: PopupConfig? = null,
    @Json(name = "update") val update: AppUpdateConfig? = null,
    @Json(name = "message") val message: String? = null
)

// Request Bodies
@JsonClass(generateAdapter = true)
data class ApproveRequestBody(
    @Json(name = "request_id") val requestId: String,
    @Json(name = "points") val points: Int,
    @Json(name = "expiry") val expiry: String = "forever"
)

@JsonClass(generateAdapter = true)
data class RejectRequestBody(
    @Json(name = "request_id") val requestId: String
)

@JsonClass(generateAdapter = true)
data class ManagePointsBody(
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "points") val points: Int
)

@JsonClass(generateAdapter = true)
data class UpdateExpiryBody(
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "expiry_date") val expiryDate: String
)

@JsonClass(generateAdapter = true)
data class DeleteUserBody(
    @Json(name = "device_id") val deviceId: String
)

@JsonClass(generateAdapter = true)
data class SendNotificationBody(
    @Json(name = "target_user") val targetUser: String,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String,
    @Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class DeleteNotificationBody(
    @Json(name = "notification_id") val notificationId: String
)

@JsonClass(generateAdapter = true)
data class TargetDeviceBody(
    @Json(name = "device_id") val deviceId: String = "admin"
)

@JsonClass(generateAdapter = true)
data class UpdateAppConfigBody(
    @Json(name = "maintenance_mode") val maintenanceMode: Int,
    @Json(name = "maintenance_message") val maintenanceMessage: String,
    @Json(name = "maintenance_contact") val maintenanceContact: String
)

@JsonClass(generateAdapter = true)
data class UpdatePopupConfigBody(
    @Json(name = "enabled") val enabled: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "message") val message: String,
    @Json(name = "button_text") val buttonText: String,
    @Json(name = "button_url") val buttonUrl: String,
    @Json(name = "show_once") val showOnce: Int,
    @Json(name = "id") val id: String
)

@JsonClass(generateAdapter = true)
data class UpdateAppUpdateConfigBody(
    @Json(name = "latest_version_code") val latestVersionCode: Int,
    @Json(name = "latest_version_name") val latestVersionName: String,
    @Json(name = "min_required_version") val minRequiredVersion: Int,
    @Json(name = "force_update") val forceUpdate: Int,
    @Json(name = "update_title") val updateTitle: String,
    @Json(name = "update_notes") val updateNotes: String,
    @Json(name = "download_url") val downloadUrl: String
)
