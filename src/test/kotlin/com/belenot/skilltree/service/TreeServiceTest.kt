package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.repository.TreeRepository
import com.belenot.skilltree.utils.PAGING_VALIDATION_VIOLATION
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock

class TreeServiceTest {

    lateinit var treeService: TreeService
    lateinit var trees: MutableMap<String, Tree>
    lateinit var skillService: SkillService
    lateinit var nodeService: NodeService
    lateinit var treeRepository: TreeRepository

    @BeforeEach
    fun beforeEach() {
        trees = mutableMapOf()
        skillService = mock(SkillService::class.java)
        nodeService = mock(NodeService::class.java)
        treeRepository = TreeRepository(trees)
        treeService = TreeService(treeRepository, nodeService, skillService)
    }

    @Test
    fun `When get nodes Then return nodes collection`() {
        val actualNodes = nodeService.getNode(0, 1)
        assertThat(actualNodes).isNotNull()
    }


    @Test
    fun `When get tree Then return tree`() {
        val tree = Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill")), "")
        trees[tree.id] = tree
        val actualTree = treeService.getTree(tree.id)
        assertThat(actualTree).isEqualTo(tree)
    }

    @Test
    fun `Given unknown id When get tree Then return null`() {
        val actualTree = treeService.getTree(newUUID())
        assertThat(actualTree).isNull()
    }

    @Test
    fun `When create tree Then create tree`() {
        val rootNode = Node(newUUID(), skill = Skill(newUUID(), "skill"))
        doReturn(rootNode).`when`(nodeService).getNode(rootNode.id)

        val tree = treeService.createTree(rootNode.id, "tree description")
        assertThat(tree).isNotNull()
        assertThat(tree.id).isNotBlank()
        assertThat(tree.root).isEqualTo(rootNode)
    }

    @Test
    fun `Given unknown root node id When create tree Then throw SkillTreeException`() {
        val rootId = newUUID()
        assertThatThrownBy { treeService.createTree(rootId, "") }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage("Not found root node with id = ${rootId}.")
    }

    @Test
    fun `When delete tree Then delete tree from collection`() {
        val tree = Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill")), description = "tree")
        trees[tree.id] = tree
        val originalTreeCollection = trees.map { it.value }
        val deletedTree = treeService.deleteTree(tree.id)
        val actualTreeCollection = trees.map { it.value }
        assertThat(originalTreeCollection).isNotEqualTo(actualTreeCollection)
        assertThat(actualTreeCollection).doesNotContain(tree)
        assertThat(deletedTree).isEqualTo(tree)
    }

    @Test
    fun `Given unknown tree id When delete tree Then return null and do not change collection`() {
        val originalTreeCollection = trees.map { it.value }
        val deletedTree = treeService.deleteTree(newUUID())
        val actualTreeCollection = trees.map { it.value }

        assertThat(deletedTree).isNull()
        assertThat(originalTreeCollection).isEqualTo(actualTreeCollection)
    }

    @Test
    fun `Given unknown tree id When replace tree Then return null and dont change collection`() {
        val originalTreeCollection = trees.map { it.value }
        val replacedTree = treeService.replaceTree(newUUID(), newUUID(), "")
        val actualTreeCollection = trees.map { it.value }

        assertThat(replacedTree).isNull()
        assertThat(originalTreeCollection).isEqualTo(actualTreeCollection)
    }

    @Test
    fun `Given known tree id and unknown root node id When replace tree Then throw SkillTreeException and do not change collection`() {
        val originalTree = Tree(newUUID(), root = Node(newUUID(), skill = Skill(newUUID(), "")), "original tree")
        trees[originalTree.id] = originalTree
        val originalTreeCollection = trees.map { it.value }
        assertThatThrownBy { treeService.replaceTree(originalTree.id, newUUID(), "replaced tree") }
        assertThat(originalTreeCollection.toSet()).isEqualTo(trees.map { it.value }.toSet())
    }

    @Test
    fun `Given known tree id and known root node id When replace tree Then return old tree and replace tree in collection`() {
        val originalNode = Node(newUUID(), skill = Skill(newUUID(), "original skill"))
        val originalTree = Tree(newUUID(), root = Node(newUUID(), skill = Skill(newUUID(), "")), "original tree")
        val originalTreeCollection = trees.map { it.value }
        val replacedNode = Node(newUUID(), skill = Skill(newUUID(), "replaced skill"))

        doReturn(originalNode).`when`(nodeService).getNode(originalNode.id)
        doReturn(replacedNode).`when`(nodeService).getNode(replacedNode.id)
        trees[originalTree.id] = originalTree

        val replacedTree = treeService.replaceTree(originalTree.id, replacedNode.id, "replaced tree")
        assertThat(replacedTree).isNotNull()
        assertThat(replacedTree).isNotEqualTo(originalTree)
        assertThat(replacedTree?.root).isEqualTo(replacedNode)
        assertThat(replacedTree?.description).isEqualTo("replaced tree")
        assertThat(originalTreeCollection.toSet()).isNotEqualTo(trees.values.toSet())
        assertThat(trees[originalTree.id]?.description).isEqualTo("replaced tree")

    }


}