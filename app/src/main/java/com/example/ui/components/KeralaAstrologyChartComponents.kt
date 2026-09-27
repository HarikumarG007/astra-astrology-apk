package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.AuspiciousEmerald
import com.example.ui.theme.CautionCoral
import com.example.ui.theme.MixedAmber
import com.example.ui.theme.TempleGold

/**
 * Subtle Yin-Yang-inspired visual harmony medallion (Visual Design Balance Element Only).
 */
@Composable
fun YinYangBalanceMedallion(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    onClick: (() -> Unit)? = null
) {
    val goldColor = TempleGold
    val darkColor = Color(0xFF0D1322)
    val lightColor = Color(0xFFFBF7EE)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val d = this.size.minDimension
            val r = d / 2f
            drawArc(
                color = darkColor,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
                size = Size(d, d)
            )
            drawArc(
                color = lightColor,
                startAngle = 270f,
                sweepAngle = 180f,
                useCenter = true,
                size = Size(d, d)
            )
            drawCircle(
                color = lightColor,
                radius = r / 2f,
                center = Offset(r, r / 2f)
            )
            drawCircle(
                color = darkColor,
                radius = r / 2f,
                center = Offset(r, r * 1.5f)
            )
            drawCircle(
                color = darkColor,
                radius = r / 6.5f,
                center = Offset(r, r / 2f)
            )
            drawCircle(
                color = goldColor,
                radius = r / 6.5f,
                center = Offset(r, r * 1.5f)
            )
            drawCircle(
                color = goldColor,
                radius = r - 1.5f,
                style = Stroke(width = 2.5f)
            )
        }
    }
}

@Composable
fun PeriodClassificationBadge(
    classification: PeriodClassification,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (classification) {
        PeriodClassification.FAVORABLE -> AuspiciousEmerald.copy(alpha = 0.18f) to AuspiciousEmerald
        PeriodClassification.MIXED -> MixedAmber.copy(alpha = 0.18f) to MixedAmber
        PeriodClassification.CHALLENGING -> CautionCoral.copy(alpha = 0.18f) to CautionCoral
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        modifier = modifier.border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(50))
    ) {
        Text(
            text = classification.malayalamLabel,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

/**
 * Authentic Kerala-style 12-Rashi Square Chakra View.
 * Fixed clockwise signs starting with Meenam (top-left), Mesham (top-2nd), Idavam (top-3rd), Mithunam (top-right)...
 * Supports tapping any Rashi cell to inspect all planets and Bhava details inside that Rashi.
 */
@Composable
fun KeralaRashiChakraView(
    chartTitleMalayalam: String,
    chartSubtitleMalayalam: String,
    lagnaRashi: Rashi,
    planetSigns: Map<Planet, Rashi>,
    modifier: Modifier = Modifier,
    onRashiSelected: ((Rashi) -> Unit)? = null
) {
    var selectedRashi by remember { mutableStateOf<Rashi?>(lagnaRashi) }
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
    val cellBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    val centerBg = MaterialTheme.colorScheme.surface

    // 4x4 grid mapping: null for center 2x2 cells, Rashi index (0..11) for outer 12 cells
    val gridRashiMap = listOf(
        listOf(Rashi.MEENAM, Rashi.MESHAM, Rashi.IDAVAM, Rashi.MITHUNAM),
        listOf(Rashi.KUMBHAM, null, null, Rashi.KARKADAKAM),
        listOf(Rashi.MAKARAM, null, null, Rashi.CHINGAM),
        listOf(Rashi.DHANU, Rashi.VRISCHIKAM, Rashi.THULAM, Rashi.KANNI)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kerala_rashi_chakra_${chartTitleMalayalam.take(6)}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, borderColor, RoundedCornerShape(14.dp))
                .background(cellBg)
        ) {
            val totalSide = maxWidth
            val cellSize = totalSide / 4f

            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0..3) {
                    Row(modifier = Modifier.fillMaxWidth().height(cellSize)) {
                        for (col in 0..3) {
                            val rashi = gridRashiMap[row][col]
                            if (rashi != null) {
                                val isLagna = (rashi == lagnaRashi)
                                val isSelected = (rashi == selectedRashi)
                                val houseNum = ((rashi.index - lagnaRashi.index + 12) % 12) + 1
                                val planetsInCell = planetSigns.entries
                                    .filter { it.value == rashi }
                                    .map { it.key }

                                RashiCellBox(
                                    rashi = rashi,
                                    houseNumber = houseNum,
                                    isLagna = isLagna,
                                    isSelected = isSelected,
                                    planets = planetsInCell,
                                    borderColor = borderColor,
                                    modifier = Modifier
                                        .size(cellSize)
                                        .clickable {
                                            selectedRashi = rashi
                                            onRashiSelected?.invoke(rashi)
                                        }
                                )
                            } else if (row == 1 && col == 1) {
                                // Render 2x2 Center Medallion Box
                                Box(
                                    modifier = Modifier
                                        .size(cellSize * 2f)
                                        .background(centerBg)
                                        .border(1.5.dp, borderColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        YinYangBalanceMedallion(size = 28.dp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = chartTitleMalayalam,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = chartSubtitleMalayalam,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "ലഗ്നം: ${lagnaRashi.malayalamName}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            } else if ((row == 1 && col == 2) || (row == 2 && (col == 1 || col == 2))) {
                                // Occupied by the 2x2 center box; handled below via overlay
                                Spacer(modifier = Modifier.size(cellSize))
                            }
                        }
                    }
                }
            }

            // Overlay the 2x2 center box cleanly over (row 1..2, col 1..2)
            Box(
                modifier = Modifier
                    .offset(x = cellSize, y = cellSize)
                    .size(cellSize * 2f)
                    .background(centerBg)
                    .border(1.5.dp, borderColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    YinYangBalanceMedallion(size = 28.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = chartTitleMalayalam,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = chartSubtitleMalayalam,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ലഗ്നം: ${lagnaRashi.malayalamName}",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Selected Rashi Interactive Inspector Bar
        selectedRashi?.let { sel ->
            val houseNum = ((sel.index - lagnaRashi.index + 12) % 12) + 1
            val occupants = planetSigns.entries.filter { it.value == sel }.map { it.key }
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${sel.malayalamName} (${houseNum}-ാം ഭാവം) • രാശ്യാധിപൻ: ${sel.lord.malayalamName}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (occupants.isEmpty()) "ഈ രാശിയിൽ ഗ്രഹങ്ങളില്ല (ശുദ്ധ ഭാവം)"
                            else "ഗ്രഹങ്ങൾ: ${occupants.joinToString(", ") { it.malayalamName }}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RashiCellBox(
    rashi: Rashi,
    houseNumber: Int,
    isLagna: Boolean,
    isSelected: Boolean,
    planets: List<Planet>,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        isLagna -> MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        else -> Color.Transparent
    }
    Box(
        modifier = modifier
            .background(bgColor)
            .border(0.75.dp, borderColor)
            .padding(4.dp)
    ) {
        // Top row: Rashi Name & House Number
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rashi.malayalamName,
                fontSize = 9.5.sp,
                lineHeight = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            Text(
                text = "$houseNumber",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }

        // Center: Lagna marker "ല" + Planet Malayalam short codes
        val tokens = buildList {
            if (isLagna) add("ല")
            planets.forEach { add(it.shortCodeMal) }
        }
        if (tokens.isNotEmpty()) {
            Text(
                text = tokens.joinToString(" "),
                fontSize = if (tokens.size > 3) 11.5.sp else 13.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLagna) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(top = 8.dp)
            )
        }
    }
}
