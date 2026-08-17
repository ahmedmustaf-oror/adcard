package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.HistoryItem
import com.example.ui.theme.*
import com.example.util.TimeUtils
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTabScreen(
    history: List<HistoryItem>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isNewestFirst by remember { mutableStateOf(true) }
    var pageSize by remember { mutableIntStateOf(15) }
    var currentPage by remember { mutableIntStateOf(1) }

    // Reset to page 1 whenever search query or history or sorting changes
    LaunchedEffect(searchQuery, history.size, isNewestFirst, pageSize) {
        currentPage = 1
    }

    // Sort order: Newest first (history.reversed()) or Oldest first (history)
    val sortedHistory = remember(history, isNewestFirst) {
        if (isNewestFirst) history.reversed() else history
    }

    val filteredHistory = remember(sortedHistory, searchQuery) {
        if (searchQuery.isBlank()) {
            sortedHistory
        } else {
            val q = searchQuery.trim().lowercase()
            sortedHistory.filter { item ->
                item.username.lowercase().contains(q) ||
                item.productName.lowercase().contains(q) ||
                item.receiverNumber.lowercase().contains(q) ||
                item.purchaseTime.lowercase().contains(q)
            }
        }
    }

    val totalItems = filteredHistory.size
    val totalPages = maxOf(1, ceil(totalItems.toDouble() / pageSize).toInt())
    val safePage = currentPage.coerceIn(1, totalPages)

    val startIndex = (safePage - 1) * pageSize
    val endIndex = minOf(startIndex + pageSize, totalItems)
    val currentPageItems = remember(filteredHistory, safePage, pageSize) {
        if (startIndex < totalItems) {
            filteredHistory.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    val totalPointsDeducted = remember(history) {
        history.sumOf { it.pointsCost }
    }

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
            // Header Toolbar
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
                            .clip(CircleShape)
                            .background(OrorGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = OrorGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "سجل عمليات الشحن",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "منظم بالصفحات (الأحدث في الأعلى)",
                            fontSize = 11.sp,
                            color = OrorTextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(OrorSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تحديث",
                        tint = OrorGold
                    )
                }
            }

            // Summary Stats Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stat 1: Total Operations Count
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = OrorGold, modifier = Modifier.size(20.dp))
                        Column {
                            Text("إجمالي الشحنات", fontSize = 10.sp, color = OrorTextMuted)
                            Text("${history.size} عملية", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Stat 2: Total Points Spent
                Card(
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = OrorAmber, modifier = Modifier.size(20.dp))
                        Column {
                            Text("إجمالي النقاط الخصم", fontSize = 10.sp, color = OrorTextMuted)
                            Text("$totalPointsDeducted نقطة", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OrorAmber)
                        }
                    }
                }
            }

            // Search Bar & Sort Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث باسم المستخدم، المنتج، أو رقم الهاتف...", fontSize = 12.sp, color = OrorTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrorGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = OrorTextMuted)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
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

                // Order Toggle Button
                IconButton(
                    onClick = { isNewestFirst = !isNewestFirst },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isNewestFirst) OrorGold.copy(alpha = 0.2f) else OrorSurfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isNewestFirst) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = if (isNewestFirst) "الأحدث أولاً" else "الأقدم أولاً",
                        tint = if (isNewestFirst) OrorGold else Color.White
                    )
                }
            }

            // Controls Row: Items Per Page & Current Page Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Size Selector Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("العرض:", fontSize = 11.sp, color = OrorTextMuted)
                    listOf(15, 30, 50).forEach { size ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (pageSize == size) OrorGold else OrorSurfaceVariant)
                                .clickable { pageSize = size }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$size",
                                fontSize = 11.sp,
                                fontWeight = if (pageSize == size) FontWeight.Bold else FontWeight.Normal,
                                color = if (pageSize == size) Color.Black else Color.White
                            )
                        }
                    }
                }

                // Range Text Indicator
                if (totalItems > 0) {
                    Text(
                        text = "عرض ${startIndex + 1}-$endIndex من $totalItems",
                        fontSize = 11.sp,
                        color = OrorTextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Top Pagination Bar
            if (totalPages > 1) {
                PaginationBar(
                    currentPage = safePage,
                    totalPages = totalPages,
                    onPageChange = { currentPage = it }
                )
            }

            // History List
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrorGold)
                }
            } else if (filteredHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = OrorTextMuted, modifier = Modifier.size(40.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "لا توجد نتائج مطابقة للبحث" else "لا توجد عمليات شحن مسجلة بعد",
                            color = OrorTextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currentPageItems.forEachIndexed { idx, item ->
                        val globalIndex = if (isNewestFirst) (totalItems - (startIndex + idx)) else (startIndex + idx + 1)
                        HistoryCard(
                            indexNumber = globalIndex,
                            historyItem = item
                        )
                    }
                }
            }

            // Bottom Pagination Bar
            if (totalPages > 1) {
                Spacer(modifier = Modifier.height(4.dp))
                PaginationBar(
                    currentPage = safePage,
                    totalPages = totalPages,
                    onPageChange = { currentPage = it }
                )
            }
        }
    }
}

