package com.belenot.skilltree.repository

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.doReturn

class TreeRepositoryTest {

    lateinit var treeRepository: TreeRepository
    lateinit var trees: MutableMap<String, Tree>

    @BeforeEach
    fun beforeEach() {
        trees = mutableMapOf()
        treeRepository = TreeRepository(trees)
    }

    @Test
    fun `When get trees Then return trees collection`() {
        val actualTrees = treeRepository.getTree(0, 1)
        assertThat(actualTrees).isNotNull()
    }

    @Test
    fun `When get tree Then return tree`() {
        val tree = Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill")), "")
        trees[tree.id] = tree
        val actualTree = treeRepository.getTree(tree.id)
        assertThat(actualTree).isNotNull()
        assertThat(actualTree).isEqualTo(tree)
        assertThat(trees).containsKey(tree.id)
        assertThat(trees).containsValue(tree)
    }

    @Test
    fun `Given unknown id When get tree Then return null`() {
        val unknownId = newUUID()
        val actualTree = treeRepository.getTree(unknownId)
        assertThat(actualTree).isNull()
    }

    @Test
    fun `When create tree Then return tree and add tree to collection`() {
        val rootNode = Node(newUUID(), skill = Skill(newUUID(), "skill"))
        val treesBefore = trees.values.toSet()
        val tree = treeRepository.createTree(rootNode.id, rootNode)
        val treesAfter = trees.values.toSet()
        assertThat(tree).isNotNull()
        assertThat(tree.id).isNotBlank()
        assertThat(tree.root).isEqualTo(rootNode)
        assertThat(treesBefore).isNotEqualTo(treesAfter)
        assertThat(treesBefore).doesNotContain(tree)
        assertThat(treesAfter).contains(tree)
    }

    @Test
    fun `When remove tree Then return tree and remove from collection`() {
        val tree = Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill")), description = "tree")
        trees[tree.id] = tree
        val originalTreeCollection = trees.map { it.value }
        val deletedTree = treeRepository.removeTree(tree.id)
        val actualTreeCollection = trees.map { it.value }
        assertThat(originalTreeCollection).isNotEqualTo(actualTreeCollection)
        assertThat(actualTreeCollection).doesNotContain(tree)
        assertThat(deletedTree).isEqualTo(tree)
    }

    @Test
    fun `Given unknown tree id When remove tree Then return null and do not change collection`() {
        val unknownTreeId = newUUID()
        val originalTreeCollection = trees.map { it.value }
        val deletedTree = treeRepository.removeTree(unknownTreeId)
        val actualTreeCollection = trees.map { it.value }
        assertThat(deletedTree).isNull()
        assertThat(originalTreeCollection).isEqualTo(actualTreeCollection)
    }

    @Test
    fun `Given known tree id When update tree Then return updated tree and change in collection`() {
        val originalTree = Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill")), "original tree")
        val expectedRootNode = Node(newUUID(), skill = Skill(newUUID(), "another skill"))
        val expectedTree = Tree(originalTree.id, expectedRootNode, "expected tree")
        val treesBefore = trees.values.toSet()
        val actualTree = treeRepository.updateTree(originalTree.id, expectedTree.description, expectedRootNode)
        val treesAfter = trees.values.toSet()
        assertThat(treesAfter).isNotEqualTo(treesBefore)
        assertThat(actualTree).isEqualTo(expectedTree)
        assertThat(treesAfter).doesNotContain(originalTree)
        assertThat(treesAfter).contains(actualTree)

    }

    @Test
    fun `Given tree id and there exists tree with this id When call containsId Then return true`() {
        val treeId = newUUID()
        val tree = Tree(treeId, Node(newUUID(), skill = Skill(newUUID(), "skill")), "tree")
        trees[treeId] = tree
        val actualResult = treeRepository.containsId(treeId)

        assertThat(actualResult).isTrue()
    }

    @Test
    fun `Given skill id and there doesnt exist skill with this id When call containsId Then return false`() {
        val skillId = newUUID()

        val actualResult = treeRepository.containsId(skillId)

        assertThat(actualResult).isFalse()
    }



}