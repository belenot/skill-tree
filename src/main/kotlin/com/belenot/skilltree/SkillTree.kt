package com.belenot.skilltree

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
open class SkillTree

fun main(args: Array<String>) {
    runApplication<SkillTree>(*args)
}