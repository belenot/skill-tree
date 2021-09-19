package com.belenot.skilltree.utils

import com.belenot.skilltree.SkillTreeException
import com.belenot.skilltree.dao.BaseDao
import com.belenot.skilltree.dao.BaseDto
import com.belenot.skilltree.service.TreeService
import java.util.*

const val PAGING_VALIDATION_VIOLATION = "Page must be greater then or equal zero and size must be greater then zero."

fun newUUID() = UUID.randomUUID().toString()

fun <T>paged(collection: Collection<T>, page: Int, size: Int) = if (page < 0 || size <= 0)
    throw SkillTreeException(PAGING_VALIDATION_VIOLATION)
    else collection.asSequence().chunked(size).drop(page).firstOrNull()?: emptyList()
fun <T: BaseDto>paged(dao: BaseDao<T>, page: Int, size: Int) = if (page < 0 || size <= 0)
    throw SkillTreeException(PAGING_VALIDATION_VIOLATION)
    else dao.get(page * size, size)
