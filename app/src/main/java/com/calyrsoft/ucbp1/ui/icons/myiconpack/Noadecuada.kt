package com.calyrsoft.ucbp1.ui.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.calyrsoft.ucbp1.ui.icons.MyIconPack
import kotlin.Unit

public val MyIconPack.Noadecuada: ImageVector
    get() {
        if (_noadecuada != null) {
            return _noadecuada!!
        }
        _noadecuada = Builder(name = "Noadecuada", defaultWidth = 566.0.dp, defaultHeight =
                1006.0.dp, viewportWidth = 566.0f, viewportHeight = 1006.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = SolidColor(Color(0x00000000)),
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(0.0f, 503.0f)
                lineToRelative(0.0f, 503.0f)
                lineToRelative(283.0f, -0.0f)
                lineToRelative(283.0f, -0.0f)
                lineToRelative(0.0f, -503.0f)
                lineToRelative(0.0f, -503.0f)
                lineToRelative(-283.0f, -0.0f)
                lineToRelative(-283.0f, -0.0f)
                lineToRelative(0.0f, 503.0f)
                close()
            }
        }
        .build()
        return _noadecuada!!
    }

private var _noadecuada: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.Noadecuada, contentDescription = "")
    }
}
