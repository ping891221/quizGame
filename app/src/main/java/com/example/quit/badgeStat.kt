package com.example.quit
//這裡的東西只要關掉數據就會重來^^
object badgeStat {
    //使用者id
    var nowUserUid: String? = null
    //是否有用道具
    val hintPhotoStates = List(10) { Hint1State() }
    val hintResponeStates = List(10) { Hint1State() }
    val hintConverStates = List(10) { Hint1State() }
    val hintTalkStates = List(10) { Hint1State() }
    //是否過關
    val okPhoto = List(10){ok1()}
    val okRes = List(10){ok2()}
    val okConver = List(10){ok3()}
    val okTalks = List(10){ok4()}
    //根據獎章的要求設定
}
data class Hint1State(var con: Boolean = false, var trans: Boolean = false)
data class Hint2State(var con: Boolean = false, var trans: Boolean = false)
data class Hint3State(var con: Boolean = false, var trans: Boolean = false)
data class Hint4State(var con: Boolean = false, var trans: Boolean = false)
data class ok1(var ok: Boolean = false)
data class ok2(var ok: Boolean = false)
data class ok3(var ok: Boolean = false)
data class ok4(var ok: Boolean = false)