@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPageChange(1) },
                enabled = currentPage > 1
            ) {
                Icon(
                    imageVector = Icons.Default.FirstPage,
                    contentDescription = "الأولى",
                    tint = if (currentPage > 1) OrorGold else OrorTextMuted
                )
            }

            IconButton(
                onClick = { onPageChange(currentPage - 1) },
                enabled = currentPage > 1
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "السابق",
                    tint = if (currentPage > 1) Color.White else OrorTextMuted
                )
            }

            // Current Page Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(OrorGold.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "صفحة $currentPage من $totalPages",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrorGold
                )
            }

            IconButton(
                onClick = { onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "التالي",
                    tint = if (currentPage < totalPages) Color.White else OrorTextMuted
                )
            }

            IconButton(
                onClick = { onPageChange(totalPages) },
                enabled = currentPage < totalPages
            ) {
                Icon(
                    imageVector = Icons.Default.LastPage,
                    contentDescription = "الأخيرة",
                    tint = if (currentPage < totalPages) OrorGold else OrorTextMuted
                )
            }
        }
    }
}

@Composable
fun HistoryCard(
    indexNumber: Int,
    historyItem: HistoryItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(OrorCardBorder, OrorGold.copy(alpha = 0.3f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: Number badge + Username + Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(OrorGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "شحنة #$indexNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrorGold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(
                            text = historyItem.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                // Success Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(OrorGreen.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(OrorGreen)
                        )
                        Text(
                            text = "تم الشحن",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrorGreen
                        )
                    }
                }
            }

            HorizontalDivider(color = OrorCardBorder.copy(alpha = 0.6f), thickness = 1.dp)

            // Middle Details: Product & Receiver Phone Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Product Chip
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = OrorSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CardMembership, contentDescription = null, tint = OrorGold, modifier = Modifier.size(16.dp))
                        Column {
                            Text("المنتج / الكارت", fontSize = 10.sp, color = OrorTextMuted)
                            Text(
                                text = historyItem.productName.ifBlank { "غير محدد" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Receiver Number Chip
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = OrorSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                        Column {
                            Text("رقم المستلم", fontSize = 10.sp, color = OrorTextMuted)
                            Text(
                                text = historyItem.receiverNumber.ifBlank { "غير متاح" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF93C5FD)
                            )
                        }
                    }
                }
            }

            // Bottom Section: Points Cost & Remaining Balance & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Points Cost
                    Text(
                        text = "الخصم: -${historyItem.pointsCost} نقطة",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrorCrimson
                    )

                    // Remaining Points
                    Text(
                        text = "المتبقي: ${historyItem.remainingPoints} نقطة",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrorGreen
                    )
                }

                // Purchase Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = OrorTextMuted, modifier = Modifier.size(12.dp))
                    Text(
                        text = TimeUtils.formatDisplayTime(historyItem.purchaseTime, null),
                        fontSize = 11.sp,
                        color = OrorTextMuted
                    )
                }
            }
        }
    }
}


