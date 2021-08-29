package com.belenot.skilltree.service

import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.repository.SkillRepository
import org.springframework.stereotype.Service

@Service
open class SkillService(val skillRepository: SkillRepository) {


    open fun getSkill(page: Int, size: Int) = skillRepository.getSkill(page, size)

    open fun getSkill(id: String) = skillRepository.getSkill(id)

    open fun createSkill(title: String): Skill = skillRepository.createSkill(title)

    open fun deleteSkill(id: String): Skill? = skillRepository.deleteSkill(id)

    open fun replaceSkill(id: String, title: String) = skillRepository.updateSkill(id, title)
}