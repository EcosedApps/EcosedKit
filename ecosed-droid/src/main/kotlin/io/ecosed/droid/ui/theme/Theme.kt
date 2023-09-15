package io.ecosed.droid.ui.theme

import android.content.Context
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val darkColorScheme: ColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val lightColorScheme: ColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
internal fun LibEcosedTheme(
    dynamicColor: Boolean = true,
    content: @Composable (Boolean) -> Unit
) {
    // 获取系统是否处于深色模式
    val darkTheme: Boolean = isSystemInDarkTheme()
    // 获取上下文
    val context: Context = LocalContext.current
    // 获取ComposeView
    val view: View = LocalView.current
    // 初始化系统栏控制器
    val systemUiController: SystemUiController = rememberSystemUiController()

    val colorScheme: ColorScheme = when {
        dynamicColor and (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            if (darkTheme) {
                dynamicDarkColorScheme(context = context)
            } else {
                dynamicLightColorScheme(context = context)
            }
        }

        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    if (!view.isInEditMode) SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = MaterialTheme.shapes,
        typography = Typography,
        content = {
            content(dynamicColor)
        }
    )
}