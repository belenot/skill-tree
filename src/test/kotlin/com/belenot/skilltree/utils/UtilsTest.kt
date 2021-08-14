package com.belenot.skilltree.utils

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.domain.Tree
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UtilsTest {

    @Test
    fun `Given empty collection When get elements Then return empty collection`() {
        val collection = emptyList<String>()
        val actualCollection = paged(collection, 0, 1)
        assertThat(actualCollection).isEmpty()
    }

    @Test
    fun `Given non empty collection When get elements Then return paged collection`() {
        val cachedTrees = generateSequence {
            Tree(newUUID(), Node(newUUID(), skill = Skill(newUUID(), "skill=${newUUID()}")), "")
        }.take(20).toList()
        val actualTrees = paged(cachedTrees, 0, 10)
        assertThat(actualTrees).isEqualTo(cachedTrees.take(10))

    }

    @Test
    fun `Given negative or zero page or size When get elements Then throw SkillTreeException`() {
        val collection = emptyList<Any>()
        Assertions.assertThatThrownBy { paged(collection, -1, 1) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(PAGING_VALIDATION_VIOLATION)
        Assertions.assertThatThrownBy { paged(collection, 1, -1) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(PAGING_VALIDATION_VIOLATION)
        Assertions.assertThatThrownBy { paged(collection, -1, -1) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(PAGING_VALIDATION_VIOLATION)
        Assertions.assertThatThrownBy { paged(collection, 0, 0) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(PAGING_VALIDATION_VIOLATION)
    }
}