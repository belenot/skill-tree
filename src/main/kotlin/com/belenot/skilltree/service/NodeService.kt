package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.repository.NodeRepository
import org.springframework.stereotype.Service

@Service
open class NodeService(val nodeRepository: NodeRepository, val skillService: SkillService) {

    open fun getNode(page: Int, size: Int) = nodeRepository.getNode(page, size)

    open fun getNode(id: String): Node? = nodeRepository.getNode(id)

    open fun createNode(skillId: String, childrenIds: Set<String> = emptySet(), parentId: String? = null): Node {
        val skill = skillService.getSkill(skillId)
        if (skill != null) {
            return nodeRepository.createNode(childrenIds.toSet(), skill, parentId)
        } else {
            throw SkillTreeException("Not found skill with id = ${skillId}")
        }
    }

    open fun deleteNode(id: String): Node? = nodeRepository.removeNode(id)

    open fun replaceNode(id: String, skillId: String, childrenIds: Set<String> = emptySet(), parentId: String? = null): Node? {
        if (nodeRepository.containsId(id)) {
            val skill = skillService.getSkill(skillId)
            if (skill != null) {
                val node = nodeRepository.updateNode(id, childrenIds.toSet(), skill, parentId)
                return node
            } else {
                throw SkillTreeException("Not found skill with id = ${skillId}")
            }
        } else {
            return null
        }
    }
}