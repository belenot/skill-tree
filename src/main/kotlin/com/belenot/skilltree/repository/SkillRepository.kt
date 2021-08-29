package com.belenot.skilltree.repository

import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.utils.paged
import org.springframework.stereotype.Repository

@Repository
open class SkillRepository(
    private val skills: MutableMap<String, Skill>) {


    open fun getSkill(page: Int, size: Int): List<Skill> = paged(skills.values, page, size)
    open fun containsId(id: String): Boolean = skills.containsKey(id)
    open fun getSkill(id: String) = skills[id]
    open fun createSkill(title: String): Skill {
        val id = newUUID()
        val skill = Skill(id = id, title = title)
        skills.put(id, skill)
        return skill
    }

    open fun deleteSkill(id: String): Skill? = skills.remove(id)
    open fun updateSkill(id: String, title: String): Skill? = skills.replace(id, Skill(id = id, title = title))
}