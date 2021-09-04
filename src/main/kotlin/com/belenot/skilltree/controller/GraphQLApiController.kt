package com.belenot.skilltree.controller

import com.belenot.skilltree.graphql.query.NodeApi
import com.belenot.skilltree.graphql.query.SkillApi
import com.belenot.skilltree.graphql.query.TreeApi
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.idl.SchemaPrinter
import io.leangen.graphql.GraphQLSchemaGenerator
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class GraphQLApiController(skillApi: SkillApi, nodeApi: NodeApi, treeApi: TreeApi) {
    private val logger = LoggerFactory.getLogger(GraphQLApiController::class.java)

    var graphQL: GraphQL = GraphQL.newGraphQL(GraphQLSchemaGenerator()
        .withBasePackages("com.belenot.skilltree.graphql")
        .withOperationsFromSingletons(skillApi, nodeApi, treeApi)
        .generate().also { logger.info(SchemaPrinter().print(it)) }).build()

    @PostMapping("/graphql")
    fun graphQl(@RequestBody request: Map<String, String>, raw: HttpServletRequest) =
        graphQL.execute(ExecutionInput.newExecutionInput()
            .query(request.get("query"))
            .operationName(request.get("operationName"))
            .context(raw)
            .build()).toSpecification()
}