package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.utils.newUUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*

class SkillServiceTest {
    lateinit var skillService: SkillService

    @BeforeEach
    fun beforeEach() {
        skillService = SkillService()
    }

    @Test
    fun `Given initialized When get skills Then return empty list`() {
        val skills = skillService.getSkill(0, 1)
        assertThat(skills).isEmpty()
    }

    @Test
    fun `Given negative or zero page or size When get skills Then throw Exception`() {
        assertThatThrownBy { skillService.getSkill(-1, 1) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(SkillService.GET_SKILL_VALIDATION_VIOLATION)
        assertThatThrownBy { skillService.getSkill(1, -1) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(SkillService.GET_SKILL_VALIDATION_VIOLATION)
        assertThatThrownBy { skillService.getSkill(-1, -1) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(SkillService.GET_SKILL_VALIDATION_VIOLATION)
        assertThatThrownBy { skillService.getSkill(1, 0) }
            .isExactlyInstanceOf(SkillTreeException::class.java)
            .hasMessage(SkillService.GET_SKILL_VALIDATION_VIOLATION)
    }

    @Test
    fun `Given new skill When create skill Then save skill`() {
        val createdSkill = skillService.createSkill("title")
        assertThat(createdSkill.id).isNotBlank()
        assertThat(createdSkill.title).isEqualTo("title")
    }

    @Test
    fun `When delete skill Then delete skill`() {
        val skillForDeletion = skillService.createSkill("skill for deletion")
        val deletedSkill = skillService.deleteSkill(skillForDeletion.id)
        assertThat(deletedSkill).isNotNull()
        assertThat(deletedSkill?.id).isEqualTo(skillForDeletion.id)
        assertThat(deletedSkill?.title).isEqualTo(skillForDeletion.title)
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
    }

    @Test
    fun `Given non existing id When update skill Then return null`() {
        val updatedSkill = skillService.replaceSkill(newUUID(), "title")
        assertThat(updatedSkill).isNull()
    }

    @Test
    fun `Given non existing id When get skill Then return null`() {
        val skill = skillService.getSkill(newUUID())
        assertThat(skill).isNull()
    }

    @Test
    fun `When get skill Then return Skill`() {
        val skill = skillService.createSkill("new skill")
        val actualSkill = skillService.getSkill(skill.id)
        assertThat(actualSkill).isEqualTo(skill)
    }

    // TODO add more tests

}