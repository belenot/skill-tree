package com.belenot.skilltree.domain

data class Node (
    val id: String,
    val children: Set<Node> = emptySet(),
    val skill: Skill,
    val parent: Node? = null
)