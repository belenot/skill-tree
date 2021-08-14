package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.paged
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException


@Service
open class SkillService {
    // TODO move to repository
    private val skills  = mutableMapOf<String, Skill>()

    open fun getSkill(page: Int, size: Int) = paged(skills.values, page, size)

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