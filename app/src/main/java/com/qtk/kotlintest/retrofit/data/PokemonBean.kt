package com.qtk.kotlintest.retrofit.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PokemonListBean(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonBean>
): Parcelable

@Parcelize
data class PokemonBean(
    val name: String = "",
    var url: String = ""
): Parcelable

fun getImageUrl(url: String): String {
    val index = url.split("/".toRegex()).dropLast(1).last()
    return "https://pokeres.bastionbot.org/images/pokemon/$index.png"
}