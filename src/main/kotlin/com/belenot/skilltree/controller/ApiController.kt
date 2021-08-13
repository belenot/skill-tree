package com.belenot.skilltree.controller

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.domain.Tree
import com.belenot.skilltree.service.NodeService
import com.belenot.skilltree.service.SkillService
import com.belenot.skilltree.service.TreeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import java.net.http.HttpResponse
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

@RestController
class ApiController {

    // TODO move dependencies to constructor
    @Autowired
    private lateinit var skillService: SkillService
    @Autowired
    private lateinit var nodeService: NodeService
    @Autowired
    private lateinit var treeSerivce: TreeService

    @GetMapping("/skill")
    fun getSkill(@RequestParam("page") page: Int, @RequestParam("size") size: Int) =  skillService.getSkill(page, size)

    @GetMapping("/skill/{id}")
    fun getSkill(@PathVariable id: String) = skillService.getSkill(id)?:throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PostMapping("/skill")
    fun postSkill(@RequestBody postSkill: PostSkill ) = skillService.createSkill(postSkill.title)

    @DeleteMapping("/skill/{id}")
    fun deleteSkill(@PathVariable id: String) = skillService.deleteSkill(id)

    @PutMapping("/skill/{id}")
    fun replaceSkill(@PathVariable id: String, @RequestBody putSkill: PutSkill) = skillService.replaceSkill(id, putSkill.title)?:throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @GetMapping("/node")
    fun getNode(@RequestParam("page") page: Int, @RequestParam("size") size: Int) = nodeService.getNode(page, size)

    @GetMapping("/node/{id}")
    fun getNode(@PathVariable id: String) = nodeService.getNode(id)?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PostMapping("/node")
    fun postNode(@RequestBody postNode: PostNode ) = nodeService.createNode(postNode.skillId, postNode.childrenIds.toSet(), postNode.parentId)

    @DeleteMapping("/node/{id}")
    fun deleteNode(@PathVariable id: String) = nodeService.deleteNode(id)

    @PutMapping("/node/{id}")
    fun replaceNode(@PathVariable id: String, @RequestBody putNode: PutNode) = nodeService.replaceNode(id, putNode.skillId, putNode.childrenIds.toSet(), putNode.parentId)?:throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @GetMapping("/tree")
    fun getTree(@RequestParam("page") page: Int, @RequestParam("size") size: Int) = treeSerivce.getTree(page, size)

    @GetMapping("/tree/{id}")
    fun getTree(@PathVariable id: String) = treeSerivce.getTree(id)?:throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PostMapping("/tree")
    fun postTree(@RequestBody postTree: PostTree ) = treeSerivce.createTree(postTree)

    @DeleteMapping("/tree/{id}")
    fun deleteTree(@PathVariable id: String) = treeSerivce.deleteTree(id)

    @PutMapping("/tree/{id}")
    fun replaceTree(@PathVariable id: String, @RequestBody putTree: PutTree) = treeSerivce.replaceTree(id, putTree)?:throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun exceptionHandler(exc: SkillTreeException) {}

}

data class PostSkill(val title: String)
typealias PutSkill = PostSkill

data class PostNode(
    val skillId: String,
    val childrenIds: List<String> = emptyList<String>(),
    val parentId: String? = null)

typealias PutNode = PostNode

data class PostTree(
    val rootId: String,
    val description: String = ""
)

typealias PutTree = PostTree