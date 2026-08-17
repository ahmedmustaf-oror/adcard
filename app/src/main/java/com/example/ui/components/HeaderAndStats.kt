package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HeaderSection(
    isMaintenance: Boolean,
    currentTime: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = OrorSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(OrorCardBorder, OrorGold.copy(alpha = 0.25f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Top Bar: Brand + System Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(OrorCrimson, OrorGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "O",
                            color = Color(0xFF0B0B1A),
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }

                    Column {
                        Text(
                            text = "OROR CARD",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = OrorGold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "لوحة الإدارة والمتابعة",
                            fontSize = 11.sp,
                            color = OrorTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Status Badges Column
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatusBadge(isMaintenance = isMaintenance)

                    // Background Monitoring Active Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = OrorGold.copy(alpha = 0.10f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(OrorGold.copy(alpha = 0.3f), OrorGold.copy(alpha = 0.3f)))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = OrorGold,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "مراقبة الخلفية نشطة",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrorGold
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = OrorCardBorder.copy(alpha = 0.6f)
            )

            // Live Clock Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = OrorTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "التوقيت الحالي:",
                        fontSize = 11.sp,
                        color = OrorTextMuted
                    )
                }

                Text(
                    text = currentTime,
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StatusBadge(isMaintenance: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val badgeBg = if (isMaintenance) Color(0x26FBBF24) else Color(0x2634D399)
    val dotColor = if (isMaintenance) OrorAmber else OrorGreen
    val text = if (isMaintenance) "وضع الصيانة" else "الخدمة متصلة"

    Surface(
        shape = RoundedCornerShape(30.dp),
        color = badgeBg,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(dotColor.copy(alpha = 0.4f), dotColor.copy(alpha = 0.4f))
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = alpha))
            )
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = dotColor
            )
        }
    }
}

@Composable
fun StatsGrid(
    totalUsers: Long,
    totalPoints: Long,
    pendingRequests: Int,
    todayPurchases: Int,
    totalPurchases: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Users & Points
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Default.Group,
                number = "$totalUsers",
                label = "إجمالي المستخدمين",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.Diamond,
                number = "$totalPoints",
                label = "إجمالي النقاط",
                isGold = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Requests & Purchases
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Default.Schedule,
                number = "$pendingRequests",
                label = "طلبات معلقة",
                accentColor = if (pendingRequests > 0) OrorAmber else OrorTextSecondary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.ShoppingBag,
                number = "$todayPurchases",
                label = "مشتريات اليوم",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.EmojiEvents,
                number = "$totalPurchases",
                label = "إجمالي المشتريات",
                isGold = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    number: String,
    label: String,
    isGold: Boolean = false,
    accentColor: Color = OrorGold,
    modifier: Modifier = Modifier
) {
    val containerBg = if (isGold) Color(0x18FFD700) else OrorSurface
    val borderBrush = if (isGold) {
        Brush.linearGradient(listOf(OrorGold.copy(alpha = 0.35f), OrorGold.copy(alpha = 0.15f)))
    } else {
        Brush.linearGradient(listOf(OrorCardBorder, OrorCardBorder))
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = borderBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isGold) OrorGold.copy(alpha = 0.18f) else OrorSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isGold) OrorGold else accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Text(
                    text = number,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGold) OrorGold else Color.White
                )
            }

            Text(
                text = label,
                fontSize = 11.sp,
                color = OrorTextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
