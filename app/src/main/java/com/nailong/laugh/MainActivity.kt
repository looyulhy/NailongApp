package com.nailong.laugh

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.VideoView
import android.widget.MediaController
import android.app.Activity

/**
 * 奶龙大笑 - 主界面
 *
 * 交互流程：
 * 1. 打开App看到大奶龙静态图
 * 2. 点击奶龙 → Q弹缩放动效（duang~）
 * 3. 弹跳动画结束 → 播放大笑视频（带声音）
 * 4. 视频播完 → 回到静态图，可再次点击
 *
 * 素材替换：
 * 修改 companion object 中的常量即可，详见下方注释
 */
class MainActivity : Activity() {

    private lateinit var ivNailong: ImageView
    private lateinit var videoNailong: VideoView

    // 是否正在播放视频，防止重复点击
    private var isPlaying = false

    // 视频资源URI（在onCreate中初始化）
    private var videoUri: Uri? = null

    companion object {
        // ====== 素材替换区 - 只需修改这里 ======

        // 静态图片资源ID
        // 替换方法：将新图片放入 res/drawable/ 目录，然后修改这行
        // 例如：R.drawable.my_nailong_image
        const val IMAGE_RES_ID = R.drawable.nailong_placeholder

        // 视频资源名称（不含扩展名）
        // 替换方法：将新 mp4 视频放入 res/raw/ 目录，然后修改这行
        // 例如：如果你的视频叫 my_laugh.mp4，就写 "my_laugh"
        const val VIDEO_RESOURCE_NAME = "nailong_laugh"

        // ====== 动画参数 - 可微调手感 ======

        // Q弹缩放的最大倍率
        const val BOUNCE_SCALE = 1.25f

        // Q弹动画时长（毫秒）
        const val BOUNCE_DURATION = 400L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivNailong = findViewById(R.id.ivNailong)
        videoNailong = findViewById(R.id.videoNailong)

        // 设置静态图片
        ivNailong.setImageResource(IMAGE_RES_ID)

        // 构造视频URI
        videoUri = Uri.parse("android.resource://$packageName/raw/$VIDEO_RESOURCE_NAME")

        // 设置视频完成监听
        videoNailong.setOnCompletionListener {
            onVideoComplete()
        }

        // 设置视频错误监听（视频文件不存在时优雅降级）
        videoNailong.setOnErrorListener { _, _, _ ->
            onVideoError()
            true
        }

        // 点击奶龙触发交互
        ivNailong.setOnClickListener {
            if (!isPlaying) {
                onNailongClicked()
            }
        }
    }

    /**
     * 奶龙被点击
     * 先播放Q弹缩放动画，动画结束后播放视频
     */
    private fun onNailongClicked() {
        isPlaying = true

        // 创建Q弹缩放动画：从1.0缩放到BOUNCE_SCALE，使用自定义弹簧插值器
        val bounceAnimation = ScaleAnimation(
            1.0f, BOUNCE_SCALE,   // X轴：1.0 → 1.25
            1.0f, BOUNCE_SCALE,   // Y轴：1.0 → 1.25
            Animation.RELATIVE_TO_SELF, 0.5f,  // 锚点X：自身中心
            Animation.RELATIVE_TO_SELF, 0.5f   // 锚点Y：自身中心
        ).apply {
            duration = BOUNCE_DURATION
            interpolator = BounceInterpolator(damping = 8.0f, frequency = 10.0f)
            fillAfter = false  // 动画结束后恢复原状
        }

        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // 动画开始，暂时移除点击监听
            }

            override fun onAnimationEnd(animation: Animation?) {
                // 弹跳动画结束，切换到视频播放
                startVideoPlayback()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        ivNailong.startAnimation(bounceAnimation)
    }

    /**
     * 开始播放大笑视频
     */
    private fun startVideoPlayback() {
        videoUri?.let { uri ->
            // 切换显示：隐藏静态图，显示视频
            ivNailong.visibility = View.GONE
            videoNailong.visibility = View.VISIBLE

            // 配置并播放视频
            videoNailong.setVideoURI(uri)
            videoNailong.start()
        } ?: run {
            // 没有视频URI，直接重置状态
            isPlaying = false
        }
    }

    /**
     * 视频播放完成
     * 切回静态图，恢复可点击状态
     */
    private fun onVideoComplete() {
        videoNailong.visibility = View.GONE
        ivNailong.visibility = View.VISIBLE
        isPlaying = false
    }

    /**
     * 视频播放出错（通常是视频文件不存在）
     * 优雅降级：切回静态图，添加一个轻微晃动提示用户
     */
    private fun onVideoError() {
        videoNailong.visibility = View.GONE
        ivNailong.visibility = View.VISIBLE

        // 轻微晃动提示（替代无法播放的视频）
        val shakeAnimation = ScaleAnimation(
            1.0f, 0.95f,
            1.0f, 0.95f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 150
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }
        ivNailong.startAnimation(shakeAnimation)

        isPlaying = false
    }

    override fun onPause() {
        super.onPause()
        // 页面不可见时暂停视频
        if (videoNailong.isPlaying) {
            videoNailong.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        // 从后台回来时，如果视频正在播放则恢复
        // 如果页面显示的是静态图，则不做任何操作
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放视频资源
        videoNailong.stopPlayback()
    }
}
