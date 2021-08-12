package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

class NodeServiceTest {

    lateinit var nodeService: NodeService
    lateinit var skillService: SkillService

    @BeforeEach
    fun beforeEach() {
        skillService = mock(SkillService::class.java)
        nodeService = NodeService(skillService)
    }

    @Test
    fun `Given unknown skillId When createNode Then throw SkillTreeException`() {
        val skillId = newUUID()
        `when`(skillService.getSkill(skillId)).thenReturn(null)
        assertThatThrownBy { nodeService.createNode(skillId) }.isExactlyInstanceOf(SkillTreeException::class.java)
    }
}