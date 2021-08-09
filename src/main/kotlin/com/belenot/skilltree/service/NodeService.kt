package com.belenot.skilltree.service

import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class NodeService {

    // TODO move to repository
    private val nodes  = mutableMapOf<String, Node>()
    // TODO move to constructor
    @Autowired
    private lateinit var skillService: SkillService

    fun getNode(page: Int, size: Int) =
        nodes.values.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()

    fun getNode(id: String) = if (nodes.containsKey(id)) nodes[id] else null

    fun createNode(skillId: String, childrenIds: List<String> = emptyList(), parentId: String? = null): Node {
        val skill = skillService.getSkill(skillId)
        if (skill != null) {
            val node = Node(newUUID(), children = childrenIds.mapNotNull { nodes[it] }, skill = skill, parent = nodes[parentId])
            val id = node.id
            nodes[id] = node
            return node
        } else {
            // TODO throw service exception
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found skill with id = ${skillId}.")
        }
    }

    fun deleteNode(id: String) = nodes.remove(id)

    fun replaceNode(id: String, skillId: String, childrenIds: List<String> = emptyList(), parentId: String? = null): Node? {
        if (nodes.containsKey(id)) {
            val skill = skillService.getSkill(skillId)
            if (skill != null) {
                val node = Node(id, children = childrenIds.mapNotNull { nodes[it] }, skill = skill, parent = nodes[parentId])
                nodes[id] = node
                return node
            } else {
                // TODO throw service exception
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found skill with id = ${skillId}.")
            }
        } else {
            return null
        }
    }
}