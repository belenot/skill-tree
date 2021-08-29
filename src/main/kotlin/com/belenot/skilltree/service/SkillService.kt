package com.belenot.skilltree.service

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.utils.newUUID
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.repository.SkillRepository
import com.belenot.skilltree.utils.paged
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException


@Service
open class SkillService(val skillRepository: SkillRepository) {


    open fun getSkill(page: Int, size: Int) = skillRepository.getSkill(page, size)

    open fun getSkill(id: String) =
        if (skillRepository.containsId(id)) skillRepository.getSkill(id)
        else null

    open fun createSkill(title: String): Skill = skillRepository.createSkill(title)

    open fun deleteSkill(id: String): Skill? = skillRepository.deleteSkill(id)

    open fun replaceSkill(id: String, title: String) = skillRepository.updateSkill(id, title)
}