package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivationRequest
import com.example.ui.theme.*
import com.example.util.TimeUtils

@Composable
fun RequestsTabScreen(
    requests: List<ActivationRequest>,
    isLoading: Boolean,
    pointsMap: Map<String, String>,
    onPointsChanged: (String, String) -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(OrorGold.copy(alpha = 0.4f), OrorCardBorder, OrorSurfaceVariant)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Toolbar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(OrorGold.copy(alpha = 0.15f))
                            .border(1.dp, OrorGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = OrorGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "طلبات التفعيل",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (requests.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = OrorCrimson.copy(alpha = 0.25f),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(listOf(OrorCrimson, OrorCrimson))
                                    )
                                ) {
                                    Text(
                                        text = "${requests.size} قيد الانتظار",
                                        color = Color(0xFFFF7A8A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "مراجعة وتفعيل حسابات الأعضاء الجدد",
                            fontSize = 12.sp,
                            color = OrorTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x15FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تحديث",
                        tint = OrorGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrorGold, strokeWidth = 3.dp)
                }
            } else if (requests.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0x10FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = OrorGold.copy(alpha = 0.7f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "لا توجد طلبات معلقة حالياً",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ستظهر هنا أي طلبات تفعيل جديدة يرسلها المستخدمون تلقائياً",
                        color = OrorTextMuted,
                        fontSize = 12.sp
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    requests.forEachIndexed { index, req ->
                        RequestCard(
                            request = req,
                            index = index + 1,
                            pointsInput = pointsMap[req.id] ?: "0",
                            onPointsChanged = { onPointsChanged(req.id, it) },
                            onApprove = { onApprove(req.id) },
                            onReject = { onReject(req.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCard(
    request: ActivationRequest,
    index: Int,
    pointsInput: String,
    onPointsChanged: (String) -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(OrorGold.copy(alpha = 0.35f), OrorCardBorder, Color(0x30FFFFFF))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Avatar initial, Username, Phone Pill & Index Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar Badge
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(OrorGold.copy(alpha = 0.3f), OrorAmber.copy(alpha = 0.5f))
                                )
                            )
                            .border(1.5.dp, OrorGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.username.take(1).uppercase(),
                            color = OrorGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Column {
                        Text(
                            text = request.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = OrorGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = request.phone.ifBlank { "بدون هاتف" },
                                fontSize = 13.sp,
                                color = OrorGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Index Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x18FFFFFF),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(Color(0x30FFFFFF), Color(0x30FFFFFF)))
                    )
                ) {
                    Text(
                        text = "#$index",
                        color = OrorTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0x15FFFFFF)
            )

            // Info Details Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x15000000))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItemRow(
                        icon = Icons.Default.PhoneAndroid,
                        label = "الجهاز:",
                        value = request.computer ?: "غير محدد"
                    )
                    InfoItemRow(
                        icon = Icons.Default.Language,
                        label = "IP:",
                        value = request.ip ?: "-"
                    )
                }

                if (!request.deviceId.isNullOrBlank()) {
                    InfoItemRow(
                        icon = Icons.Default.Fingerprint,
                        label = "Device ID:",
                        value = request.deviceId
                    )
                }

                InfoItemRow(
                    icon = Icons.Default.Schedule,
                    label = "التاريخ:",
                    value = TimeUtils.formatDisplayTime(request.time, request.id)
                )
            }

            // Quick Points Pills Selector
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val currentPts = pointsInput.trim().toIntOrNull() ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ اختر النقاط الممنوحة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrorTextSecondary
                    )
                    Text(
                        text = if (currentPts == 0) "المحدد: بدون نقاط" else "المحدد: $currentPts نقطة",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentPts == 0) Color(0xFF64B5F6) else OrorGold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val options = listOf(
                        "0" to "0 (بدون)",
                        "30" to "+30",
                        "50" to "+50",
                        "100" to "+100",
                        "200" to "+200"
                    )

                    options.forEach { (ptsVal, label) ->
                        val isSelected = pointsInput.trim() == ptsVal || (ptsVal == "0" && (pointsInput.isBlank() || pointsInput.trim() == "0"))
                        Box(
                            modifier = Modifier
                                .weight(if (ptsVal == "0") 1.2f else 1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) OrorGold.copy(alpha = 0.25f)
                                    else Color(0x18FFFFFF)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) OrorGold else Color(0x35FFFFFF),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onPointsChanged(ptsVal) }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) OrorGold else Color.White
                            )
                        }
                    }
                }
            }

            // Points Input & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = pointsInput,
                    onValueChange = onPointsChanged,
                    label = { Text("النقاط (0 = بدون نقاط)", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = OrorGold,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrorGold,
                        unfocusedBorderColor = OrorCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0x10000000),
                        unfocusedContainerColor = Color(0x10000000)
                    ),
                    singleLine = true
                )

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = OrorGreen),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("قبول", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = OrorCrimson),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    modifier = Modifier.weight(0.8f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("رفض", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun InfoItemRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrorTextSecondary,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = OrorTextSecondary
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
