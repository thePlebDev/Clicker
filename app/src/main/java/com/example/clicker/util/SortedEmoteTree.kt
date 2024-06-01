package com.example.clicker.util

class SortedEmoteTree<E> {
    private var root:EmoteNode<E>? = null
    private val size:Int = 0


    fun insertNode(node:EmoteNode<E>){
        val rootTest = root
        if(rootTest == null){
            root = node
        }else{
            traverseInsertNode(rootTest,node)
        }
    }
    private fun traverseInsertNode(node:EmoteNode<E>, newNode:EmoteNode<E>){
        if(node.clicks > newNode.clicks){
            if(node.left == null){
                node.copy(
                    left = newNode
                )
            }
            else{
                traverseInsertNode(node.left,newNode)
            }
        }
        else{
            if(node.right ==null){
                node.copy(
                    right = newNode
                )
            }else{
                traverseInsertNode(node.right,newNode)
            }
        }
    }

    fun inOrderTraverse(){
        val rootTest = root
        if(rootTest == null){
            println("null")
        }else{
            inOrderTraversing(rootTest)
        }
    }
    private fun inOrderTraversing(node: EmoteNode<E>){
        if(node.left != null){
            inOrderTraversing(node.left)
        }else{
            println("${node.clicks} -->")
        }
        if(node.right != null){
            inOrderTraversing(node.right)
        }else{
            println("${node.clicks} -->")
        }
    }

}


data class EmoteNode<E>(
//     val element:E,
     val clicks:Int, //for right now clicks is what is being compared
//     val amountsClicked:Int,
     val left:EmoteNode<E>?,
     val right:EmoteNode<E>?,
//     val parent:E?
)