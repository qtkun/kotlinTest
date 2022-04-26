package com.qtk.kotlintest.widget

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.qtk.kotlintest.MuteHelper
import com.qtk.kotlintest.R
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotSaveListener
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.NetworkUtils
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import moe.codeest.enviews.ENDownloadView
import moe.codeest.enviews.ENPlayView
import java.io.File

open class DcVideoPlayer : GSYVideoPlayer, LifecycleEventObserver {
    //亮度dialog
    protected var mBrightnessDialog: Dialog? = null

    //音量dialog
    protected var mVolumeDialog: Dialog? = null

    //触摸进度dialog
    protected var mProgressDialog: Dialog? = null

    //触摸进度条的progress
    protected var mDialogProgressBar: ProgressBar? = null

    //音量进度条的progress
    protected var mDialogVolumeProgressBar: ProgressBar? = null

    //亮度文本
    protected var mBrightnessDialogTv: TextView? = null

    //触摸移动显示文本
    protected var mDialogSeekTime: TextView? = null

    //触摸移动显示全部时间
    protected var mDialogTotalTime: TextView? = null
    protected var mMuteIv: ImageView? = null

    //触摸移动方向icon
    protected var mDialogIcon: ImageView? = null
    protected var mBottomProgressDrawable: Drawable? = null
    protected var mBottomShowProgressDrawable: Drawable? = null
    protected var mBottomShowProgressThumbDrawable: Drawable? = null
    protected var mVolumeProgressDrawable: Drawable? = null
    protected var mDialogProgressBarDrawable: Drawable? = null
    protected var mDialogProgressHighLightColor = -11
    protected var mDialogProgressNormalColor = -11

