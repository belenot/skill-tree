package com.belenot.skilltree.repository

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.utils.paged
import org.springframework.stereotype.Repository

@Repository
open class NodeRepository(val nodes: MutableMap<String, Node>  = mutableMapOf()) {
    open fun getNode(page: Int, size: Int): List<Node> = paged(nodes.values, page, size)
    open fun getNode(id: String): Node? = if (nodes.containsKey(id)) nodes[id] else null
    open fun createNode(childrenIds: Set<String> = emptySet(), skill: Skill, parentId: String? = null): Node = Node(newUUID(),
        childrenIds = childrenIds,
        skill = skill,
        parentId = parentId)
        .also { nodes[it.id] = it }
    open fun removeNode(id: String): Node? = nodes.remove(id)
    open fun containsId(id: String): Boolean = nodes.containsKey(id)
    open fun updateNode(id: String, childrenIds: Set<String> = emptySet(), skill: Skill, parentId: String? = null): Node =
        Node(id,
            childrenIds = childrenIds,
            skill = skill,
            parentId = parentId)
            .also { nodes[it.id] = it }
}