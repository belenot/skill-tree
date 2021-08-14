package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.domain.Skill
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException


@Service
open class SkillService {
    companion object {
        @JvmStatic
        val GET_SKILL_VALIDATION_VIOLATION = "Page must be greater then or equal zero and size must be greater then zero."
    }
    // TODO move to repository
    private val skills  = mutableMapOf<String, Skill>()

    open fun getSkill(page: Int, size: Int) = if (page < 0 || size <= 0)
        throw SkillTreeException(GET_SKILL_VALIDATION_VIOLATION)
        else skills.values.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()

    open fun getSkill(id: String) =
        if (skills.containsKey(id)) skills[id]
        else null

    open fun createSkill(title: String): Skill {
        val id = newUUID()
        val skill = Skill(id = id, title = title)
        skills.put(id, skill)
        return skill
    }

    open fun deleteSkill(id: String) = skills.remove(id)

    open fun replaceSkill(id: String, title: String) =
        if (skills.containsKey(id)) skills.replace(id, Skill(id = id, title = title))
        else null
}