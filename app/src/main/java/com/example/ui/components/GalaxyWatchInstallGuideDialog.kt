package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianMint
import com.example.ui.theme.PersianTurquoise

@Composable
fun GalaxyWatchInstallGuideDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, PersianMint.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("install_guide_dialog"),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PersianTurquoise, PersianMint)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Watch,
                        contentDescription = "راهنمای نصب گلکسی واچ",
                        tint = Color(0xFF090D14),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "آموزش نصب روی سامسونگ گلکسی واچ",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "سازگار با تمامی مدل‌های گلکسی واچ ۴، ۵، ۶، ۷ و اولترا (Wear OS)",
                    fontSize = 11.sp,
                    color = PersianMint,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // METHOD 1: WIRELESS ADB INSTALL (Standard for Wear OS side-loading)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(PersianMint, PersianTurquoise)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = PersianMint, modifier = Modifier.size(20.dp))
                            Text(
                                text = "روش اول: نصب مستقیم با وای‌فای (سریع و بدون کابل)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "۱. در ساعت خود به مسیر زیر بروید:\nSettings (تنظیمات) > About Watch (درباره ساعت) > Software info (اطلاعات نرم‌افزار)\n۲. روی عبارت «Software version» ۷ بار پشت سر هم ضربه بزنید تا پیام Developer mode turned on نمایان شود.\n۳. به منوی تنظیمات ساعت برگردید و وارد منوی جدید «Developer options» شوید.\n۴. دو گزینه «ADB debugging» و «Wireless debugging» را فعال (روشن) کنید.\n۵. در گوشی نرم‌افزار GeminiMan Wear OS Manager را باز کرده، به ساعت متصل شده و این فایل جدید را ارسال کنید.\n۶. فعال‌سازی واچ‌فیس: پس از نصب، انگشت خود را ۲ ثانیه روی صفحه ساعت نگه دارید، به انتهای واچ‌فیس‌ها بروید و با زدن علامت + (Add watch face)، «واچ‌فیس شمسی دکتر خسروی» را انتخاب فرمایید! (یا در گوشی وارد برنامه Galaxy Wearable شده و در بخش Downloaded آن را برگزینید).",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // EXPLANATION: WHY GALAXY WEARABLE DOESN'T INSTALL LOCAL APKS DIRECTLY
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(PersianGold.copy(alpha = 0.7f), PersianMint.copy(alpha = 0.5f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "آیا می‌توان مستقیماً و بدون برنامه جانبی از Galaxy Wearable نصب کرد؟",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PersianGold
                        )
                        Text(
                            text = "در ساعت‌های قدیمی سامسونگ (سیستم Tizen) این امکان وجود داشت؛ اما از گلکسی واچ ۴ به بعد که مجهز به سیستم‌عامل Wear OS گوگل شدند، به دلایل امنیتی اندروید، برنامه Galaxy Wearable فقط برنامه‌های تأییدشده Google Play و Galaxy Store را به ساعت ارسال می‌کند و اجازه نصب فایل‌های محلی APK شخصی را از منوی خود نمی‌دهد.\n\nبه همین دلیل، خود گوگل و سامسونگ قابلیت رسمی «Wireless Debugging» را درون تنظیمات ساعت قرار داده‌اند تا بتوانید هر برنامه‌ای را به صورت بی‌سیم در کمتر از ۲ دقیقه به ساعت منتقل فرمایید.",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // METHOD 2: ANDROID HOME SCREEN WIDGET
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162032))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = PersianGold, modifier = Modifier.size(20.dp))
                            Text(
                                text = "روش دوم: افزودن به عنوان ویجت صفحه گوشی",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "روی یک بخش خالی از صفحه اصلی گوشی انگشت خود را نگه دارید، گزینه «ویجت‌ها (Widgets)» را انتخاب کرده و ویجت «تقویم واچ» را به صفحه اضافه فرمایید.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct Set Watch Face button
                Button(
                    onClick = {
                        val success = com.example.util.GalaxyWearableHelper.setAsWatchFace(context)
                        if (!success) {
                            com.example.util.GalaxyWearableHelper.openGalaxyWearable(context)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_set_as_watch_face_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PersianTurquoise,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Watch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فعال‌سازی مستقیم به عنوان واچ‌فیس (Set as Watch Face)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Open Galaxy Wearable app
                    Button(
                        onClick = {
                            com.example.util.GalaxyWearableHelper.openGalaxyWearable(context)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_galaxy_wearable_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PersianMint,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Watch, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("برنامه Galaxy Wearable", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Share APK / App
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "تقویم هوشمند و واچ‌فیس شمسی گلکسی واچ سامسونگ - طراحی دکتر خسروی (09123371764 | https://1tw.ir)"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "ارسال برنامه به ساعت یا دیگران"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_app_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اشتراک‌گذاری", color = Color.White, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_install_guide_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF334155),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("متوجه شدم")
                }
            }
        }
    }
}
