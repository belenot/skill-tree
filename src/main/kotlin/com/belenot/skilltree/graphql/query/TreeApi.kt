package com.belenot.skilltree.graphql.query

import com.belenot.skilltree.service.TreeService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.stereotype.Component

@Component
class TreeApi(val treeService: TreeService) {

    @GraphQLQuery(name = "tree")
    fun getTree(@GraphQLArgument(name = "id") id: String) =
        treeService.getTree(id)

    @GraphQLQuery(name = "trees")
    fun getTrees(@GraphQLArgument(name = "page") page: Int, @GraphQLArgument(name = "size") size: Int) =
        treeService.getTree(page, size)


    @GraphQLMutation(name = "addTree")
    fun addTree(@GraphQLArgument(name = "tree") addTreeDto: AddTreeDto) =
        treeService.createTree(addTreeDto.rootId, addTreeDto.description)

    @GraphQLMutation(name = "updateTree")
    fun updateTree(@GraphQLArgument(name = "tree") updateTreeDto: UpdateTreeDto) =
        treeService.replaceTree(
            updateTreeDto.id,
            updateTreeDto.rootId,
            updateTreeDto.description
        )

    @GraphQLMutation(name = "deleteTree")
    fun deleteTree(@GraphQLArgument(name = "treeId") treeId: String) =
        treeService.deleteTree(treeId)
}

data class AddTreeDto(
    val rootId: String,
    val description: String
)

data class UpdateTreeDto(
    val id: String,
    val rootId: String,
    val description: String
)