    private var muteHelper: MuteHelper? = null

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == View.VISIBLE) {
            muteHelper?.registerMuteListener(object: MuteHelper.MuteListener {
                override fun onMuteChange(mute: Boolean) {
                    if (mute) {
                        mMuteIv?.setImageResource(R.drawable.ic_icon_mute)
                    } else {
                        mMuteIv?.setImageResource(R.drawable.ic_icon_unmute)
                    }
                }
            })
        } else {
            muteHelper?.unregisterMuteListener()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {

            }
            Lifecycle.Event.ON_RESUME -> {

            }
            Lifecycle.Event.ON_DESTROY -> {

            }
        }
    }

    override fun init(context: Context) {
        super.init(context)
        muteHelper = MuteHelper(context)
        mMuteIv = findViewById(R.id.mute)
        updateMuteImage()
        mMuteIv?.setOnClickListener {
            val isMute = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)
            if (isMute) {
                mAudioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_UNMUTE,
                    0
                )
                mMuteIv?.setImageResource(R.drawable.ic_icon_unmute)
            } else {
                mAudioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_MUTE,
                    0
                )
                mMuteIv?.setImageResource(R.drawable.ic_icon_mute)
            }
        }
        //增加自定义ui
        if (mBottomProgressDrawable != null) {
            mBottomProgressBar.progressDrawable = mBottomProgressDrawable
        }
        if (mBottomShowProgressDrawable != null) {
            mProgressBar.progressDrawable = mBottomProgressDrawable
        }
        if (mBottomShowProgressThumbDrawable != null) {
            mProgressBar.thumb = mBottomShowProgressThumbDrawable
        }
    }

    /**
     * 继承后重写可替换为你需要的布局
     *
     * @return
     */
    override fun getLayoutId(): Int {
        return R.layout.video_layout_dc
    }

    /**
     * 开始播放
     */
    override fun startPlayLogic() {
        if (mVideoAllCallBack != null) {
            Debuger.printfLog("onClickStartThumb")
            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, this@DcVideoPlayer)
        }
        prepareVideo()
        startDismissControlViewTimer()
    }

    /**
     * 显示wifi确定框，如需要自定义继承重写即可
     */
    override fun showWifiDialog() {
        if (!NetworkUtils.isAvailable(mContext)) {
            //Toast.makeText(mContext, getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
            startPlayLogic()
            return
        }
        val builder = AlertDialog.Builder(activityContext)
        builder.setMessage(resources.getString(R.string.tips_not_wifi))
        builder.setPositiveButton(resources.getString(R.string.tips_not_wifi_confirm)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            startPlayLogic()
        }
        builder.setNegativeButton(resources.getString(R.string.tips_not_wifi_cancel)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.create().show()
    }

    /**
     * 触摸显示滑动进度dialog，如需要自定义继承重写即可，记得重写dismissProgressDialog
     */
    override fun showProgressDialog(
        deltaX: Float,
        seekTime: String,
        seekTimePosition: Int,
        totalTime: String,
        totalTimeDuration: Int
    ) {
        if (mProgressDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                progressDialogLayoutId, null
            )
            if (localView.findViewById<View>(progressDialogProgressId) is ProgressBar) {
                mDialogProgressBar = localView.findViewById(
                    progressDialogProgressId
                )
                if (mDialogProgressBarDrawable != null) {
                    mDialogProgressBar!!.progressDrawable = mDialogProgressBarDrawable
                }
            }
            if (localView.findViewById<View>(progressDialogCurrentDurationTextId) is TextView) {
                mDialogSeekTime = localView.findViewById(
                    progressDialogCurrentDurationTextId
                )
            }
            if (localView.findViewById<View>(progressDialogAllDurationTextId) is TextView) {
                mDialogTotalTime = localView.findViewById(progressDialogAllDurationTextId)
            }
            if (localView.findViewById<View>(progressDialogImageId) is ImageView) {
                mDialogIcon = localView.findViewById(progressDialogImageId)
            }
            mProgressDialog = Dialog(activityContext, R.style.video_style_dialog_progress).apply {
                setContentView(localView)
                window?.apply {
                    addFlags(Window.FEATURE_ACTION_BAR)
                    addFlags(32)
                    addFlags(16)
                    setLayout(width, height)
                }
            }
            if (mDialogProgressNormalColor != -11 && mDialogTotalTime != null) {
                mDialogTotalTime?.setTextColor(mDialogProgressNormalColor)
            }
            if (mDialogProgressHighLightColor != -11 && mDialogSeekTime != null) {
                mDialogSeekTime?.setTextColor(mDialogProgressHighLightColor)
            }
            val localLayoutParams = mProgressDialog!!.window!!.attributes
            localLayoutParams.gravity = Gravity.TOP
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mProgressDialog!!.window!!.attributes = localLayoutParams
        }
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
        if (mDialogSeekTime != null) {
            mDialogSeekTime!!.text = seekTime
        }
        if (mDialogTotalTime != null) {
            mDialogTotalTime!!.text = " / $totalTime"
        }
        if (totalTimeDuration > 0) if (mDialogProgressBar != null) {
            mDialogProgressBar!!.progress = seekTimePosition * 100 / totalTimeDuration
        }
        if (deltaX > 0) {
            if (mDialogIcon != null) {
                mDialogIcon!!.setBackgroundResource(R.drawable.video_forward_icon)
            }
        } else {
            if (mDialogIcon != null) {
                mDialogIcon!!.setBackgroundResource(R.drawable.video_backward_icon)
            }
        }
    }

    override fun dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
    }

    /**
     * 触摸音量dialog，如需要自定义继承重写即可，记得重写dismissVolumeDialog
     */
    override fun showVolumeDialog(deltaY: Float, volumePercent: Int) {
        if (mVolumeDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                volumeLayoutId, null
            )
            if (localView.findViewById<View>(volumeProgressId) is ProgressBar) {
                mDialogVolumeProgressBar = localView.findViewById<View>(
                    volumeProgressId
                ) as ProgressBar
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar!!.progressDrawable = mVolumeProgressDrawable
                }
            }
            mVolumeDialog = Dialog(activityContext, R.style.video_style_dialog_progress)
            mVolumeDialog!!.setContentView(localView)
            mVolumeDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mVolumeDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mVolumeDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mVolumeDialog!!.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val localLayoutParams = mVolumeDialog!!.window!!
                .attributes
            localLayoutParams.gravity = Gravity.TOP or Gravity.START
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mVolumeDialog!!.window!!.attributes = localLayoutParams
        }
        if (!mVolumeDialog!!.isShowing) {
            mVolumeDialog!!.show()
        }
        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar!!.progress = volumePercent
        }
    }

    override fun dismissVolumeDialog() {
        if (mVolumeDialog != null) {
            mVolumeDialog!!.dismiss()
            mVolumeDialog = null
        }
    }

    /**
     * 触摸亮度dialog，如需要自定义继承重写即可，记得重写dismissBrightnessDialog
     */
    override fun showBrightnessDialog(percent: Float) {
        if (mBrightnessDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                brightnessLayoutId, null
            )
            if (localView.findViewById<View>(brightnessTextId) is TextView) {
                mBrightnessDialogTv = localView.findViewById<View>(brightnessTextId) as TextView
            }
            mBrightnessDialog = Dialog(activityContext, R.style.video_style_dialog_progress)
            mBrightnessDialog!!.setContentView(localView)
            mBrightnessDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mBrightnessDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mBrightnessDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mBrightnessDialog!!.window!!.decorView.systemUiVisibility = SYSTEM_UI_FLAG_HIDE_NAVIGATION
            mBrightnessDialog!!.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val localLayoutParams = mBrightnessDialog!!.window!!
                .attributes
            localLayoutParams.gravity = Gravity.TOP or Gravity.END
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mBrightnessDialog!!.window!!.attributes = localLayoutParams
        }
        if (!mBrightnessDialog!!.isShowing) {
            mBrightnessDialog!!.show()
        }
        if (mBrightnessDialogTv != null) mBrightnessDialogTv!!.text = "${(percent * 100).toInt()}%"
    }

    override fun dismissBrightnessDialog() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog!!.dismiss()
            mBrightnessDialog = null
        }
    }

    override fun cloneParams(from: GSYBaseVideoPlayer, to: GSYBaseVideoPlayer) {
        super.cloneParams(from, to)
        val sf = from as DcVideoPlayer
        val st = to as DcVideoPlayer
        if (st.mProgressBar != null && sf.mProgressBar != null) {
            st.mProgressBar.progress = sf.mProgressBar.progress
            st.mProgressBar.secondaryProgress = sf.mProgressBar.secondaryProgress
        }
        if (st.mTotalTimeTextView != null && sf.mTotalTimeTextView != null) {
            st.mTotalTimeTextView.text = sf.mTotalTimeTextView.text
        }
        if (st.mCurrentTimeTextView != null && sf.mCurrentTimeTextView != null) {
            st.mCurrentTimeTextView.text = sf.mCurrentTimeTextView.text
        }
    }

    override fun onAutoCompletion() {
        super.onAutoCompletion()
        setStateAndUi(CURRENT_STATE_NORMAL)
    }

    /**
     * 将自定义的效果也设置到全屏
     *
     * @param context
     * @param actionBar 是否有actionBar，有的话需要隐藏
     * @param statusBar 是否有状态bar，有的话需要隐藏
     * @return
     */
    override fun startWindowFullscreen(
        context: Context,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        if (gsyBaseVideoPlayer != null) {
            val gsyVideoPlayer = gsyBaseVideoPlayer as DcVideoPlayer
            gsyVideoPlayer.setLockClickListener(mLockClickListener)
            gsyVideoPlayer.isNeedLockFull = isNeedLockFull
            initFullUI(gsyVideoPlayer)
            //比如你自定义了返回案件，但是因为返回按键底层已经设置了返回事件，所以你需要在这里重新增加的逻辑
        }
        return gsyBaseVideoPlayer
    }
    /********************************各类UI的状态显示 */
    /**
     * 点击触摸显示和隐藏逻辑
     */
    override fun onClickUiToggle(e: MotionEvent) {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE)
            return
        }
        if (mIfCurrentIsFullscreen && !mSurfaceErrorPlay && mCurrentState == CURRENT_STATE_ERROR) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPlayingClear()
                } else {
                    changeUiToPlayingShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PREPAREING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPrepareingClear()
                } else {
                    changeUiToPreparingShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPlayingClear()
                } else {
                    changeUiToPlayingShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPauseClear()
                } else {
                    changeUiToPauseShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToCompleteClear()
                } else {
                    changeUiToCompleteShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPlayingBufferingClear()
                } else {
                    changeUiToPlayingBufferingShow()
                }
            }
        }
    }

    override fun hideAllWidget() {
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
    }

    override fun changeUiToNormal() {
        Debuger.printfLog("changeUiToNormal")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        updateStartImage()
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
    }

    override fun changeUiToPreparingShow() {
        Debuger.printfLog("changeUiToPreparingShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        updateMuteImage()
        if (mLoadingProgressBar is ENDownloadView) {
            val enDownloadView = mLoadingProgressBar as ENDownloadView
            if (enDownloadView.currentState == ENDownloadView.STATE_PRE) {
                (mLoadingProgressBar as ENDownloadView).start()
            }
        }
    }

    override fun changeUiToPlayingShow() {
        Debuger.printfLog("changeUiToPlayingShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    override fun changeUiToPauseShow() {
        Debuger.printfLog("changeUiToPauseShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
        updatePauseCover()
    }

    override fun changeUiToPlayingBufferingShow() {
        Debuger.printfLog("changeUiToPlayingBufferingShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            val enDownloadView = mLoadingProgressBar as ENDownloadView
            if (enDownloadView.currentState == ENDownloadView.STATE_PRE) {
                (mLoadingProgressBar as ENDownloadView).start()
            }
        }
    }

    override fun changeUiToCompleteShow() {
        Debuger.printfLog("changeUiToCompleteShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    override fun changeUiToError() {
        Debuger.printfLog("changeUiToError")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dismissVolumeDialog()
        dismissBrightnessDialog()
    }

    /**
     * 触摸进度dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogLayoutId: Int
        get() = R.layout.video_progress_dialog

    /**
     * 触摸进度dialog的进度条id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogProgressId: Int
        get() = R.id.duration_progressbar

    /**
     * 触摸进度dialog的当前时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogCurrentDurationTextId: Int
        get() = R.id.tv_current

    /**
     * 触摸进度dialog全部时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogAllDurationTextId: Int
        get() = R.id.tv_duration

    /**
     * 触摸进度dialog的图片id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogImageId: Int
        get() = R.id.duration_image_tip

    /**
     * 音量dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showVolumeDialog方法
     */
    protected val volumeLayoutId: Int
        get() = R.layout.video_volume_dialog

    /**
     * 音量dialog的百分比进度条 id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showVolumeDialog方法
     */
    protected val volumeProgressId: Int
        get() = R.id.volume_progressbar

    /**
     * 亮度dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showBrightnessDialog方法
     */
    protected val brightnessLayoutId: Int
        get() = R.layout.video_brightness

    /**
     * 亮度dialog的百分比text id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showBrightnessDialog方法
     */
    protected val brightnessTextId: Int
        get() = R.id.app_video_brightness

    protected fun changeUiToPrepareingClear() {
        Debuger.printfLog("changeUiToPrepareingClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
    }

    protected fun changeUiToPlayingClear() {
        Debuger.printfLog("changeUiToPlayingClear")
        changeUiToClear()
        setViewShowState(mBottomProgressBar, VISIBLE)
    }

    protected fun changeUiToPauseClear() {
        Debuger.printfLog("changeUiToPauseClear")
        changeUiToClear()
        setViewShowState(mBottomProgressBar, VISIBLE)
        updatePauseCover()
    }

    protected fun changeUiToPlayingBufferingClear() {
        Debuger.printfLog("changeUiToPlayingBufferingClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            val enDownloadView = mLoadingProgressBar as ENDownloadView
            if (enDownloadView.currentState == ENDownloadView.STATE_PRE) {
                (mLoadingProgressBar as ENDownloadView).start()
            }
        }
        updateStartImage()
    }

    protected fun changeUiToClear() {
        Debuger.printfLog("changeUiToClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
    }

    protected fun changeUiToCompleteClear() {
        Debuger.printfLog("changeUiToCompleteClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    /**
     * 定义开始按键显示
     */
    protected fun updateStartImage() {
        if (mStartButton is ENPlayView) {
            val enPlayView = mStartButton as ENPlayView
            enPlayView.setDuration(500)
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                enPlayView.play()
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                enPlayView.pause()
            } else {
                enPlayView.pause()
            }
        } else if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.ic_icon_pause)
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.video_click_error_selector)
            } else {
                imageView.setImageResource(R.drawable.ic_icon_play)
            }
        }
    }

    protected fun updateMuteImage() {
        val isMute = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)
        if (isMute) {
            mMuteIv!!.setImageResource(R.drawable.ic_icon_mute)
        } else {
            mMuteIv!!.setImageResource(R.drawable.ic_icon_unmute)
        }
    }

    /**
     * 全屏的UI逻辑
     */
    private fun initFullUI(dcVideoPlayer: DcVideoPlayer) {
        dcVideoPlayer.postDelayed({ dcVideoPlayer.mOrientationUtils.releaseListener() }, 500)
        dcVideoPlayer.titleTextView?.let {
            it.text = mTitle
            it.setTextColor(Color.WHITE)
            it.textSize = 16f
            Typeface.DEFAULT_BOLD
        }
        val orientation = dcVideoPlayer.findViewById<ImageView>(R.id.orientation)
        if (orientation != null) {
            orientation.visibility = VISIBLE
            orientation.setOnClickListener {
                dcVideoPlayer.let {
                    it.mOrientationUtils.resolveByClick()
                    setOrientationUI(it)
                }
            }
        }
        if (mBottomProgressDrawable != null) {
            dcVideoPlayer.setBottomProgressBarDrawable(mBottomProgressDrawable)
        }
        if (mBottomShowProgressDrawable != null && mBottomShowProgressThumbDrawable != null) {
            dcVideoPlayer.setBottomShowProgressBarDrawable(
                mBottomShowProgressDrawable,
                mBottomShowProgressThumbDrawable
            )
        }
        if (mVolumeProgressDrawable != null) {
            dcVideoPlayer.setDialogVolumeProgressBar(mVolumeProgressDrawable)
        }
        if (mDialogProgressBarDrawable != null) {
            dcVideoPlayer.setDialogProgressBar(mDialogProgressBarDrawable)
        }
        if (mDialogProgressHighLightColor != -11 && mDialogProgressNormalColor != -11) {
            dcVideoPlayer.setDialogProgressColor(
                mDialogProgressHighLightColor,
                mDialogProgressNormalColor
            )
        }
    }

    private fun setOrientationUI(it: DcVideoPlayer) {
        if (it.mOrientationUtils.isLand == 0) {
            it.mMuteIv?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    topToBottom = ConstraintLayout.LayoutParams.UNSET
                }
            }
            it.mProgressBar?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToEnd = R.id.mute
                    startToStart = ConstraintLayout.LayoutParams.UNSET
                }
            }
        } else {
            it.mMuteIv?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    topToBottom = R.id.progress
                    topToTop = ConstraintLayout.LayoutParams.UNSET
                }
            }
            it.mProgressBar?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    startToEnd = ConstraintLayout.LayoutParams.UNSET
                }
            }
        }
    }

    /**
     * 底部进度条-弹出的
     */
    fun setBottomShowProgressBarDrawable(drawable: Drawable?, thumb: Drawable?) {
        mBottomShowProgressDrawable = drawable
        mBottomShowProgressThumbDrawable = thumb
        if (mProgressBar != null) {
            mProgressBar.progressDrawable = drawable
            mProgressBar.thumb = thumb
        }
    }

    /**
     * 底部进度条-非弹出
     */
    fun setBottomProgressBarDrawable(drawable: Drawable?) {
        mBottomProgressDrawable = drawable
        if (mBottomProgressBar != null) {
            mBottomProgressBar.progressDrawable = drawable
        }
    }

    /**
     * 声音进度条
     */
    fun setDialogVolumeProgressBar(drawable: Drawable?) {
        mVolumeProgressDrawable = drawable
    }

    /**
     * 中间进度条
     */
    fun setDialogProgressBar(drawable: Drawable?) {
        mDialogProgressBarDrawable = drawable
    }

    /**
     * 中间进度条字体颜色
     */
    fun setDialogProgressColor(highLightColor: Int, normalColor: Int) {
        mDialogProgressHighLightColor = highLightColor
        mDialogProgressNormalColor = normalColor
    }
    /**
     * 获取截图
     *
     * @param high 是否需要高清的
     */
    /************************************* 关于截图的  */
    /**
     * 获取截图
     */
    @JvmOverloads
    fun taskShotPic(gsyVideoShotListener: GSYVideoShotListener?, high: Boolean = false) {
        if (currentPlayer.renderProxy != null) {
            currentPlayer.renderProxy.taskShotPic(gsyVideoShotListener, high)
        }
    }

    /**
     * 保存截图
     */
    fun saveFrame(file: File?, gsyVideoShotSaveListener: GSYVideoShotSaveListener?) {
        saveFrame(file, false, gsyVideoShotSaveListener)
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    fun saveFrame(file: File?, high: Boolean, gsyVideoShotSaveListener: GSYVideoShotSaveListener?) {
        if (currentPlayer.renderProxy != null) {
            currentPlayer.renderProxy.saveFrame(file, high, gsyVideoShotSaveListener)
        }
    }

    /**
     * 重新开启进度查询以及控制view消失的定时任务
     * 用于解决GSYVideoHelper中通过removeview方式做全屏切换导致的定时任务停止的问题
     * GSYVideoControlView   onDetachedFromWindow（）
     */
    fun restartTimerTask() {
        startProgressTimer()
        startDismissControlViewTimer()
    }
}