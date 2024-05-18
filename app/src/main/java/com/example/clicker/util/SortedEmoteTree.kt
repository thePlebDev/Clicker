package com.example.clicker.util

class SortedEmoteTree<E> {
    private val root:EmoteNode<E>? = null
    private val size:Int = 0
}


data class EmoteNode<E>(
     val element:E,
     val amountsClicked:Int,
     val left:E,
     val right:E,
     val parent:E
)