package com.belenot.skilltree.domain

data class Node (
    val id: String,
    val children: List<Node> = emptyList(),
    val skill: Skill,
    val parent: Node? = null
)