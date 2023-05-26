package com.qtk.kotlintest.activities

import android.Manifest
import android.content.DialogInterface
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.flexbox.FlexboxLayoutManager
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.FlowAdapter
import com.qtk.kotlintest.base.BaseActivity
import com.qtk.kotlintest.databinding.ActivityFlowLayoutBinding
import com.tencent.soter.core.model.ConstantsSoter
import com.tencent.soter.wrapper.SoterWrapperApi
import com.tencent.soter.wrapper.wrap_biometric.SoterBiometricCanceller
import com.tencent.soter.wrapper.wrap_biometric.SoterBiometricStateCallback
import com.tencent.soter.wrapper.wrap_task.AuthenticationParam
import java.util.concurrent.Executor

@RequiresApi(Build.VERSION_CODES.P)
class FlowActivity: BaseActivity<ActivityFlowLayoutBinding>(R.layout.activity_flow_layout) {
    private val finger = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it == true) {
            biometricPrompt.authenticate(cancel, executor, authenticationCallback)
        }
    }

    private val cancel = CancellationSignal()

    private val handler = Handler(Looper.getMainLooper())

    private val executor = Executor {
        handler.post(it)
    }

    private val biometricPrompt = BiometricPrompt.Builder(this)
        .setTitle("指纹验证")
        .setSubtitle("请验证指纹")
        .setDescription("请将手指放在指纹传感器上")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .setNegativeButton("取消", executor) { dialog, which ->
            // 用户点击了取消按钮
        }
        .build()

    private val authenticationCallback = object: BiometricPrompt.AuthenticationCallback(){
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.flowRv.layoutManager = FlexboxLayoutManager(this)
        val data = listOf(
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
        )
        binding.flowRv.adapter = FlowAdapter(data) { _, _ -> }
//        finger.launch(Manifest.permission.USE_BIOMETRIC)
        SoterWrapperApi.prepareAuthKey({}, false, true, 0, null, null)
        val param = AuthenticationParam.AuthenticationParamBuilder()
            .setScene(0)
            .setContext(this)
            .setBiometricType(ConstantsSoter.FINGERPRINT_AUTH)
            .setSoterBiometricCanceller(SoterBiometricCanceller())
            .setPrefilledChallenge("test challenge")
            .setSoterBiometricStateCallback(object: SoterBiometricStateCallback{
                override fun onStartAuthentication() {

                }

                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {

                }

                override fun onAuthenticationSucceed() {

                }

                override fun onAuthenticationFailed() {

                }

                override fun onAuthenticationCancelled() {

                }

                override fun onAuthenticationError(errorCode: Int, errorString: CharSequence?) {

                }

            })
            .build()
        SoterWrapperApi.requestAuthorizeAndSign({}, param)
    }
}