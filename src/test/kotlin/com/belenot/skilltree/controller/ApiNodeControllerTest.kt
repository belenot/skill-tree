package com.belenot.skilltree.controller

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.utils.newUUID
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
class ApiNodeControllerTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var apiController: ApiController

    @Test
    fun `get empty list of nodes`() {
        val response = restTemplate.getForEntity<String>(
            url = "/node?page=0&size=1")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `post new node`() {
        val postSkill = PostSkill("skill for new node")
        val postSkillResponse = restTemplate.postForEntity<Skill>("/skill", postSkill)
        val skillId = postSkillResponse.body?.id ?: throw IllegalStateException("Skill must be created.")
        val postNode = PostNode(skillId = skillId)
        val response = restTemplate.postForEntity<Node>(
            "/node", postNode)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        response.body?.let { node ->
            assertThat(node.id).isNotBlank()
            assertThat(node.skill.id).isEqualTo(skillId)
//            assertThat(node.title).isEqualTo(postNode.title)
        }
    }

    @Test
    fun `delete created node`() {
        val postSkill = PostSkill("skill for new node")
        val postSkillResponse = restTemplate.postForEntity<Skill>("/skill", postSkill)
        val skillId = postSkillResponse.body?.id ?: throw IllegalStateException("Skill must be created.")
        val postNode = PostNode(skillId = skillId)
        val createdNode = restTemplate.postForEntity<Node>("/node", postNode)
        val response = restTemplate.execute(
            "/node/${createdNode.body?.id}",
            HttpMethod.DELETE, { _ ->  }, {response -> response })
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `put created node`() {
        val postSkill = PostSkill("skill for new node")
        val postSkillResponse = restTemplate.postForEntity<Skill>("/skill", postSkill)
        val anotherPostSkill = PostSkill("another skill for new node")
        val anotherPostSkillResponse = restTemplate.postForEntity<Skill>("/skill", anotherPostSkill)
        val skillId = postSkillResponse.body?.id ?: throw IllegalStateException("Skill must be created.")
        val anotherSkillId = anotherPostSkillResponse.body?.id ?: throw IllegalStateException("Skill must be created.")
        val postNode = PostNode(skillId = skillId)
        val createdNode = restTemplate.postForEntity<Node>("/node", postNode)
        val putNode: PutNode = PutNode(anotherSkillId)
        val response = restTemplate.exchange<Node>(
            "/node/${createdNode.body?.id}",
            HttpMethod.PUT,
            HttpEntity(putNode))
        assertThat(response.body).isNotNull()
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.id).isEqualTo(createdNode.body?.id)
        assertThat(response.body?.skill?.id).isEqualTo(anotherSkillId)
    }

    @Test
    fun `Given unknown nodeId When get node Then return NOT_FOUND`() {
        val id = newUUID()
        val response = restTemplate.getForEntity<String>("/node/${id}")
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Given unknown skillId When post node Then return BAD_REQUEST`() {
        val skillId = newUUID()
        val postNode = PostNode(skillId = skillId)
        val response = restTemplate.postForEntity<Any>("/node", postNode)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Given unknown nodeId When put node Then return NOT_FOUND`() {
        val nodeId = newUUID()
        val putNode = PutNode(skillId = newUUID())
        val response = restTemplate.exchange<Any>("/node/${nodeId}", HttpMethod.PUT, HttpEntity(putNode))
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Given created node and unknown skill id When replace node Then return BAD_REQUEST`() {
        val postSkillResponse = restTemplate.postForEntity<Skill>(
            "/skill", PostSkill("Skill for node"))
        val postNode = PostNode(skillId = postSkillResponse.body!!.id)
        val postNodeResponse = restTemplate.postForEntity<Node>("/node", postNode)
        val putNode = PutNode(newUUID())
        val response = restTemplate.exchange<Any>(
            "/node/${postNodeResponse.body!!.id}", HttpMethod.PUT, HttpEntity(putNode))
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}