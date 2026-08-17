package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@Composable
fun UsersTabScreen(
    users: List<UserItem>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    currentPage: Int,
    usersPerPage: Int,
    onPageSelected: (Int) -> Unit,
    onOpenPointsModal: (UserItem) -> Unit,
    onOpenExpiryModal: (UserItem) -> Unit,
    onDeleteUser: (UserItem) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalUsers = users.size
    val totalPages = if (totalUsers == 0) 1 else ceil(totalUsers.toDouble() / usersPerPage).toInt()
    val safePage = currentPage.coerceIn(1, totalPages)

    val startIndex = (safePage - 1) * usersPerPage
    val endIndex = (startIndex + usersPerPage).coerceAtMost(totalUsers)
    val pageUsers = if (startIndex < totalUsers) users.subList(startIndex, endIndex) else emptyList()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(OrorCardBorder, OrorCardBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header & Search toolbar
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = OrorGold
                        )
                        Text(
                            text = "قائمة المستخدمين",
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

                // Search box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    placeholder = { Text("بحث بالاسم أو الهاتف...", fontSize = 13.sp, color = OrorTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrorTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح", tint = OrorTextSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(40.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrorGold,
                        unfocusedBorderColor = OrorCardBorder,
                        focusedContainerColor = OrorSurfaceVariant,
                        unfocusedContainerColor = OrorSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrorGold)
                }
            } else if (users.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا يوجد مستخدمين", color = OrorTextMuted, fontSize = 14.sp)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    pageUsers.forEachIndexed { index, user ->
                        val globalIndex = startIndex + index + 1
                        UserCard(
                            indexNumber = globalIndex,
                            user = user,
                            onPointsClick = { onOpenPointsModal(user) },
                            onExpiryClick = { onOpenExpiryModal(user) },
                            onDeleteClick = { onDeleteUser(user) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pagination
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onPageSelected(1) },
                        enabled = safePage > 1
                    ) {
                        Icon(Icons.Default.FirstPage, contentDescription = "الأولى", tint = if (safePage > 1) OrorGold else OrorTextMuted)
                    }

                    IconButton(
                        onClick = { onPageSelected(safePage - 1) },
                        enabled = safePage > 1
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "السابق", tint = if (safePage > 1) Color.White else OrorTextMuted)
                    }

                    Text(
                        text = "$safePage / $totalPages ($totalUsers)",
                        fontSize = 13.sp,
                        color = OrorTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onPageSelected(safePage + 1) },
                        enabled = safePage < totalPages
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "التالي", tint = if (safePage < totalPages) Color.White else OrorTextMuted)
                    }

                    IconButton(
                        onClick = { onPageSelected(totalPages) },
                        enabled = safePage < totalPages
                    ) {
                        Icon(Icons.Default.LastPage, contentDescription = "الأخيرة", tint = if (safePage < totalPages) OrorGold else OrorTextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    indexNumber: Int,
    user: UserItem,
    onPointsClick: () -> Unit,
    onExpiryClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val expiryText = when (val exp = user.expiryDate) {
        null, "", "forever", "مفتوح" -> "♾️ صلاحية مفتوحة"
        else -> "📅 حتى $exp"
    }

    val isExpired = try {
        val exp = user.expiryDate
        if (exp != null && exp != "forever" && exp != "مفتوح" && exp.isNotBlank()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(exp)
            date != null && date.before(Date())
        } else false
    } catch (e: Exception) {
        false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(OrorCardBorder, OrorCardBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x26FFD700),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("$indexNumber", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrorGold)
                        }
                    }

                    Column {
                        Text(
                            text = user.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = user.phone,
                            fontSize = 12.sp,
                            color = OrorTextSecondary
                        )
                    }
                }

                // Points Display
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${user.points}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = OrorGold
                    )
                    Text("نقطة", fontSize = 11.sp, color = OrorTextMuted)
                }
            }

            HorizontalDivider(color = OrorCardBorder.copy(alpha = 0.5f), thickness = 0.8.dp)

            // Expiry and Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expiry Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isExpired) OrorCrimson.copy(alpha = 0.15f) else Color(0x1834D399),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                if (isExpired) OrorCrimson.copy(alpha = 0.4f) else OrorGreen.copy(alpha = 0.4f),
                                if (isExpired) OrorCrimson.copy(alpha = 0.4f) else OrorGreen.copy(alpha = 0.4f)
                            )
                        )
                    )
                ) {
                    Text(
                        text = if (isExpired) "⚠️ منتهي الصلاحية" else expiryText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isExpired) OrorCrimson else OrorGreen,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onExpiryClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("صلاحية", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = onPointsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("نقاط", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = OrorCrimson),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("حذف", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpiryDialog(
    user: UserItem,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedPreset by remember { mutableStateOf("forever") }
    var customDate by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val now = Calendar.getInstance()

    fun calculateFutureDate(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return sdf.format(cal.time)
    }

    val presets = listOf(
        Pair("forever", "♾️ مفتوح دائماً"),
        Pair(calculateFutureDate(7), "📅 أسبوع (7 أيام)"),
        Pair(calculateFutureDate(30), "📅 شهر (30 يوم)"),
        Pair(calculateFutureDate(90), "📅 3 شهور"),
        Pair(calculateFutureDate(180), "📅 6 شهور"),
        Pair(calculateFutureDate(365), "📅 سنة (365 يوم)"),
        Pair("custom", "✍️ تاريخ مخصص")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = OrorGold)
                Text(
                    text = "تعديل صلاحية تفعيل الحساب",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "المستخدم: ${user.username} (${user.phone})",
                    fontSize = 13.sp,
                    color = OrorTextSecondary
                )

                Text(
                    text = "اختر مدة التفعيل المطلوبة:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                presets.forEach { (value, label) ->
                    val isSelected = if (value == "custom") selectedPreset == "custom" else selectedPreset == value
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) OrorGold.copy(alpha = 0.2f) else OrorSurfaceVariant,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                listOf(if (isSelected) OrorGold else OrorCardBorder, if (isSelected) OrorGold else OrorCardBorder)
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPreset = value }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) OrorGold else Color.White
                            )
                            if (value != "forever" && value != "custom") {
                                Text(
                                    text = value,
                                    fontSize = 11.sp,
                                    color = OrorTextMuted
                                )
                            }
                        }
                    }
                }

                if (selectedPreset == "custom") {
                    OutlinedTextField(
                        value = customDate,
                        onValueChange = { customDate = it },
                        label = { Text("أدخل التاريخ (YYYY-MM-DD)", fontSize = 11.sp) },
                        placeholder = { Text("2026-12-31", color = OrorTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrorGold,
                            unfocusedBorderColor = OrorCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalExpiry = if (selectedPreset == "custom") customDate.trim() else selectedPreset
                    if (finalExpiry.isNotBlank()) {
                        onConfirm(finalExpiry)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrorGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("حفظ التفعيل", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = OrorTextMuted)
            }
        },
        containerColor = OrorSurface,
        shape = RoundedCornerShape(18.dp)
    )
}
