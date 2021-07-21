package com.belenot.skilltree.controller

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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiControllerTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `get empty list of skills`() {
        val response = restTemplate.getForEntity<String>(
            url = "/skill", uriVariables = mapOf("page" to 0, "size" to 0))
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `post new skill`() {
        val postSkill = PostSkill("New test skill")
        val response = restTemplate.postForEntity<Skill>(
            "/skill", postSkill)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        response.body?.let { skill ->
            assertThat(skill.id).isNotBlank()
            assertThat(skill.title).isEqualTo(postSkill.title)
        }
    }

    @Test
    fun `delete created skill`() {
        val postSkill = PostSkill("Skill for deletion")
        val createdSkill = restTemplate.postForEntity<Skill>("/skill", postSkill)
        val response = restTemplate.execute(
            "/skill/${createdSkill.body?.id}",
            HttpMethod.DELETE, { _ ->  }, {response -> response })
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `put created skill`() {
        val postSkill = PostSkill("Skill for update")
        val createdSkill = restTemplate.postForEntity<Skill>("/skill", postSkill)
        val putSkill: PutSkill = PutSkill("Updated skill")
        val response = restTemplate.exchange<Skill>(
            "/skill/${createdSkill.body?.id}",
            HttpMethod.PUT,
            HttpEntity(putSkill))
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.id).isEqualTo(createdSkill.body?.id)
        assertThat(response.body?.title).isEqualTo(postSkill.title)
        val updatedSkill = restTemplate.getForEntity<Skill>("/skill/${createdSkill.body?.id}")
        assertThat(updatedSkill.body?.title).isEqualTo(putSkill.title)
    }
}