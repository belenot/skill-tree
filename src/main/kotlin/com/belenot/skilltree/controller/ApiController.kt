package com.belenot.skilltree.controller

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.domain.Tree
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ApiController {
    private val skills  = mutableMapOf<String, Skill>()
    private val nodes  = mutableMapOf<String, Node>()
    private val trees   = mutableMapOf<String, Tree>()

    @GetMapping("/skill")
    fun getSkill(@RequestParam("page") page: Int, @RequestParam("size") size: Int) =
        skills.values.asSequence().chunked(size).drop(page).first()

    @GetMapping("/skill/{id}")
    fun getSkill(@PathVariable id: String) = skills[id]

    @PostMapping("/skill")
    fun postSkill(@RequestBody postSkill: PostSkill ): Skill {
        val id = newUUID()
        val skill = postSkill.toSkill(id)
        skills.put(id, skill)
        return skill
    }

    @DeleteMapping("/skill/{id}")
    fun deleteSkill(@PathVariable id: String) = skills.remove(id)

    @PutMapping("/skill/{id}")
    fun replaceSkill(@PathVariable id: String, @RequestBody putSkill: PutSkill) =
        if (skills.containsKey(id)) skills.replace(id, putSkill.toSkill(id)) else null


    @ExceptionHandler
    @ResponseBody
    fun exceptionHandler(exc: Exception) = exc.message ?: exc.toString()
}

data class PostSkill(val title: String)
fun PostSkill.toSkill(id: String) = Skill(id = id, title = title)

typealias PutSkill = PostSkill

fun newUUID() = UUID.randomUUID().toString()