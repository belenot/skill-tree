package com.belenot.skilltree.repository

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.utils.paged

class TreeRepository(val trees: MutableMap<String, Tree>) {
    fun getTree(page: Int, size: Int): List<Tree> = paged(trees.values, page, size)
    fun getTree(id: String): Tree? = trees[id]
    fun exists(id: String): Boolean = trees.containsKey(id)
    fun createTree(description: String, root: Node): Tree =
        Tree(id = newUUID(), root = root, description = description)

    fun removeTree(id: String) = trees.remove(id)
    fun updateTree(id: String, description: String, root: Node): Tree =
        Tree(id = id, root = root, description = description)
            .also { trees.replace(id, it) }
}