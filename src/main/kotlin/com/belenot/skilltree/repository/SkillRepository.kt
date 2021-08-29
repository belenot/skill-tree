package com.belenot.skilltree.repository

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.utils.paged
import org.springframework.stereotype.Repository

@Repository
class SkillRepository {

    private val skills  = mutableMapOf<String, Skill>()

    fun getSkill(page: Int, size: Int): List<Skill> = paged(skills.values, page, size)
    fun containsId(id: String): Boolean = skills.containsKey(id)
    fun getSkill(id: String) = skills[id]
    fun createSkill(title: String): Skill {
        val id = newUUID()
        val skill = Skill(id = id, title = title)
        skills.put(id, skill)
        return skill
    }

    fun deleteSkill(id: String): Skill? = skills.remove(id)
    fun updateSkill(id: String, title: String): Skill? = skills.replace(id, Skill(id = id, title = title))
}