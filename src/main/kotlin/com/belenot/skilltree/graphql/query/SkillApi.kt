package com.belenot.skilltree.graphql.query

import com.belenot.skilltree.service.SkillService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.stereotype.Component

@Component
class SkillApi(val skillService: SkillService) {

    @GraphQLQuery(name = "skill")
    fun getSkill(@GraphQLArgument(name = "id") id: String) =
        skillService.getSkill(id)

    @GraphQLQuery(name = "skills")
    fun getSkills(@GraphQLArgument(name = "page") page: Int, @GraphQLArgument(name = "size") size: Int) =
        skillService.getSkill(page, size)

    @GraphQLMutation(name = "addSkill")
    fun addSkill(@GraphQLArgument(name = "skill") addSkillDto: AddSkillDto) =
        skillService.createSkill(addSkillDto.title)

    @GraphQLMutation(name = "updateSkill")
    fun updateSkill(@GraphQLArgument(name = "skill") updateSkillDto: UpdateSkillDto) =
        skillService.replaceSkill(updateSkillDto.id, updateSkillDto.title)

    @GraphQLMutation(name = "deleteSkill")
    fun deleteSkill(@GraphQLArgument(name = "skillId") skillId: String) =
        skillService.deleteSkill(skillId)
}

data class AddSkillDto(val title: String)
data class UpdateSkillDto(val id: String, val title: String)