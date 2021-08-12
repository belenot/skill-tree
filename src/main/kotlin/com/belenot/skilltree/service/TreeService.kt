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
class TreeService {

    // TODO move to repository
    private val trees   = mutableMapOf<String, Tree>()

    // TODO move to constructor
    @Autowired
    private lateinit var nodeService: NodeService
    @Autowired
    private lateinit var skillService: SkillService

    fun getTree(page: Int, size: Int) =
        trees.values.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()

    fun getTree(id: String) = if (trees.containsKey(id)) trees[id]  else null

    fun createTree(postTree: PostTree): Tree {
        val root = nodeService.getNode(postTree.rootId)
        if (root != null) {
            val tree = createTree(postTree, root)
            val id = tree.id
            trees[id] = tree
            return tree
        } else {
            //
            throw SkillTreeException("Not found root node with id = ${postTree.rootId}.")
        }
    }

    private fun createTree(
        postTree: PostTree,
        root: Node,
        id: String = newUUID()
    ) = Tree(
        id = id,
        root = root,
        description = postTree.description
    )

    fun deleteTree(id: String) = trees.remove(id)

    fun replaceTree(id: String, putTree: PutTree): Tree? {
        if (trees.containsKey(id)) {
            val root = nodeService.getNode(putTree.rootId)
            if (root != null) {
                val tree = createTree(putTree, root, id = id)
                trees[id] = tree
                return tree
            } else {
                throw SkillTreeException("Not found root node with id = ${putTree.rootId}.")
            }
        } else {
            return null
        }
    }
}