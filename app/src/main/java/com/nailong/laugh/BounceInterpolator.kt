package com.nailong.laugh

import android.view.Interpolator
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.PI

/**
 * Q弹插值器 - 基于阻尼弹簧模型
 *
 * 公式: y(t) = 1 - e^(-c*t) * cos(w*t*π)
 *
 * 模拟物理弹簧的弹性回弹效果：
 * - c (damping): 阻尼系数，越大回弹越快衰减，默认8
 * - w (frequency): 弹簧频率，越大弹跳次数越多，默认10
 *
 * 效果：先冲过目标值（放大超出1.0），然后弹回，
 * 再微微超出，再回来……逐渐收敛到1.0
 * 这就是那种 "duang~" 的Q弹手感
 */
class BounceInterpolator(
    private val damping: Float = 8.0f,
    private val frequency: Float = 10.0f
) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        // 阻尼弹簧公式：输出值围绕1.0振荡并逐渐收敛
        val decay = exp(-damping * t.toDouble())
        val oscillation = cos(frequency * t * PI)
        return (1.0 - decay * oscillation).toFloat()
    }
}
