package com.belenot.skilltree.repository

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.generateNodes
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class NodeRepositoryTest {
    lateinit var nodeRepository: NodeRepository
    lateinit var nodes: MutableMap<String, Node>

    @BeforeEach
    fun beforeEach() {
        nodes = mutableMapOf()
        nodeRepository = NodeRepository(nodes)
    }

    @Test
    fun `When create node Then add node to collection`() {
        val skill = Skill(newUUID(), "skill")
        val nodesBefore = nodes.map { it.value }.toSet()
        val newNode = nodeRepository.createNode(emptySet(), skill, null)
        val nodesAfter = nodes.map {it.value}.toSet()
        assertThat(newNode.id).isNotBlank()
        assertThat(newNode.skill.id).isEqualTo(skill.id)
        assertThat(newNode.skill.title).isEqualTo(skill.title)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodes).containsValue(newNode)
    }

    @Test
    fun `Given non existing id When get node Then return null`() {
        val node = nodeRepository.getNode(newUUID())
        assertThat(node).isNull()
    }

    @Test
    fun `When get nodes Then return nodes collection`() {
        val actualnodes = nodeRepository.getNode(0, 1)
        assertThat(actualnodes).isNotNull()
    }

    @Test
    fun `When create node Then return new node without children and parent and add node to collection`() {
        val skill = Skill(newUUID(), "new skill")
        val nodesBefore = nodes.map { it.value }.toSet()
        val node = nodeRepository.createNode(skill = skill)
        val nodesAfter = nodes.map {it.value}.toSet()
        assertThat(node.id).isNotBlank()
        assertThat(node.skill).isEqualTo(skill)
        assertThat(node.children).isEmpty()
        assertThat(node.parent).isNull()
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodes).containsValue(node)
    }

    @Test
    fun `Given parent When create node Then return new node without children but with parent and add node to collection`() {
        val parentSkill = Skill(newUUID(), "parent skill")
        val skill = Skill(newUUID(), "new skill")
        val parentNode = Node(newUUID(), skill = parentSkill)
        nodes[parentNode.id] = parentNode
        val nodesBefore = nodes.map { it.value }.toSet()
        val node = nodeRepository.createNode(skill = skill, parentId = parentNode.id)
        val nodesAfter = nodes.map {it.value}.toSet()
        assertThat(node.id).isNotBlank()
        assertThat(node.skill).isEqualTo(skill)
        assertThat(node.children).isEmpty()
        assertThat(node.parent).isEqualTo(parentNode)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodes).containsValue(node)
    }

    @Test
    fun `Given children When create node Then return new node with children but without parent and add node to collection`() {
        val children = generateNodes()
        nodes.putAll(children)
        val skill = Skill(newUUID(), "new skill")
        val nodesBefore = nodes.map { it.value }.toSet()
        val node = nodeRepository.createNode(skill = skill, childrenIds = children.keys)
        val nodesAfter = nodes.map {it.value}.toSet()
        assertThat(node.id).isNotBlank()
        assertThat(node.skill).isEqualTo(skill)
        assertThat(node.children).isEqualTo(children.values)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodes).containsValue(node)
    }

    @Test
    fun `When delete Node then remove node from collection`() {
        val node = Node(newUUID(), skill = Skill(newUUID(), ""))
        nodes[node.id] = node
        assertThat(nodes).containsKey(node.id)
        val nodesBefore = nodes.map { it.value }.toSet()
        val deletedNode = nodeRepository.removeNode(node.id)
        val nodesAfter = nodes.map {it.value}.toSet()
        assertThat(deletedNode).isEqualTo(node)
        assertThat(nodes).doesNotContainKey(node.id)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodes).doesNotContainValue(node)
    }

    @Test
    fun `Given non existing id When remove node Then return null and do nothing with collection`() {
        val nodesBefore = nodes.map { it }.toSet()
        val removedNode = nodeRepository.removeNode(newUUID())
        val nodesAfter = nodes.map { it }.toSet()
        assertThat(nodesBefore).isEqualTo(nodesAfter)
        assertThat(removedNode).isNull()
    }

    @Test
    fun `Given identical parameters When update node Then replace Node idempotent`() {
        val skill = Skill(newUUID(), "")
        val node = Node(newUUID(), skill = skill)
        nodes[node.id] = node
        val nodesBefore = nodes.map { it.value }.toSet()
        val replacedNode = nodeRepository.updateNode(node.id, skill = node.skill)
        val nodesAfter = nodes.map { it.value }.toSet()
        assertThat(replacedNode).isEqualTo(node)
        assertThat(nodesBefore).isEqualTo(nodesAfter)
    }

    @Test
    fun `Given node without parent When update node with parent Then replace Node with parent`() {
        val skill = Skill(newUUID(), "skill")
        val node = Node(newUUID(), skill = skill)
        val parentSkill = Skill(newUUID(), "parent skill")
        val parentNode = Node(newUUID(), skill = parentSkill)
        nodes[parentNode.id] = parentNode
        nodes[node.id] = node
        val nodesBefore = nodes.map { it.value }.toSet()
        val replacedNode = nodeRepository.updateNode(node.id, skill = node.skill, parentId = parentNode.id)
        val nodesAfter = nodes.map { it.value }.toSet()
        assertThat(replacedNode).isNotNull()
        assertThat(node.id).isEqualTo(replacedNode.id)
        assertThat(replacedNode).isNotEqualTo(node)
        assertThat(replacedNode.parent).isEqualTo(parentNode)
        assertThat(nodes[replacedNode.id]).isEqualTo(replacedNode)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodesBefore).contains(node)
        assertThat(nodesAfter).contains(replacedNode)
    }

    @Test
    fun `Given node with parent When update node with parent Then replace Node with parent`() {
        val parentSkill = Skill(newUUID(), "parent skill")
        val anotherParentSkill = Skill(newUUID(), "parent skill")
        val originalParentNode = Node(newUUID(), skill = parentSkill)
        val replacedParentNode = Node(newUUID(), skill = anotherParentSkill)
        val skill = Skill(newUUID(), "skill")
        val originalNode = Node(newUUID(), skill = skill, parent = originalParentNode)
        nodes[originalNode.id] = originalNode
        nodes[originalParentNode.id] = originalParentNode
        nodes[replacedParentNode.id] = replacedParentNode
        val nodesBefore = nodes.map { it.value }.toSet()
        val replacedNode =
            nodeRepository.updateNode(originalNode.id, skill = originalNode.skill, parentId = replacedParentNode.id)
        val nodesAfter = nodes.map { it.value }.toSet()
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode).isNotEqualTo(originalNode)
        assertThat(replacedNode.parent).isNotEqualTo(originalParentNode)
        assertThat(replacedNode.parent).isEqualTo(replacedParentNode)
        assertThat(nodes[replacedNode.id]).isEqualTo(replacedNode)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodesAfter).contains(replacedNode)
        assertThat(nodesAfter).doesNotContain(originalNode)
    }

    @Test
    fun `Given node without children When update node with children Then replace Node with children`() {
        val children = generateNodes()
        val skill = Skill(newUUID(), "skill")
        val originalNode = Node(newUUID(), skill = skill)

        nodes[originalNode.id] = originalNode
        nodes.putAll(children)
        val nodesBefore = nodes.map { it.value }.toSet()
        val replacedNode =
            nodeRepository.updateNode(originalNode.id, skill = originalNode.skill, childrenIds = children.keys)
        val nodesAfter = nodes.map { it.value }.toSet()
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode).isNotEqualTo(originalNode)
        assertThat(replacedNode.children).isNotEmpty()
        assertThat(replacedNode.children).isEqualTo(children.values)
        assertThat(nodes[replacedNode.id]).isEqualTo(replacedNode)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodesAfter).contains(replacedNode)
        assertThat(nodesAfter).doesNotContain(originalNode)
    }

    @Test
    fun `Given node with children When update node with children Then replace Node with children`() {
        val originalChildren = generateNodes()
        val replacedChildren = generateNodes()
        val skill = Skill(newUUID(), "skill")
        val originalNode = Node(newUUID(), skill = skill)

        nodes[originalNode.id] = originalNode
        nodes.putAll(originalChildren)
        nodes.putAll(replacedChildren)
        val nodesBefore = nodes.map { it.value }.toSet()
        val replacedNode = nodeRepository.updateNode(originalNode.id, skill = originalNode.skill, childrenIds = replacedChildren.keys)
        val nodesAfter = nodes.map { it.value }.toSet()
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode).isNotEqualTo(originalNode)
        assertThat(replacedNode.children).isNotEqualTo(originalChildren)
        assertThat(replacedNode.children).isEqualTo(replacedChildren.values)
        assertThat(nodes[replacedNode.id]).isEqualTo(replacedNode)
        assertThat(nodesBefore).isNotEqualTo(nodesAfter)
        assertThat(nodesAfter).contains(replacedNode)
        assertThat(nodesAfter).doesNotContain(originalNode)
    }


    @Test
    fun `Given node id and there exists node with this id When call containsId Then return true`() {
        val nodeId = newUUID()
        val node = Node(nodeId, skill = Skill(newUUID(), "skill"))
        nodes[nodeId] = node

        val actualResult = nodeRepository.containsId(nodeId)

        assertThat(actualResult).isTrue()
    }

    @Test
    fun `Given skill id and there doesnt exist skill with this id When call containsId Then return false`() {
        val skillId = newUUID()

        val actualResult = nodeRepository.containsId(skillId)

        assertThat(actualResult).isFalse()
    }

}