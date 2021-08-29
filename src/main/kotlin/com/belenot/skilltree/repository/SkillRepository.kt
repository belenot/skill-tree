package com.belenot.skilltree.repository

import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.paged
import org.springframework.stereotype.Repository

@Repository
class SkillRepository {

    private val skills  = mutableMapOf<String, Skill>()

    fun getSkill(page: Int, size: Int): List<Skill> = paged(skills.values, page, size)
    fun containsId(id: String): Boolean = skills.containsKey(id)
    fun getSkill(id: String): List<Any> =


}