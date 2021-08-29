package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.repository.SkillRepository
import com.belenot.skilltree.utils.PAGING_VALIDATION_VIOLATION
import com.belenot.skilltree.utils.newUUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class SkillServiceTest {
    lateinit var skillService: SkillService
    lateinit var skillRepository: SkillRepository

    @BeforeEach
    fun beforeEach() {
        // skillRepository = SkillRepository(mutableMapOf())
        skillRepository = spy(SkillRepository(mutableMapOf()))
        skillService = SkillService(skillRepository)
    }

    @Test
    fun `When get skills Then return skills collection`() {
        val actualSkills = skillService.getSkill(0, 1)
        verify(skillRepository, times(1)).getSkill(0, 1)
        assertThat(actualSkills).isNotNull()
    }

    @Test
    fun `Given new skill When create skill Then save skill`() {
        val createdSkill = skillService.createSkill("title")
        assertThat(createdSkill.id).isNotBlank()
        assertThat(createdSkill.title).isEqualTo("title")
        verify(skillRepository, times(1)).createSkill("title")
    }

    @Test
    fun `When delete skill Then delete skill`() {
        val skillForDeletion = skillService.createSkill("skill for deletion")
        val deletedSkill = skillService.deleteSkill(skillForDeletion.id)
        assertThat(deletedSkill).isNotNull()
        assertThat(deletedSkill?.id).isEqualTo(skillForDeletion.id)
        assertThat(deletedSkill?.title).isEqualTo(skillForDeletion.title)
        verify(skillRepository, times(1)).deleteSkill(skillForDeletion.id)
    }

    @Test
    fun `When update skill Then update skill`() {
        val skillForUpdate = skillService.createSkill("skill for update")
        val previousSkill = skillService.replaceSkill(skillForUpdate.id, "updated title")
        assertThat(previousSkill).isNotNull()
        assertThat(previousSkill!!).isEqualTo(skillForUpdate)
        val updatedSkill = skillService.getSkill(previousSkill.id)
        assertThat(updatedSkill).isNotNull()
        assertThat(updatedSkill?.id).isEqualTo(previousSkill.id)
        verify(skillRepository, times(1)).updateSkill(skillForUpdate.id, "updated title")
    }

    @Test
    fun `Given non existing id When update skill Then return null`() {
        val id = newUUID()
        val updatedSkill = skillService.replaceSkill(id, "title")
        assertThat(updatedSkill).isNull()
        verify(skillRepository, times(1)).updateSkill(id, "title")
    }

    @Test
    fun `Given non existing id When get skill Then return null`() {
        val id = newUUID()
        val skill = skillService.getSkill(id)
        assertThat(skill).isNull()
        verify(skillRepository, times(1)).getSkill(id)
    }

    @Test
    fun `When get skill Then return Skill`() {
        val skill = skillService.createSkill("new skill")
        val actualSkill = skillService.getSkill(skill.id)
        assertThat(actualSkill).isEqualTo(skill)
        verify(skillRepository).getSkill(skill.id)
    }

    // TODO add more tests

}