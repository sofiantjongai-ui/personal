package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiInsightEntity
import com.example.data.repository.NetWorthSummary
import com.example.ui.components.AiInsightCardItem
import com.example.ui.components.formatPercent
import com.example.ui.components.formatRupiah
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BrandBlueIce
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueVibrant
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun AiAdvisorScreen(
    netWorth: NetWorthSummary,
    insights: List<AiInsightEntity>,
    onMarkRead: (Long) -> Unit
) {
    val debtRatio = if (netWorth.totalAssets > 0) (netWorth.totalLiabilities / netWorth.totalAssets) * 100.0 else 0.0
    val healthScore = (85 - (debtRatio * 0.2) + (netWorth.totalLiquidCash / 10000000.0).coerceAtMost(10.0)).coerceIn(50.0, 98.0).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "ARTIFICIAL INTELLIGENCE & HEURISTIC ENGINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlueLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Apex AI Advisor",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryDark
                )
            }
        }

        // Financial Health Score Hero
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    BrandBluePrimary.copy(alpha = 0.25f),
                                    BrandBlueVibrant.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                radius = 500f
                            )
                        )
                        .border(1.dp, BrandBluePrimary.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SKOR KESEHATAN FINANSIAL",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryDark,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$healthScore",
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BrandBlueLight
                                    )
                                    Text(
                                        text = " / 100",
                                        fontSize = 18.sp,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "PRIME HEALTH",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Metric indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Rasio Hutang (D/A)", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("${formatPercent(debtRatio)} (Sehat)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentEmerald)
                            }
                            Column {
                                Text("Likuiditas Runway", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("8.4 Bulan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandBlueLight)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Savings Rate", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("38.5%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentEmerald)
                            }
                        }
                    }
                }
            }
        }

        // Active AI Insights list
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rekomendasi Pintar AI (${insights.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }
        }

        items(insights) { insight ->
            AiInsightCardItem(
                insight = insight,
                onApplyClick = { onMarkRead(insight.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
