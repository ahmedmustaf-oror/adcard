package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NotificationItem
import com.example.data.UserItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsTabScreen(
    notifications: List<NotificationItem>,
    isLoading: Boolean,
    users: List<UserItem>,
    targetUser: String,
    onTargetUserChanged: (String) -> Unit,
    title: String,
    onTitleChanged: (String) -> Unit,
    type: String,
    onTypeChanged: (String) -> Unit,
    message: String,
    onMessageChanged: (String) -> Unit,
    onSendNotification: () -> Unit,
    onDeleteNotification: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var targetExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val typeOptions = listOf(
        "info" to "ℹ️ معلومات",
        "success" to "✅ نجاح",
        "warning" to "⚠️ تنبيه",
        "alert" to "🛑 عاجل"
    )

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = OrorGold
                    )
                    Text(
                        text = "إدارة الإشعارات",
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

            // New Notification Form Card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = OrorGold, modifier = Modifier.size(18.dp))
                        Text("إرسال إشعار جديد", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }

                    // Target Selector
                    ExposedDropdownMenuBox(
                        expanded = targetExpanded,
                        onExpandedChange = { targetExpanded = !targetExpanded }
                    ) {
                        val selectedLabel = if (targetUser == "all") "📢 جميع المستخدمين" else {
                            val u = users.find { it.deviceId == targetUser }
                            if (u != null) "👤 ${u.username}" else targetUser
                        }

                        OutlinedTextField(
                            value = selectedLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المستهدف", fontSize = 12.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrorGold,
                                unfocusedBorderColor = OrorCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = targetExpanded,
                            onDismissRequest = { targetExpanded = false },
                            modifier = Modifier.background(OrorSurfaceVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("📢 جميع المستخدمين", color = Color.White) },
                                onClick = {
                                    onTargetUserChanged("all")
                                    targetExpanded = false
                                }
                            )
                            users.take(50).forEach { user ->
                                DropdownMenuItem(
                                    text = { Text("👤 ${user.username}", color = Color.White) },
                                    onClick = {
                                        onTargetUserChanged(user.deviceId)
                                        targetExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChanged,
                        label = { Text("عنوان الإشعار", fontSize = 12.sp) },
                        placeholder = { Text("مثال: عرض خاص اليوم", color = OrorTextMuted) },
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

                    // Notification Type Selector
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded }
                    ) {
                        val currentTypeLabel = typeOptions.find { it.first == type }?.second ?: "ℹ️ معلومات"

                        OutlinedTextField(
                            value = currentTypeLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("نوع التنبيه", fontSize = 12.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrorGold,
                                unfocusedBorderColor = OrorCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false },
                            modifier = Modifier.background(OrorSurfaceVariant)
                        ) {
                            typeOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.second, color = Color.White) },
                                    onClick = {
                                        onTypeChanged(opt.first)
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Message Textarea
                    OutlinedTextField(
                        value = message,
                        onValueChange = onMessageChanged,
                        label = { Text("نص الرسالة", fontSize = 12.sp) },
                        placeholder = { Text("اكتب نص الإشعار هنا...", color = OrorTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 4
                    )

                    Button(
                        onClick = onSendNotification,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrorGold)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نشر الإشعار", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Active Notifications List
            Text("الإشعارات النشطة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrorGold)
                }
            } else if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد إشعارات نشطة", color = OrorTextMuted, fontSize = 13.sp)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    notifications.forEach { notif ->
                        NotificationCard(
                            notification = notif,
                            onDelete = { onDeleteNotification(notif.rawId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(OrorCardBorder, OrorCardBorder)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = OrorTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${notification.createdAt ?: notification.time ?: ""} · ${if (notification.targetUser == "all") "الكل" else "مستخدم محدد"}",
                    fontSize = 10.sp,
                    color = OrorTextMuted
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = OrorCrimson)
            }
        }
    }
}
