package com.qtk.kotlintest.widget

import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val FLING_TRANSLATION_MAGNITUDE = 0.5f

private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.5f

class SpringEdgeEffect: RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
        return object: EdgeEffect(recyclerView.context) {
            private val isHorizontal = (recyclerView.layoutManager as? LinearLayoutManager)?.orientation == RecyclerView.HORIZONTAL

            private var translationAnim: SpringAnimation = SpringAnimation(
                recyclerView,
                if (isHorizontal) SpringAnimation.TRANSLATION_X else SpringAnimation.TRANSLATION_Y
            ).setSpring(
                SpringForce(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW)
            )
            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            override fun onRelease() {
                super.onRelease()
                if ((if (isHorizontal) recyclerView.translationX else recyclerView.translationY) != 0f) {
                    translationAnim.start()
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                val sign = if (direction == if (isHorizontal) DIRECTION_RIGHT else DIRECTION_BOTTOM) -1 else 1
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                translationAnim.cancel()
                translationAnim.setStartVelocity(translationVelocity).start()
            }

            override fun isFinished(): Boolean {
                return translationAnim.isRunning.not()
            }

            private fun handlePull(deltaDistance: Float) {
                if (isHorizontal) {
                    val sign = if (direction == DIRECTION_RIGHT) -1 else 1
                    val translationXDelta = sign * recyclerView.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    recyclerView.translationX += translationXDelta
                } else {
                    val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                    val translationYDelta = sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    recyclerView.translationY += translationYDelta
                }
                translationAnim.cancel()
            }
        }
    }
}