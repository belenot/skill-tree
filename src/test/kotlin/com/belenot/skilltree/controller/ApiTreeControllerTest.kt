package com.belenot.skilltree.controller

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.domain.Skill
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.assertj.core.api.Assertions.*
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
import java.lang.IllegalStateException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTreeControllerTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var apiController: ApiController

    @Test
    fun `get empty list of trees`() {
        val response = restTemplate.getForEntity<String>(
            url = "/tree?page=0&size=1")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `post new tree`() {
        val postSkill = PostSkill("skill for new tree")
        val postSkillResponse = restTemplate.postForEntity<Skill>("/skill", postSkill)
        val skillId = postSkillResponse.body?.id ?: throw IllegalStateException("Skill must be created.")
        val postNode = PostNode(skillId = skillId)
        val postNodeResponse = restTemplate.postForEntity<Node>(
            "/node", postNode)
        val nodeId = postNodeResponse.body?.id ?: throw IllegalStateException("Node must be created.")
        val postTree = PostTree(rootId = nodeId, description = "Test tree")
        val response = restTemplate.postForEntity<Tree>("/tree", postTree)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        response.body?.let { tree ->
            assertThat(tree.id).isNotBlank()
            assertThat(tree.root.id).isEqualTo(nodeId)
            assertThat(tree.description).isEqualTo(postTree.description)
        }
    }

    @Test
    fun `delete created tree`() {
        val postSkill = PostSkill("skill for new tree, that must be deleted")
        val postSkillResponse = restTemplate.postForEntity<Skill>("/skill", postSkill)
        val skillId = postSkillResponse.body?.id ?: throw IllegalStateException("Skill must be created.")
        val postNode = PostNode(skillId = skillId)
        val postNodeResponse = restTemplate.postForEntity<Node>(
            "/node", postNode)
        val nodeId = postNodeResponse.body?.id ?: throw IllegalStateException("Node must be created.")
        val postTree = PostTree(rootId = nodeId, description = "Test tree for deletion")
        val createdTree = restTemplate.postForEntity<Tree>("/tree", postTree)
        val response = restTemplate.execute(
            "/tree/${createdTree.body?.id}",
            HttpMethod.DELETE, { _ ->  }, {response -> response })
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val notFoundTree = restTemplate.getForEntity<Any>("/tree/${createdTree.body?.id}")
        assertThat(notFoundTree.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `put created tree`() {
        val anotherPostSkill = PostSkill("another skill for new tree")
        val anotherPostSkillResponse = restTemplate.postForEntity<Skill>("/skill", anotherPostSkill)
        val anotherSkillId = anotherPostSkillResponse.body?.id ?: throw IllegalStateException("Skill must be created.")
        val anotherPostNode = PostNode(skillId = anotherSkillId)
        val anotherNode = restTemplate.postForEntity<Node>("/node", anotherPostNode)
        val anotherNodeId = anotherNode.body?.id?: throw IllegalStateException("Another node must be created")
        val putTree = PutTree(rootId = anotherNodeId, description = "Tree that will replace")
        val responsePostTree = restTemplate.postForEntity<Tree>(
            "/tree",
            putTree)
        val createdTreeId = responsePostTree.body?.id?: throw IllegalStateException("Tree must be created")
        val response = restTemplate.exchange<Tree>(
            "/tree/${createdTreeId}",
            HttpMethod.PUT,
            HttpEntity(putTree)
        )
        assertThat(response.body).isNotNull()
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.id).isEqualTo(createdTreeId)
        assertThat(response.body?.root?.id).isEqualTo(anotherNodeId)
    }
}