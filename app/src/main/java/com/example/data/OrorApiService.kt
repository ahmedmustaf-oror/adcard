package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface OrorApiService {

    @POST("api.php")
    suspend fun getStats(
        @Query("action") action: String = "get_stats"
    ): StatsResponse

    @POST("api.php")
    suspend fun getRequests(
        @Query("action") action: String = "get_requests"
    ): RequestsResponse

    @POST("api.php")
    suspend fun approveRequest(
        @Body body: ApproveRequestBody,
        @Query("action") action: String = "approve_request"
    ): BaseResponse

    @POST("api.php")
    suspend fun rejectRequest(
        @Body body: RejectRequestBody,
        @Query("action") action: String = "reject_request"
    ): BaseResponse

    @POST("api.php")
    suspend fun getUsers(
        @Query("action") action: String = "get_users"
    ): UsersResponse

    @POST("api.php")
    suspend fun addPoints(
        @Body body: ManagePointsBody,
        @Query("action") action: String = "add_points"
    ): BaseResponse

    @POST("api.php")
    suspend fun removePoints(
        @Body body: ManagePointsBody,
        @Query("action") action: String = "remove_points"
    ): BaseResponse

    @POST("api.php")
    suspend fun deleteUser(
        @Body body: DeleteUserBody,
        @Query("action") action: String = "delete_user"
    ): BaseResponse

    @POST("api.php")
    suspend fun getHistory(
        @Query("action") action: String = "get_history"
    ): HistoryResponse

    @POST("api.php")
    suspend fun getNotifications(
        @Body body: TargetDeviceBody = TargetDeviceBody("admin"),
        @Query("action") action: String = "get_notifications"
    ): NotificationsResponse

    @POST("api.php")
    suspend fun sendNotification(
        @Body body: SendNotificationBody,
        @Query("action") action: String = "send_notification"
    ): BaseResponse

    @POST("api.php")
    suspend fun deleteNotification(
        @Body body: DeleteNotificationBody,
        @Query("action") action: String = "delete_notification"
    ): BaseResponse

    @POST("api.php")
    suspend fun updateExpiry(
        @Body body: UpdateExpiryBody,
        @Query("action") action: String = "update_expiry"
    ): BaseResponse

    @POST("api.php")
    suspend fun getAppConfig(
        @Query("action") action: String = "get_app_config"
    ): AppConfigResponse

    @POST("api.php")
    suspend fun updateAppConfig(
        @Body body: UpdateAppConfigBody,
        @Query("action") action: String = "update_app_config"
    ): BaseResponse

    @POST("api.php")
    suspend fun updatePopupConfig(
        @Body body: UpdatePopupConfigBody,
        @Query("action") action: String = "update_popup_config"
    ): BaseResponse

    @POST("api.php")
    suspend fun updateAppUpdateConfig(
        @Body body: UpdateAppUpdateConfigBody,
        @Query("action") action: String = "update_app_update_config"
    ): BaseResponse

    companion object {
        private const val BASE_URL = "https://elias555.serv00.net/dashcard2/api/"

        fun create(): OrorApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OrorApiService::class.java)
        }
    }
}
