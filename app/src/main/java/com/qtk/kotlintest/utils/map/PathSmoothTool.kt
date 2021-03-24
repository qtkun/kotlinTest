package com.qtk.kotlintest.utils.map

import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import java.util.ArrayList
import kotlin.math.sqrt

/**
 * 轨迹优化工具类
 * Created by my94493 on 2017/3/31.
 *
 *
 * 使用方法：
 *
 *
 * PathSmoothTool pathSmoothTool = new PathSmoothTool();
 * pathSmoothTool.setIntensity(2);//设置滤波强度，默认3
 * List<LatLng> mList = LatpathSmoothTool.kalmanFilterPath(list);
</LatLng> */
class PathSmoothTool {
    var intensity = 3
    var threshhold = 0.3f
    private var mNoiseThreshhold = 10f
    fun setNoiseThreshhold(mnoiseThreshhold: Float) {
        mNoiseThreshhold = mnoiseThreshhold
    }

    /**
     * 轨迹平滑优化
     * @param originlist 原始轨迹list,list.size大于2
     * @return 优化后轨迹list
     */
    fun pathOptimize(originlist: List<LatLng>?): List<LatLng>? {
        val list = removeNoisePoint(originlist) //去噪
        val afterList = kalmanFilterPath(list, intensity) //滤波
        return reducerVerticalThreshold(afterList, threshhold)
    }

    /**
     * 轨迹线路滤波
     * @param originlist 原始轨迹list,list.size大于2
     * @return 滤波处理后的轨迹list
     */
    fun kalmanFilterPath(originlist: List<LatLng>?): List<LatLng> {
        return kalmanFilterPath(originlist, intensity)
    }

    /**
     * 轨迹去噪，删除垂距大于20m的点
     * @param originlist 原始轨迹list,list.size大于2
     * @return
     */
    fun removeNoisePoint(originlist: List<LatLng>?): List<LatLng>? {
        return reduceNoisePoint(originlist, mNoiseThreshhold)
    }

    /**
     * 单点滤波
     * @param lastLoc 上次定位点坐标
     * @param curLoc 本次定位点坐标
     * @return 滤波后本次定位点坐标值
     */
    fun kalmanFilterPoint(lastLoc: LatLng?, curLoc: LatLng): LatLng? {
        return kalmanFilterPoint(lastLoc, curLoc, intensity)
    }

    /**
     * 轨迹抽稀
     * @param inPoints 待抽稀的轨迹list，至少包含两个点，删除垂距小于mThreshhold的点
     * @return 抽稀后的轨迹list
     */
    fun reducerVerticalThreshold(inPoints: List<LatLng>?): List<LatLng>? {
        return reducerVerticalThreshold(inPoints, threshhold)
    }
    /** */
    /**
     * 轨迹线路滤波
     * @param originlist 原始轨迹list,list.size大于2
     * @param intensity 滤波强度（1—5）
     * @return
     */
    private fun kalmanFilterPath(originlist: List<LatLng>?, intensity: Int): List<LatLng> {
        val kalmanFilterList: MutableList<LatLng> = ArrayList()
        if (originlist == null || originlist.size <= 2) return kalmanFilterList
        initial() //初始化滤波参数
        var latLng: LatLng? = null
        var lastLoc = originlist[0]
        kalmanFilterList.add(lastLoc)
        for (i in 1 until originlist.size) {
            val curLoc = originlist[i]
            latLng = kalmanFilterPoint(lastLoc, curLoc, intensity)
            if (latLng != null) {
                kalmanFilterList.add(latLng)
                lastLoc = latLng
            }
        }
        return kalmanFilterList
    }

    /**
     * 单点滤波
     * @param lastLoc 上次定位点坐标
     * @param curLoc 本次定位点坐标
     * @param intensity 滤波强度（1—5）
     * @return 滤波后本次定位点坐标值
     */
    private fun kalmanFilterPoint(lastLoc: LatLng?, curLoc: LatLng, intensity: Int): LatLng? {
        var curLocVar: LatLng? = curLoc
        var intensityVar = intensity
        if (pdelt_x == 0.0 || pdelt_y == 0.0) {
            initial()
        }
        var kalmanLatlng: LatLng? = null
        if (lastLoc == null || curLocVar == null) {
            return kalmanLatlng
        }
        if (intensityVar < 1) {
            intensityVar = 1
        } else if (intensityVar > 5) {
            intensityVar = 5
        }
        for (j in 0 until intensityVar) {
            kalmanLatlng = kalmanFilter(
                lastLoc.longitude,
                curLocVar!!.longitude,
                lastLoc.latitude,
                curLocVar.latitude
            )
            curLocVar = kalmanLatlng
        }
        return kalmanLatlng
    }

    /***************************卡尔曼滤波开始 */
    private var lastLocation_x = 0.0 //上次位置
    private var currentLocation_x = 0.0 //这次位置
    private var lastLocation_y = 0.0 //上次位置
    private var currentLocation_y = 0.0 //这次位置
    private var estimate_x = 0.0 //修正后数据
    private var estimate_y = 0.0 //修正后数据
    private var pdelt_x = 0.0 //自预估偏差
    private var pdelt_y = 0.0 //自预估偏差
    private var mdelt_x = 0.0 //上次模型偏差
    private var mdelt_y = 0.0 //上次模型偏差
    private var gauss_x = 0.0 //高斯噪音偏差
    private var gauss_y = 0.0 //高斯噪音偏差
    private var kalmanGain_x = 0.0 //卡尔曼增益
    private var kalmanGain_y = 0.0 //卡尔曼增益
    private val m_R = 0.0
    private val m_Q = 0.0

