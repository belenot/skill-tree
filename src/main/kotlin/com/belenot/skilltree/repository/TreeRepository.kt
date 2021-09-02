package com.belenot.skilltree.repository

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.utils.paged

open class TreeRepository(val trees: MutableMap<String, Tree> = mutableMapOf()) {
    open fun getTree(page: Int, size: Int): List<Tree> = paged(trees.values, page, size)
    open fun getTree(id: String): Tree? = trees[id]
    open fun containsId(id: String): Boolean = trees.containsKey(id)
    open fun createTree(description: String, root: Node): Tree =
        Tree(id = newUUID(), root = root, description = description)
            .also { trees[it.id] = it }

    open fun removeTree(id: String) = trees.remove(id)
    open fun updateTree(id: String, description: String, root: Node): Tree =
        Tree(id = id, root = root, description = description)
            .also { trees[id] = it }
}