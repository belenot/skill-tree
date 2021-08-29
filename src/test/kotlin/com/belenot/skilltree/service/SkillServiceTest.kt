package com.belenot.skilltree.service

import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.repository.SkillRepository
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class SkillServiceTest {
    lateinit var skillService: SkillService
    lateinit var skillRepository: SkillRepository

    @BeforeEach
    fun beforeEach() {
        skillRepository = mock(SkillRepository::class.java)
        doReturn(listOf<Skill>()).`when`(skillRepository).getSkill(anyInt(), anyInt())
        skillService = SkillService(skillRepository)
    }

    @Test
    fun `When get skills Then call skillRepository getSkill`() {
        skillService.getSkill(0, 1)
        verify(skillRepository, times(1)).getSkill(0, 1)
    }

    @Test
    fun `Given new skill When create skill Then call skillRepository createSkill`() {
        doReturn(Skill(newUUID(), "title")).`when`(skillRepository).createSkill("title")
        skillService.createSkill("title")
        verify(skillRepository, times(1)).createSkill("title")
    }

    @Test
    fun `When delete skill Then call skillRepositry deleteSkill`() {
        val skillForDeletion = Skill(newUUID(), "skill for deletion")
        doReturn(Skill(skillForDeletion.id, skillForDeletion.title))
            .`when`(skillRepository)
            .deleteSkill(skillForDeletion.id)
        skillService.deleteSkill(skillForDeletion.id)
        verify(skillRepository, times(1)).deleteSkill(skillForDeletion.id)
    }

    @Test
    fun `When replace skill Then call skillRepository updateSkill`() {
        val skill = Skill(newUUID(), "updated title")
        skillService.replaceSkill(skill.id, skill.title)
        verify(skillRepository, times(1))
            .updateSkill(skill.id, skill.title)
    }

    @Test
    fun `When get skill Then call skillRepository getSkill`() {
        val skill = Skill(newUUID(), "title")
        doReturn(skill).`when`(skillRepository).getSkill(skill.id)
        skillService.getSkill(skill.id)
        verify(skillRepository, times(1)).getSkill(skill.id)
    }

    // TODO add more tests

}