package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.GregorianDate
import com.example.calendar.LunarHijriDate
import com.example.calendar.PersianOccasion
import com.example.calendar.SolarDate
import com.example.model.GalaxyWatchDevice
import com.example.model.WatchCustomSettings
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GalaxyWatchFrame(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    gregorianDate: GregorianDate,
    lunarDate: LunarHijriDate,
    occasions: List<PersianOccasion>,
    currentTimeMillis: Long,
    onRotateBezelClockwise: () -> Unit,
    onRotateBezelCounterClockwise: () -> Unit,
    modifier: Modifier = Modifier
) {
    val device = settings.device
    val isAmbient = settings.isAmbientAodMode

    var bezelRotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedBezelAngle by animateFloatAsState(targetValue = bezelRotationAngle, label = "bezelAngle")

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Physical Watch Container
        Box(
            modifier = Modifier
                .size(310.dp)
                .aspectRatio(1f)
                .testTag("galaxy_watch_container"),
            contentAlignment = Alignment.Center
        ) {
            // 1. Strap Lugs & Silicone Strap extension hints (Top & Bottom)
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(310.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF14171E))
            )

            // 2. Hardware Case Shape
            when (device) {
                GalaxyWatchDevice.WATCH_ULTRA -> {
                    // Cushion shape case (rounded square with titanium bumpers)
                    Box(
                        modifier = Modifier
                            .size(286.dp)
                            .shadow(16.dp, RoundedCornerShape(56.dp))
                            .clip(RoundedCornerShape(56.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF2A2E38), Color(0xFF16181E), Color(0xFF242730))
                                )
                            )
                            .border(2.dp, Color(0xFF3F4452), RoundedCornerShape(56.dp))
                    )

                    // Right Side Hardware Pushers (Orange Action Button in middle)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            // Top pusher
                            Box(
                                modifier = Modifier
                                    .size(width = 6.dp, height = 24.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF474E5D))
                            )
                            // Ultra Orange Action Button
                            Box(
                                modifier = Modifier
                                    .size(width = 9.dp, height = 30.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFF6D00))
                                    .border(1.dp, Color(0xFFFFA040), RoundedCornerShape(4.dp))
                            )
                            // Bottom pusher
                            Box(
                                modifier = Modifier
                                    .size(width = 6.dp, height = 24.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF474E5D))
                            )
                        }
                    }
                }

                GalaxyWatchDevice.WATCH_6_CLASSIC -> {
                    // Circular stainless case with distinct grooved mechanical bezel
                    Box(
                        modifier = Modifier
                            .size(284.dp)
                            .shadow(14.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF383D48), Color(0xFF1B1D24), Color(0xFF121418))
                                )
                            )
                            .border(2.5.dp, Color(0xFF555B6A), CircleShape)
                    )

                    // Right Hardware Buttons
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 7.dp, height = 22.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF888F9E))
                            )
                            Box(
                                modifier = Modifier
                                    .size(width = 7.dp, height = 22.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF888F9E))
                            )
                        }
                    }
                }

                else -> {
                    // Watch 7 / 5 Pro / 4: Sleek circular titanium/aluminum frame
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF282C35), Color(0xFF151820), Color(0xFF0F1116))
                                )
                            )
                            .border(1.5.dp, Color(0xFF404756), CircleShape)
                    )

                    // Standard Galaxy Watch Side Buttons
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 3.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 6.dp, height = 20.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(device.accentColor)
                            )
                            Box(
                                modifier = Modifier
                                    .size(width = 6.dp, height = 20.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF555D6E))
                            )
                        }
                    }
                }
            }

            // 3. Bezel Ring Canvas (with rotating bezel notches and minute markings)
            Canvas(
                modifier = Modifier
                    .size(266.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            if (dragAmount.y < -5 || dragAmount.x > 5) {
                                bezelRotationAngle += 15f
                                onRotateBezelClockwise()
                            } else if (dragAmount.y > 5 || dragAmount.x < -5) {
                                bezelRotationAngle -= 15f
                                onRotateBezelCounterClockwise()
                            }
                        }
                    }
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val bezelRadius = size.minDimension / 2f

                // Outer bezel edge
                drawCircle(
                    color = Color(0xFF141720),
                    radius = bezelRadius,
                    center = center
                )

                rotate(animatedBezelAngle, pivot = center) {
                    // Bezel indices (Ultra Orange or Classic numbers)
                    val step = if (device == GalaxyWatchDevice.WATCH_6_CLASSIC) 6 else 15
                    for (deg in 0 until 360 step step) {
                        val angleRad = Math.toRadians(deg.toDouble())
                        val isQuarter = deg % 90 == 0
                        val tickLen = if (isQuarter) 8.dp.toPx() else 4.dp.toPx()
                        val tickWidth = if (isQuarter) 2.dp.toPx() else 1.dp.toPx()
                        val tickCol = when {
                            device == GalaxyWatchDevice.WATCH_ULTRA && isQuarter -> Color(0xFFFF6D00)
                            device == GalaxyWatchDevice.WATCH_ULTRA -> Color(0xFF888E9B)
                            isQuarter -> Color(0xFFE2E8F0)
                            else -> Color(0xFF545C6C)
                        }

                        val startX = (center.x + (bezelRadius - 3.dp.toPx() - tickLen) * sin(angleRad)).toFloat()
                        val startY = (center.y - (bezelRadius - 3.dp.toPx() - tickLen) * cos(angleRad)).toFloat()
                        val endX = (center.x + (bezelRadius - 3.dp.toPx()) * sin(angleRad)).toFloat()
                        val endY = (center.y - (bezelRadius - 3.dp.toPx()) * cos(angleRad)).toFloat()

                        drawLine(
                            color = tickCol,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = tickWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // 4. Circular AMOLED Display (1:1 Circular Screen)
            Box(
                modifier = Modifier
                    .size(232.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // The Watch Face Composable
                WatchFaceRenderer(
                    settings = settings,
                    solarDate = solarDate,
                    gregorianDate = gregorianDate,
                    lunarDate = lunarDate,
                    occasions = occasions,
                    currentTimeMillis = currentTimeMillis,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Minimal Status Icons (Wear OS system icons)
                if (!isAmbient) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Wifi,
                            contentDescription = "وای‌فای گلکسی واچ",
                            tint = Color(0xFF888888),
                            modifier = Modifier.size(10.dp)
                        )
                        Icon(
                            Icons.Default.BatteryFull,
                            contentDescription = "باتری ساعت",
                            tint = if (settings.isBatterySaverEnabled) Color(0xFF2DD4BF) else Color(0xFF888888),
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "۹۴٪",
                            fontSize = 8.sp,
                            color = Color(0xFFAAAAAA),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Ambient Mode Overlay (Simulating OLED Low Power & Burn-in Shift)
                if (isAmbient) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = "AOD • حالت کم‌مصرف",
                            fontSize = 9.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Rotating Bezel Controls Simulation Bar
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF161F2E))
                .border(1.dp, Color(0xFF2B3A52), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    bezelRotationAngle -= 20f
                    onRotateBezelCounterClockwise()
                },
                modifier = Modifier
                    .size(32.dp)
                    .testTag("bezel_left_button")
            ) {
                Icon(
                    Icons.Default.RotateLeft,
                    contentDescription = "چرخش بزل ساعت به چپ",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = "${device.titlePersian}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE2E8F0)
            )

            IconButton(
                onClick = {
                    bezelRotationAngle += 20f
                    onRotateBezelClockwise()
                },
                modifier = Modifier
                    .size(32.dp)
                    .testTag("bezel_right_button")
            ) {
                Icon(
                    Icons.Default.RotateRight,
                    contentDescription = "چرخش بزل ساعت به راست",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
