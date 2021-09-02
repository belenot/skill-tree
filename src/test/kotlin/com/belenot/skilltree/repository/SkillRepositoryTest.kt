package com.belenot.skilltree.repository

import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SkillRepositoryTest {
    lateinit var skills: MutableMap<String, Skill>
    lateinit var skillRepository: SkillRepository

    @BeforeEach
    fun beforeEach() {
        skills = mutableMapOf()
        skillRepository = SkillRepository(skills)
    }

    @Test
    fun `When get skills Then return skills collection`() {
        val actualSkills = skillRepository.getSkill(0, 1)
        assertThat(actualSkills).isNotNull()
    }

    @Test
    fun `Given new skill When create skill Then save skill`() {
        val createdSkill = skillRepository.createSkill("title")
        assertThat(createdSkill.id).isNotBlank()
        assertThat(createdSkill.title).isEqualTo("title")
        assertThat(skills[createdSkill.id]).isEqualTo(createdSkill)
    }

    @Test
    fun `When delete skill Then remove skill from collection`() {
        val skillForDeletion = Skill(newUUID(), "skill for deletion")
        skills[skillForDeletion.id] = skillForDeletion
        val skillsBefore = skills.map { it }
        val deletedSkill = skillRepository.deleteSkill(skillForDeletion.id)

        assertThat(deletedSkill).isNotNull()
        assertThat(deletedSkill?.id).isEqualTo(skillForDeletion.id)
        assertThat(deletedSkill?.title).isEqualTo(skillForDeletion.title)
        assertThat(skillsBefore).isNotEqualTo(skills)
        assertThat(skills).doesNotContainKey(skillForDeletion.id)
    }

    @Test
    fun `When update skill Then update skill`() {
        val skillForUpdate = Skill(newUUID(), "skill for update")
        skills[skillForUpdate.id] = skillForUpdate
        val previousSkill = skillRepository.updateSkill(skillForUpdate.id, "updated title")
        assertThat(previousSkill).isNotNull()
        assertThat(previousSkill!!).isEqualTo(skillForUpdate)
        assertThat(skills).containsKey(previousSkill.id)
        val updatedSkill = skills[previousSkill.id]
        assertThat(updatedSkill).isNotNull()
        assertThat(updatedSkill?.id).isEqualTo(previousSkill.id)
        assertThat(updatedSkill?.title).isEqualTo("updated title")
    }

    @Test
    fun `Given non existing id When update skill Then return null and do not update collection`() {
        val skillsBefore = skills.map { it.key to it.value }.toMap()
        val updatedSkill = skillRepository.updateSkill(newUUID(), "title")
        assertThat(updatedSkill).isNull()
        assertThat(skillsBefore).isEqualTo(skills)
    }

    @Test
    fun `Given non existing id When get skill Then return null`() {
        val skill = skillRepository.getSkill(newUUID())
        assertThat(skill).isNull()
    }

    @Test
    fun `When get skill Then return Skill`() {
        val skill = Skill(newUUID(), "new skill")
        skills[skill.id] = skill
        val actualSkill = skillRepository.getSkill(skill.id)
        assertThat(actualSkill).isEqualTo(skill)
    }

    @Test
    fun `Given skill id and there exists skill with this id When call containsId Then return true`() {
        val skillId = newUUID()
        val skill = Skill(skillId, "skill")
        skills[skillId] = skill

        val actualResult = skillRepository.containsId(skillId)

        assertThat(actualResult).isTrue()
    }

    @Test
    fun `Given skill id and there doesnt exist skill with this id When call containsId Then return false`() {
        val skillId = newUUID()

        val actualResult = skillRepository.containsId(skillId)

        assertThat(actualResult).isFalse()
    }

}