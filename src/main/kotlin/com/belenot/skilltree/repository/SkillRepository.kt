package com.belenot.skilltree.repository

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.dao.SkillDao
import com.belenot.skilltree.dao.SkillDto
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.utils.paged
import org.springframework.stereotype.Repository

@Repository
open class SkillRepository(private val skillDao: SkillDao) {

    open fun getSkill(page: Int, size: Int): List<Skill> = paged(skillDao, page, size).map { it.toSkill() }
    open fun containsId(id: String): Boolean = skillDao.get(id) != null
    open fun getSkill(id: String): Skill? = skillDao.get(id)?.toSkill()
    open fun createSkill(title: String): Skill {
        val skillDto = SkillDto(id = null, title = title)
        val id = skillDao.save(skillDto)
        val skill = skillDao.get(id)?.toSkill()
        if (skill == null) throw SkillTreeException("Created skill not found")
        return skill
    }
    open fun deleteSkill(id: String): Skill? {
        val skill = skillDao.get(id)?.toSkill()
        skillDao.delete(id)
        return skill
    }
    open fun updateSkill(id: String, title: String): Skill? {
        val previousSkillDto = skillDao.get(id)
        val skillDto = SkillDto(id, title)
        skillDao.update(skillDto)
        return previousSkillDto?.toSkill()
    }
}

fun SkillDto.toSkill(): Skill {
    val id = id;
    if (id == null) throw SkillTreeException("id should not be null")
    return Skill(id, title?:"")
}