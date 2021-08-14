package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.paged
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
open class NodeService(val nodes: MutableMap<String, Node>, val skillService: SkillService) {

    open fun getNode(page: Int, size: Int) = paged(nodes.values, page, size)

    open fun getNode(id: String) = if (nodes.containsKey(id)) nodes[id] else null

    open fun createNode(skillId: String, childrenIds: Set<String> = emptySet(), parentId: String? = null): Node {
        val skill = skillService.getSkill(skillId)
        if (skill != null) {
            val node = Node(newUUID(), children = childrenIds.mapNotNull { nodes[it] }.toSet(), skill = skill, parent = nodes[parentId])
            val id = node.id
            nodes[id] = node
            return node
        } else {
            throw SkillTreeException("Not found skill with id = ${skillId}")
        }
    }

    open fun deleteNode(id: String) = nodes.remove(id)

    open fun replaceNode(id: String, skillId: String, childrenIds: Set<String> = emptySet(), parentId: String? = null): Node? {
        if (nodes.containsKey(id)) {
            val skill = skillService.getSkill(skillId)
            if (skill != null) {
                val node = Node(id, children = childrenIds.mapNotNull { nodes[it] }.toSet(), skill = skill, parent = nodes[parentId])
                nodes[id] = node
                return node
            } else {
                throw SkillTreeException("Not found skill with id = ${skillId}")
            }
        } else {
            return null
        }
    }
}