    //初始模型
    private fun initial() {
        pdelt_x = 0.001
        pdelt_y = 0.001
        //        mdelt_x = 0;
//        mdelt_y = 0;
        mdelt_x = 5.698402909980532E-4
        mdelt_y = 5.698402909980532E-4
    }

    private fun kalmanFilter(
        oldValue_x: Double,
        value_x: Double,
        oldValue_y: Double,
        value_y: Double
    ): LatLng {
        lastLocation_x = oldValue_x
        currentLocation_x = value_x
        gauss_x = sqrt(pdelt_x * pdelt_x + mdelt_x * mdelt_x) + m_Q //计算高斯噪音偏差
        kalmanGain_x = sqrt(gauss_x * gauss_x / (gauss_x * gauss_x + pdelt_x * pdelt_x)) + m_R //计算卡尔曼增益
        estimate_x = kalmanGain_x * (currentLocation_x - lastLocation_x) + lastLocation_x //修正定位点
        mdelt_x = sqrt((1 - kalmanGain_x) * gauss_x * gauss_x) //修正模型偏差
        lastLocation_y = oldValue_y
        currentLocation_y = value_y
        gauss_y = sqrt(pdelt_y * pdelt_y + mdelt_y * mdelt_y) + m_Q //计算高斯噪音偏差
        kalmanGain_y = sqrt(gauss_y * gauss_y / (gauss_y * gauss_y + pdelt_y * pdelt_y)) + m_R //计算卡尔曼增益
        estimate_y = kalmanGain_y * (currentLocation_y - lastLocation_y) + lastLocation_y //修正定位点
        mdelt_y = sqrt((1 - kalmanGain_y) * gauss_y * gauss_y) //修正模型偏差
        return LatLng(estimate_y, estimate_x)
    }
    /***************************卡尔曼滤波结束 */
    /***************************抽稀算法 */
    private fun reducerVerticalThreshold(
        inPoints: List<LatLng>?,
        threshHold: Float
    ): List<LatLng>? {
        if (inPoints == null) {
            return null
        }
        if (inPoints.size <= 2) {
            return inPoints
        }
        val ret: MutableList<LatLng> = ArrayList()
        for (i in inPoints.indices) {
            val pre = getLastLocation(ret)
            val cur = inPoints[i]
            if (pre == null || i == inPoints.size - 1) {
                ret.add(cur)
                continue
            }
            val next = inPoints[i + 1]
            val distance = calculateDistanceFromPoint(cur, pre, next)
            if (distance > threshHold) {
                ret.add(cur)
            }
        }
        return ret
    }

    /***************************抽稀算法结束 */
    private fun reduceNoisePoint(inPoints: List<LatLng>?, threshHold: Float): List<LatLng>? {
        if (inPoints == null) {
            return null
        }
        if (inPoints.size <= 2) {
            return inPoints
        }
        val ret: MutableList<LatLng> = ArrayList()
        for (i in inPoints.indices) {
            val pre = getLastLocation(ret)
            val cur = inPoints[i]
            if (pre == null || i == inPoints.size - 1) {
                ret.add(cur)
                continue
            }
            val next = inPoints[i + 1]
            val distance = calculateDistanceFromPoint(cur, pre, next)
            if (distance < threshHold) {
                ret.add(cur)
            }
        }
        return ret
    }

    companion object {
        private fun getLastLocation(oneGraspList: List<LatLng>?): LatLng? {
            if (oneGraspList == null || oneGraspList.isEmpty()) {
                return null
            }
            val locListSize = oneGraspList.size
            return oneGraspList[locListSize - 1]
        }

        /**
         * 计算当前点到线的垂线距离
         * @param p 当前点
         * @param lineBegin 线的起点
         * @param lineEnd 线的终点
         */
        private fun calculateDistanceFromPoint(
            p: LatLng, lineBegin: LatLng,
            lineEnd: LatLng
        ): Double {
            val A = p.longitude - lineBegin.longitude
            val B = p.latitude - lineBegin.latitude
            val C = lineEnd.longitude - lineBegin.longitude
            val D = lineEnd.latitude - lineBegin.latitude
            val dot = A * C + B * D
            val len_sq = C * C + D * D
            val param = dot / len_sq
            val xx: Double
            val yy: Double
            if (param < 0 || (lineBegin.longitude == lineEnd.longitude
                        && lineBegin.latitude == lineEnd.latitude)
            ) {
                xx = lineBegin.longitude
                yy = lineBegin.latitude
                //            return -1;
            } else if (param > 1) {
                xx = lineEnd.longitude
                yy = lineEnd.latitude
                //            return -1;
            } else {
                xx = lineBegin.longitude + param * C
                yy = lineBegin.latitude + param * D
            }
            return AMapUtils.calculateLineDistance(p, LatLng(yy, xx))
                .toDouble()
        }
    }
}