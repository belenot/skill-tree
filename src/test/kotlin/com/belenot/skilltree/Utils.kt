package com.belenot.skilltree

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID


 fun generateNodes() = generateSequence { newUUID() }
    .map { Skill(it, it) }
    .map { val id = newUUID(); id to Node(id, skill = it) }
    .take(1).toMap()
