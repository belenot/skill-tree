package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.generateNodes
import com.belenot.skilltree.repository.NodeRepository
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class NodeServiceTest {

    lateinit var nodeService: NodeService
    lateinit var skillService: SkillService

    lateinit var nodeRepository: NodeRepository

    @BeforeEach
    fun beforeEach() {
        skillService = mock(SkillService::class.java)
        nodeRepository = mock(NodeRepository::class.java)
        nodeService = NodeService(nodeRepository, skillService)
    }

    @Test
    fun `Given unknown skillId When createNode Then throw SkillTreeException`() {
        val skillId = newUUID()
        `when`(skillService.getSkill(skillId)).thenReturn(null)
        assertThatThrownBy { nodeService.createNode(skillId) }.isExactlyInstanceOf(SkillTreeException::class.java)
    }

    @Test
    fun `When create node Then call nodeRepository createNode`() {
        val skill = Skill(newUUID(), "skill")
        val node = Node(newUUID(), skill = skill)
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        doReturn(node).`when`(nodeRepository).createNode(emptySet(), skill, null)
        val createdNode = nodeService.createNode(skill.id)
        assertThat(createdNode.id).isNotBlank()
        assertThat(createdNode.skill.id).isEqualTo(skill.id)
        assertThat(createdNode.skill.title).isEqualTo(skill.title)
        verify(nodeRepository, times(1)).createNode(emptySet(), skill, null)
    }

    @Test
    fun `When get nodes Then call nodeRepository getNodwes`() {
        nodeService.getNode(0, 1)
        verify(nodeRepository, times(1)).getNode(0, 1)
    }

    @Test
    fun `When delete Node then call nodeRepository removeNode`() {
        val node = Node(newUUID(), skill = Skill(newUUID(), ""))
        nodeService.deleteNode(node.id)
        verify(nodeRepository, times(1)).removeNode(node.id)
    }

    @Test
    fun `When replace node Then call nodeRepository updateNode`() {
        val skill = Skill(newUUID(), "")
        val node = Node(newUUID(), skill = skill)
        doReturn(skill).`when`(skillService).getSkill(skill.id)
        doReturn(true).`when`(nodeRepository).exists(node.id)
        doReturn(node).`when`(nodeRepository).updateNode(node.id, skill = skill)
        nodeService.replaceNode(node.id, skillId = node.skill.id)
        verify(skillService, times(1)).getSkill(skill.id)
        verify(nodeRepository, times(1)).exists(node.id)
        verify(nodeRepository, times(1)).updateNode(node.id, skill = skill)
    }

    @Test
    fun `Given non existing id When replace node Then return null and dont call nodeRepository updateNode`() {
        val nonExistingId = newUUID()
        val skill = Skill(newUUID(), "title")
        val replacedNode = nodeService.replaceNode(nonExistingId, skillId = skill.id)
        assertThat(replacedNode).isNull()
        verify(nodeRepository, times(1)).exists(nonExistingId)
        verify(nodeRepository, times(0)).updateNode(nonExistingId, skill = skill)
    }

    @Test
    fun `Given non existing skill id When replaced node Then throw SkillTreeException`() {
        val skill = Skill(newUUID(), "skill")
        val node = Node(newUUID(), skill = skill)
        doReturn(node).`when`(nodeRepository).getNode(node.id)
        doReturn(true).`when`(nodeRepository).exists(node.id)
        doReturn(null).`when`(skillService).getSkill(skill.id)
        assertThatThrownBy { nodeService.replaceNode(node.id, skillId = skill.id) }
            .hasMessage("Not found skill with id = ${skill.id}")
    }


}