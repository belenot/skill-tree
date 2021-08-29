package com.belenot.skilltree.repository

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.utils.paged
import org.springframework.stereotype.Repository

@Repository
class NodeRepository(val nodes: MutableMap<String, Node>) {
    fun getNode(page: Int, size: Int): List<Node> = paged(nodes.values, page, size)
    fun getNode(id: String): Node? = if (nodes.containsKey(id)) nodes[id] else null
    fun createNode(childrenIds: Set<String>, skill: Skill, parentId: String?): Node = Node(newUUID(),
        children = childrenIds.mapNotNull { nodes[it] }.toSet(),
        skill = skill,
        parent = nodes[parentId])
    fun removeNode(id: String): Node? = nodes.remove(id)
    fun exists(id: String): Boolean = nodes.containsKey(id)
    fun updateNode(id: String, childrenIds: Set<String>, skill: Skill, parentId: String?): Node =
        Node(id,
            children = childrenIds.mapNotNull { nodes[it] }.toSet(),
            skill = skill,
            parent = nodes[parentId])
            // .also { nodes.replace(id, it) }
}