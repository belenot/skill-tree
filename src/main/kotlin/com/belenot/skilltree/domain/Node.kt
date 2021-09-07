package com.belenot.skilltree.domain

data class Node (
    val id: String,
    val childrenIds: Collection<String> = emptySet(),
    val skill: Skill,
    val parentId: String? = null
)