package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class NodeServiceTest {

    lateinit var nodeService: NodeService
    lateinit var skillService: SkillService
    lateinit var nodes: MutableMap<String, Node>

    @BeforeEach
    fun beforeEach() {
        skillService = mock(SkillService::class.java)
        nodes = mutableMapOf()
        nodeService = NodeService(nodes, skillService)
    }

    @Test
    fun `Given unknown skillId When createNode Then throw SkillTreeException`() {
        val skillId = newUUID()
        `when`(skillService.getSkill(skillId)).thenReturn(null)
        assertThatThrownBy { nodeService.createNode(skillId) }.isExactlyInstanceOf(SkillTreeException::class.java)
    }

    @Test
    fun `When get node Then return node`() {
        val skill = Skill(newUUID(), "skill")
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        val node = nodeService.createNode(skill.id)
        assertThat(node.id).isNotBlank()
        assertThat(node.skill.id).isEqualTo(skill.id)
        assertThat(node.skill.title).isEqualTo(skill.title)
    }

    @Test
    fun `Given non existing id When get node Then return null`() {
        val node = nodeService.getNode(newUUID())
        assertThat(node).isNull()
    }

    @Test
    fun `When get nodes Then return nodes collection`() {
        val actualnodes = nodeService.getNode(0, 1)
        assertThat(actualnodes).isNotNull()
    }

    @Test
    fun `When create node Then return new node without children and parent`() {
        val skill = Skill(newUUID(), "new skill")
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        val node = nodeService.createNode(skill.id)
        assertThat(node.id).isNotBlank()
        assertThat(node.skill).isEqualTo(skill)
        assertThat(node.children).isEmpty()
        assertThat(node.parent).isNull()
    }

    @Test
    fun `Given parent When create node Then return new node without children but with parent`() {
        val parentSkill = Skill(newUUID(), "parent skill")
        val skill = Skill(newUUID(), "new skill")
        val parentNode = Node(newUUID(), skill = parentSkill)
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        nodes[parentNode.id] = parentNode
        val node = nodeService.createNode(skill.id, parentId = parentNode.id)
        assertThat(node.id).isNotBlank()
        assertThat(node.skill).isEqualTo(skill)
        assertThat(node.children).isEmpty()
        assertThat(node.parent).isEqualTo(parentNode)
    }

    @Test
    fun `Given children When create node Then return new node with children but without parent`() {
        val children = generateNodes()
        nodes.putAll(children)
        val skill = Skill(newUUID(), "new skill")
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        val node = nodeService.createNode(skill.id, childrenIds = children.keys)
        assertThat(node.id).isNotBlank()
        assertThat(node.skill).isEqualTo(skill)
        assertThat(node.children).isEqualTo(children.values)
    }

    @Test
    fun `When delete Node then remove node`() {
        val node = Node(newUUID(), skill = Skill(newUUID(), ""))
        nodes[node.id] = node
        assertThat(nodes).containsKey(node.id)
        val deletedNode = nodeService.deleteNode(node.id)
        assertThat(deletedNode).isEqualTo(node)
        assertThat(nodes).doesNotContainKey(node.id)
    }

    @Test
    fun `Given non existing id When delete node Then return null and do nothing with collection`() {
        val nodesBeforeDeletion = nodes.map { it }
        val removedNode = nodeService.deleteNode(newUUID())
        val nodesAfterDeletion = nodes.toList().map { it }
        assertThat(nodesAfterDeletion).isEqualTo(nodesBeforeDeletion)
        assertThat(removedNode).isNull()
    }

    @Test
    fun `Given identical parameters When replace node Then replace Node idempotent`() {
        val skill = Skill(newUUID(), "")
        val node = Node(newUUID(), skill = skill)
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        nodes[node.id] = node
        val replacedNode = nodeService.replaceNode(node.id, skillId = node.skill.id)
        assertThat(replacedNode).isEqualTo(node)
    }

    @Test
    fun `Given node without parent When replace node with parent Then replace Node with parent`() {
        val skill = Skill(newUUID(), "skill")
        val node = Node(newUUID(), skill = skill)
        val parentSkill = Skill(newUUID(), "parent skill")
        val parentNode = Node(newUUID(), skill = parentSkill)
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        nodes[node.id] = node
        nodes[parentNode.id] = parentNode
        val replacedNode = nodeService.replaceNode(node.id, skillId = node.skill.id, parentId = parentNode.id)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode).isNotEqualTo(node)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode?.parent).isEqualTo(parentNode)
    }

    @Test
    fun `Given node with parent When replace node with parent Then replace Node with parent`() {
        val parentSkill = Skill(newUUID(), "parent skill")
        val anotherParentSkill = Skill(newUUID(), "parent skill")
        val originalParentNode = Node(newUUID(), skill = parentSkill)
        val replacedParentNode = Node(newUUID(), skill = anotherParentSkill)
        val skill = Skill(newUUID(), "skill")
        val originalNode = Node(newUUID(), skill = skill, parent = originalParentNode)

        doReturn(skill).`when`(skillService).getSkill(skill.id)
        nodes[originalNode.id] = originalNode
        nodes[originalParentNode.id] = originalParentNode
        nodes[replacedParentNode.id] = replacedParentNode

        val replacedNode = nodeService.replaceNode(originalNode.id, skillId = originalNode.skill.id, parentId = replacedParentNode.id)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode).isNotEqualTo(originalNode)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode?.parent).isNotEqualTo(originalParentNode)
        assertThat(replacedNode?.parent).isEqualTo(replacedParentNode)
    }

    @Test
    fun `Given node without children When replace node with children Then replace Node with children`() {
        val children = generateNodes()
        val skill = Skill(newUUID(), "skill")
        val originalNode = Node(newUUID(), skill = skill)

        doReturn(skill).`when`(skillService).getSkill(skill.id)
        nodes[originalNode.id] = originalNode
        nodes.putAll(children)

        val replacedNode = nodeService.replaceNode(originalNode.id, skillId = originalNode.skill.id, childrenIds = children.keys)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode).isNotEqualTo(originalNode)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode?.children).isNotEmpty()
        assertThat(replacedNode?.children).isEqualTo(children.values)
    }

    @Test
    fun `Given node with children When replace node with children Then replace Node with children`() {
        val originalChildren = generateNodes()
        val replacedChildren = generateNodes()
        val skill = Skill(newUUID(), "skill")
        val originalNode = Node(newUUID(), skill = skill)

        doReturn(skill).`when`(skillService).getSkill(skill.id)
        nodes[originalNode.id] = originalNode
        nodes.putAll(originalChildren)
        nodes.putAll(replacedChildren)

        val replacedNode = nodeService.replaceNode(originalNode.id, skillId = originalNode.skill.id, childrenIds = replacedChildren.keys)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode).isNotEqualTo(originalNode)
        assertThat(replacedNode).isNotNull()
        assertThat(replacedNode?.children).isNotEqualTo(originalChildren)
        assertThat(replacedNode?.children).isEqualTo(replacedChildren.values)
    }

    @Test
    fun `Given non existing id When replace node Then return null and dont change collection`() {
        val replacedNode = nodeService.replaceNode(newUUID(), skillId = newUUID())
        assertThat(replacedNode).isNull()
    }

    @Test
    fun `Given non existing skill id When replaced node Then throw SkillTreeException`() {
        val skill = Skill(newUUID(), "skill")
        val node = Node(newUUID(), skill = skill)
        nodes[node.id] = node
        assertThatThrownBy { nodeService.replaceNode(node.id, skillId = skill.id) }
            .hasMessage("Not found skill with id = ${skill.id}")
    }


    private fun generateNodes() = generateSequence { newUUID() }
        .map { Skill(it, it) }
        .map { val id = newUUID(); id to Node(id, skill = it) }
        .take(1).toMap()


}