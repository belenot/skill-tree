package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.controller.PostTree
import com.belenot.skilltree.controller.PutTree
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Tree
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TreeService(val trees: MutableMap<String, Tree>, val nodeService: NodeService, val skillService: SkillService) {

    companion object {
        @JvmStatic
        val GET_TREE_VALIDATION_VIOLATION = "Page must be greater then or equal zero and size must be greater then zero."
    }

    // TODO extract paging to utility method
    fun getTree(page: Int, size: Int) = if (page < 0 || size <= 0)
        throw SkillTreeException(GET_TREE_VALIDATION_VIOLATION)
        else trees.values.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()

    fun getTree(id: String) = if (trees.containsKey(id)) trees[id]  else null

    fun createTree(rootId: String, description: String): Tree {
        val root = nodeService.getNode(rootId)
        if (root != null) {
            val tree = Tree(id = newUUID(), root = root, description = description)
            val id = tree.id
            trees[id] = tree
            return tree
        } else {
            throw SkillTreeException("Not found root node with id = ${rootId}.")
        }
    }

    fun deleteTree(id: String) = trees.remove(id)

    fun replaceTree(id: String, rootId: String, description: String): Tree? {
        if (trees.containsKey(id)) {
            val root = nodeService.getNode(rootId)
            if (root != null) {
                val tree = Tree(id = id, root = root, description = description)
                trees[id] = tree
                return tree
            } else {
                throw SkillTreeException("Not found root node with id = ${rootId}.")
            }
        } else {
            return null
        }
    }
}