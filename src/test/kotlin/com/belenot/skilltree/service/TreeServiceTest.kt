package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.repository.TreeRepository
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class TreeServiceTest {

    lateinit var treeService: TreeService
    lateinit var skillService: SkillService
    lateinit var nodeService: NodeService
    lateinit var treeRepository: TreeRepository

    @BeforeEach
    fun beforeEach() {
        skillService = mock(SkillService::class.java)
        nodeService = mock(NodeService::class.java)
        treeRepository = mock(TreeRepository::class.java)
        treeService = TreeService(treeRepository, nodeService, skillService)
    }

    @Test
    fun `When get trees Then call treeRepository getTrees`() {
        treeService.getTree(0, 1)
        verify(treeRepository, times(1)).getTree(0, 1)
    }


    @Test
    fun `When get tree Then call treeRepository getTree`() {
        val tree = Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill")), "")
        treeService.getTree(tree.id)
        verify(treeRepository, times(1)).getTree(tree.id)
    }

    @Test
    fun `Given unknown id When get tree Then return null`() {
        val actualTree = treeService.getTree(newUUID())
        assertThat(actualTree).isNull()
    }

    @Test
    fun `When create tree Then return tree and call treeRepository createTree`() {
        val rootNode = Node(newUUID(), skill = Skill(newUUID(), "skill"))
        val tree = Tree(newUUID(), rootNode, "tree description")
        doReturn(rootNode).`when`(nodeService).getNode(rootNode.id)
        doReturn(tree).`when`(treeRepository).createTree(tree.description, rootNode)
        val actualTree = treeService.createTree(rootNode.id, tree.description)
        assertThat(actualTree).isNotNull()
        assertThat(actualTree).isEqualTo(tree)
        verify(treeRepository, times(1)).createTree("tree description", rootNode)
    }

    @Test
    fun `Given unknown root node id When create tree Then throw SkillTreeException`() {
        val unknownNodeId = newUUID()
        doReturn(null).`when`(nodeService).getNode(unknownNodeId)
        assertThatThrownBy { treeService.createTree(unknownNodeId, "") }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage("Not found root node with id = ${unknownNodeId}.")
    }

    @Test
    fun `When delete tree Then return deleted tree and call treeRepository removeTree`() {
        val tree = Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill")), description = "tree")
        doReturn(tree).`when`(treeRepository).removeTree(tree.id)
        val deletedTree = treeService.deleteTree(tree.id)
        assertThat(deletedTree).isEqualTo(tree)
        verify(treeRepository, times(1)).removeTree(tree.id)
    }

    @Test
    fun `Given known tree id and unknown root node id When replace tree Then throw SkillTreeException and do not call treeRepository updateTree`() {
        val originalTree = Tree(newUUID(), root = Node(newUUID(), skill = Skill(newUUID(), "")), "original tree")
        val unknownNode = Node(newUUID(), skill = Skill(newUUID(), "skill"))
        doReturn(true).`when`(treeRepository).exists(originalTree.id)
        doReturn(null).`when`(nodeService).getNode(originalTree.root.id)
        assertThatThrownBy { treeService.replaceTree(originalTree.id, unknownNode.id, "replaced tree") }
        verify(treeRepository, times(0)).updateTree(originalTree.id, "replaced tree", unknownNode)
    }

    @Test
    fun `Given known tree id and known root node id When replace tree Then return old tree and call treeRepository updateTree`() {
        val originalNode = Node(newUUID(), skill = Skill(newUUID(), "original skill"))
        val originalTree = Tree(newUUID(), root = Node(newUUID(), skill = Skill(newUUID(), "")), "original tree")
        val replacedNode = Node(newUUID(), skill = Skill(newUUID(), "replaced skill"))
        val expectedTree = Tree(originalTree.id, replacedNode, "replaced tree")
        doReturn(originalNode).`when`(nodeService).getNode(originalNode.id)
        doReturn(replacedNode).`when`(nodeService).getNode(replacedNode.id)
        doReturn(true).`when`(treeRepository).exists(originalTree.id)
        doReturn(expectedTree).`when`(treeRepository).updateTree(originalTree.id, expectedTree.description, expectedTree.root)

        val replacedTree = treeService.replaceTree(originalTree.id, replacedNode.id, "replaced tree")
        assertThat(replacedTree).isNotNull()
        assertThat(replacedTree).isNotEqualTo(originalTree)
        assertThat(replacedTree).isEqualTo(expectedTree)
        assertThat(replacedTree?.description).isEqualTo("replaced tree")
        verify(treeRepository, times(1)).updateTree(originalTree.id, "replaced tree", replacedNode)

    }


}