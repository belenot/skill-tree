package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.controller.PostTree
import com.belenot.skilltree.controller.PutTree
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.repository.TreeRepository
import com.belenot.skilltree.utils.paged
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TreeService(val treeRepository: TreeRepository, val nodeService: NodeService, val skillService: SkillService) {

    fun getTree(page: Int, size: Int) = treeRepository.getTree(page, size)

    fun getTree(id: String) = treeRepository.getTree(id)

    fun createTree(rootId: String, description: String): Tree {
        val root = nodeService.getNode(rootId)
        if (root != null) {
            return treeRepository.createTree(description, root)
        } else {
            throw SkillTreeException("Not found root node with id = ${rootId}.")
        }
    }

    fun deleteTree(id: String): Tree? = treeRepository.removeTree(id)

    fun replaceTree(id: String, rootId: String, description: String): Tree? {
        if (treeRepository.exists(id)) {
            val root = nodeService.getNode(rootId)
            if (root != null) {
                return treeRepository.updateTree(id, description, root)
            } else {
                throw SkillTreeException("Not found root node with id = ${rootId}.")
            }
        } else {
            return null
        }
    }
}