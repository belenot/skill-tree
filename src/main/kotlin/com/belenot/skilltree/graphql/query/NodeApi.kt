package com.belenot.skilltree.graphql.query

import com.belenot.skilltree.service.NodeService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.stereotype.Component

@Component
class NodeApi(val nodeService: NodeService) {

    @GraphQLQuery(name = "node")
    fun getNode(@GraphQLArgument(name = "id") id: String) =
        nodeService.getNode(id)

    @GraphQLQuery(name = "nodes")
    fun getNodes(@GraphQLArgument(name = "page") page: Int, @GraphQLArgument(name = "size") size: Int) =
        nodeService.getNode(page, size)

    @GraphQLMutation(name = "addNode")
    fun addNode(@GraphQLArgument(name = "node") addNodeDto: AddNodeDto) =
        nodeService.createNode(addNodeDto.skillId, addNodeDto.childrenIds, addNodeDto.parentId)

    @GraphQLMutation(name = "updateNode")
    fun updateNode(@GraphQLArgument(name = "node") updateNodeDto: UpdateNodeDto) =
        nodeService.replaceNode(
            updateNodeDto.id,
            updateNodeDto.skillId,
            updateNodeDto.childrenIds,
            updateNodeDto.parentId
        )

    @GraphQLMutation(name = "deleteNode")
    fun deleteNode(@GraphQLArgument(name = "nodeId") nodeId: String) =
        nodeService.deleteNode(nodeId)
}

data class AddNodeDto(
    val skillId: String,
    val childrenIds: Set<String> = emptySet(),
    val parentId: String? = null
)

data class UpdateNodeDto(
    val id: String,
    val skillId: String,
    val childrenIds: Set<String> = emptySet(),
    val parentId: String? = null
)
