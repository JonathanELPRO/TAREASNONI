package com.calyrsoft.ucbp1.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import com.calyrsoft.ucbp1.ui.icons.myiconpack.Imagen
import com.calyrsoft.ucbp1.ui.icons.myiconpack.Noadecuada
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Imagen, Noadecuada)
    return __AllIcons!!
  }
