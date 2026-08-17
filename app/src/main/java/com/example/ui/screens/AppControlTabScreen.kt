package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

@Composable
fun AppControlTabScreen(
    isMaintenanceMode: Boolean,
    onMaintenanceModeChanged: (Boolean) -> Unit,
    maintenanceMessage: String,
    onMaintenanceMessageChanged: (String) -> Unit,
    maintenanceContact: String,
    onMaintenanceContactChanged: (String) -> Unit,
    onSaveMaintenance: () -> Unit,
    
    // Popup
    isPopupEnabled: Boolean,
    onPopupEnabledChanged: (Boolean) -> Unit,
    popupTitle: String,
    onPopupTitleChanged: (String) -> Unit,
    popupImageUrl: String,
    onPopupImageUrlChanged: (String) -> Unit,
    popupMessage: String,
    onPopupMessageChanged: (String) -> Unit,
    popupButtonText: String,
    onPopupButtonTextChanged: (String) -> Unit,
    popupButtonUrl: String,
    onPopupButtonUrlChanged: (String) -> Unit,
    isPopupShowOnce: Boolean,
    onPopupShowOnceChanged: (Boolean) -> Unit,
    onSavePopup: () -> Unit,
    
    // App Update
    latestVersionCode: String,
    onLatestVersionCodeChanged: (String) -> Unit,
    latestVersionName: String,
    onLatestVersionNameChanged: (String) -> Unit,
    minRequiredVersion: String,
    onMinRequiredVersionChanged: (String) -> Unit,
    isForceUpdate: Boolean,
    onForceUpdateChanged: (Boolean) -> Unit,
    updateTitle: String,
    onUpdateTitleChanged: (String) -> Unit,
    updateNotes: String,
    onUpdateNotesChanged: (String) -> Unit,
    downloadUrl: String,
    onDownloadUrlChanged: (String) -> Unit,
    onSaveAppUpdate: () -> Unit,

    onRefresh: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(OrorCardBorder, OrorCardBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = OrorGold
                    )
                    Text(
                        text = "التحكم بالتطبيق والأنظمة",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تحديث",
                        tint = OrorTextSecondary
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrorGold)
                }
            }

            // 1. App Updates Management Card (NEW / ENHANCED)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(OrorGold.copy(alpha = 0.35f), OrorCardBorder)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.SystemUpdateAlt, contentDescription = null, tint = OrorGold, modifier = Modifier.size(20.dp))
                            Text("إدارة تحديثات التطبيق (OTA)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isForceUpdate) OrorCrimson.copy(alpha = 0.2f) else OrorGreen.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(if (isForceUpdate) OrorCrimson else OrorGreen, if (isForceUpdate) OrorCrimson else OrorGreen))
                            )
                        ) {
                            Text(
                                text = if (isForceUpdate) "تحديث إجباري" else "تحديث اختياري",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isForceUpdate) OrorCrimson else OrorGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Force Update Switch Row
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = OrorSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("فرض التحديث الإجباري", fontSize = 13.5.sp, color = OrorTextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("منع المستخدمين من فتح التطبيق بالإصدارات القديمة", fontSize = 11.sp, color = OrorTextMuted)
                            }
                            Switch(
                                checked = isForceUpdate,
                                onCheckedChange = onForceUpdateChanged,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrorCrimson,
                                    uncheckedThumbColor = OrorTextMuted,
                                    uncheckedTrackColor = OrorSurfaceVariant
                                )
                            )
                        }
                    }

                    // Version Inputs Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = latestVersionName,
                            onValueChange = onLatestVersionNameChanged,
                            label = { Text("اسم الإصدار", fontSize = 11.5.sp) },
                            placeholder = { Text("1.1", color = OrorTextMuted) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrorGold,
                                unfocusedBorderColor = OrorCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = latestVersionCode,
                            onValueChange = onLatestVersionCodeChanged,
                            label = { Text("كود الإصدار", fontSize = 11.5.sp) },
                            placeholder = { Text("2", color = OrorTextMuted) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrorGold,
                                unfocusedBorderColor = OrorCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = minRequiredVersion,
                            onValueChange = onMinRequiredVersionChanged,
                            label = { Text("الحد الأدنى", fontSize = 11.5.sp) },
                            placeholder = { Text("1", color = OrorTextMuted) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrorGold,
                                unfocusedBorderColor = OrorCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }

                    // Update Title
                    OutlinedTextField(
                        value = updateTitle,
                        onValueChange = onUpdateTitleChanged,
                        label = { Text("عنوان نافذة التحديث", fontSize = 12.sp) },
                        placeholder = { Text("🚀 تحديث جديد متوفر!", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    // Update Notes
                    OutlinedTextField(
                        value = updateNotes,
                        onValueChange = onUpdateNotesChanged,
                        label = { Text("ملاحظات التحديث وما الجديد", fontSize = 12.sp) },
                        placeholder = { Text("تم إضافة قسم الكروت وإصلاح المشاكل السابقة...", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 3
                    )

                    // Download URL
                    OutlinedTextField(
                        value = downloadUrl,
                        onValueChange = onDownloadUrlChanged,
                        label = { Text("رابط تحميل التحديث المباشر / التليجرام", fontSize = 12.sp) },
                        placeholder = { Text("https://t.me/oror_dev", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = onSaveAppUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrorGold)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ ونشر إعدادات التحديث", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // 2. Maintenance Mode Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(OrorCardBorder, OrorCardBorder)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = OrorAmber, modifier = Modifier.size(18.dp))
                        Text("وضع الصيانة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }

                    // Switch Row
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = OrorSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تفعيل وضع الصيانة", fontSize = 14.sp, color = OrorTextPrimary)
                            Switch(
                                checked = isMaintenanceMode,
                                onCheckedChange = onMaintenanceModeChanged,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrorAmber,
                                    uncheckedThumbColor = OrorTextMuted,
                                    uncheckedTrackColor = OrorSurfaceVariant
                                )
                            )
                        }
                    }

                    // Maintenance Message
                    OutlinedTextField(
                        value = maintenanceMessage,
                        onValueChange = onMaintenanceMessageChanged,
                        label = { Text("رسالة الصيانة", fontSize = 12.sp) },
                        placeholder = { Text("التطبيق في وضع الصيانة...", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 3
                    )

                    // Support Contact Link
                    OutlinedTextField(
                        value = maintenanceContact,
                        onValueChange = onMaintenanceContactChanged,
                        label = { Text("رابط الدعم الفني", fontSize = 12.sp) },
                        placeholder = { Text("https://t.me/oror_dev", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = onSaveMaintenance,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrorAmber)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ إعدادات الصيانة", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // 3. Popup Announcement Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(OrorCardBorder, OrorCardBorder)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(20.dp))
                        Text("الواجهة المنبثقة (Pop-up)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }

                    // Popup Toggle Switch Row
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = OrorSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تفعيل الإعلان المنبثق", fontSize = 14.sp, color = OrorTextPrimary)
                            Switch(
                                checked = isPopupEnabled,
                                onCheckedChange = onPopupEnabledChanged,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrorGreen,
                                    uncheckedThumbColor = OrorTextMuted,
                                    uncheckedTrackColor = OrorSurfaceVariant
                                )
                            )
                        }
                    }

                    // Popup Title
                    OutlinedTextField(
                        value = popupTitle,
                        onValueChange = onPopupTitleChanged,
                        label = { Text("عنوان الإعلان", fontSize = 12.sp) },
                        placeholder = { Text("خصم خاص على كروت الفكة!", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    // Popup Image URL
                    OutlinedTextField(
                        value = popupImageUrl,
                        onValueChange = onPopupImageUrlChanged,
                        label = { Text("رابط الصورة", fontSize = 12.sp) },
                        placeholder = { Text("https://example.com/banner.jpg", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    // Image Preview if URL provided
                    if (popupImageUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OrorSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = popupImageUrl,
                                contentDescription = "معاينة الصورة",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Popup Message Body
                    OutlinedTextField(
                        value = popupMessage,
                        onValueChange = onPopupMessageChanged,
                        label = { Text("نص الإعلان", fontSize = 12.sp) },
                        placeholder = { Text("اشترك في قناة التليجرام...", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 3
                    )

                    // Button Text and URL in a Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = popupButtonText,
                            onValueChange = onPopupButtonTextChanged,
                            label = { Text("نص الزر", fontSize = 12.sp) },
                            placeholder = { Text("انضم الآن", color = OrorTextMuted) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrorGold,
                                unfocusedBorderColor = OrorCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = popupButtonUrl,
                            onValueChange = onPopupButtonUrlChanged,
                            label = { Text("رابط الزر", fontSize = 12.sp) },
                            placeholder = { Text("https://t.me/...", color = OrorTextMuted) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrorGold,
                                unfocusedBorderColor = OrorCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }

                    // Show once toggle switch
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = OrorSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("إظهار لمرة واحدة لكل مستخدم", fontSize = 13.sp, color = OrorTextPrimary)
                            Checkbox(
                                checked = isPopupShowOnce,
                                onPopupShowOnceChanged,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = OrorGold,
                                    checkmarkColor = Color.Black
                                )
                            )
                        }
                    }

                    Button(
                        onClick = onSavePopup,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ إعدادات الإعلان", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
