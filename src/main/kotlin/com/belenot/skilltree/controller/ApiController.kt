package com.belenot.skilltree.controller

import com.belenot.skilltree.domain.Node
import com.belenot.skilltree.domain.Skill
import com.belenot.skilltree.domain.Tree
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class ApiController {
    private val skills  = mutableMapOf<String, Skill>()
    private val nodes  = mutableMapOf<String, Node>()
    private val trees   = mutableMapOf<String, Tree>()

    @GetMapping("/skill")
    fun getSkill(@RequestParam("page") page: Int, @RequestParam("size") size: Int) =
        skills.values.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()

    @GetMapping("/skill/{id}")
    fun getSkill(@PathVariable id: String) =
        if (skills.containsKey(id)) skills[id]
        else throw ResponseStatusException(HttpStatus.NOT_FOUND)

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
        if (skills.containsKey(id)) skills.replace(id, putSkill.toSkill(id))
        else throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @GetMapping("/node")
    fun getNode(@RequestParam("page") page: Int, @RequestParam("size") size: Int) =
        nodes.values.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()

    @GetMapping("/node/{id}")
    fun getNode(@PathVariable id: String) =
        if (nodes.containsKey(id)) nodes[id]
        else throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PostMapping("/node")
    fun postNode(@RequestBody postNode: PostNode ): Node {
        val skill = skills[postNode.skillId]
        if (skill != null) {
            val node = createNode(postNode, skill)
            val id = node.id
            nodes[id] = node
            return node
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found skill with id = ${postNode.skillId}.")
        }
    }

    private fun createNode(
        postNode: PostNode,
        skill: Skill,
        id: String = newUUID()
    ) = Node(
        id = id,
        skill = skill,
        parent = nodes[postNode.parentId],
        children = nodes.values.filter { it.id in postNode.childrenIds })

    @DeleteMapping("/node/{id}")
    fun deleteNode(@PathVariable id: String) = nodes.remove(id)

    @PutMapping("/node/{id}")
    fun replaceNode(@PathVariable id: String, @RequestBody putNode: PutNode): Node {
        if (nodes.containsKey(id)) {
            val skill = skills[putNode.skillId]
            if (skill != null) {
                val node = createNode(putNode, skill, id = id)
                nodes[id] = node
                return node
            } else {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found skill with id = ${putNode.skillId}.")
            }
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/tree")
    fun getTree(@RequestParam("page") page: Int, @RequestParam("size") size: Int) =
        trees.values.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()

    @GetMapping("/tree/{id}")
    fun getTree(@PathVariable id: String) =
        if (trees.containsKey(id)) trees[id]
        else throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PostMapping("/tree")
    fun postTree(@RequestBody postTree: PostTree ): Tree {
        val root = nodes[postTree.rootId]
        if (root != null) {
            val tree = createTree(postTree, root)
            val id = tree.id
            trees[id] = tree
            return tree
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found root node with id = ${postTree.rootId}.")
        }
    }

    private fun createTree(
        postTree: PostTree,
        root: Node,
        id: String = newUUID()
    ) = Tree(
        id = id,
        root = root,
        description = postTree.description
    )

    @DeleteMapping("/tree/{id}")
    fun deleteTree(@PathVariable id: String) = trees.remove(id)

    @PutMapping("/tree/{id}")
    fun replaceTree(@PathVariable id: String, @RequestBody putTree: PutTree): Tree {
        if (trees.containsKey(id)) {
            val root = nodes[putTree.rootId]
            if (root != null) {
                val tree = createTree(putTree, root, id = id)
                trees[id] = tree
                return tree
            } else {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found root node with id = ${putTree.rootId}.")
            }
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}

data class PostSkill(val title: String)
fun PostSkill.toSkill(id: String) = Skill(id = id, title = title)

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

fun newUUID() = UUID.randomUUID().toString()