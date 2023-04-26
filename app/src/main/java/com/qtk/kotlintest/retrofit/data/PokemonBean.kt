package com.qtk.kotlintest.retrofit.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PokemonListBean(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonBean>
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class PokemonBean(
    var id: String = "",
    val name: String = "",
    var url: String = "",
    var like: Boolean = false
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is PokemonBean) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

fun getImageUrl(url: String): String {
    return "https://pokeres.bastionbot.org/images/pokemon/${getId(url)}.png"
}

fun getId(url: String): String {
    return url.split("/".toRegex()).dropLast(1).last()
}