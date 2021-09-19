package com.belenot.skilltree.dao

import com.belenot.skilltree.utils.newUUID

class LocalSkillDao(val skills: MutableMap<String, SkillDto>): GenericLocalDao<SkillDto>(skills)
class LocalNodeDao(val nodes: MutableMap<String, NodeDto>): GenericLocalDao<NodeDto>(nodes)
class LocalTreeDao(val trees: MutableMap<String, TreeDto>): GenericLocalDao<TreeDto>(trees)

open class GenericLocalDao<T: BaseDto>(val collection: MutableMap<String, T>): BaseDao<T> {
    override fun get(skip: Int, size: Int) {
        collection.values.toList()[skip ]
    }

    override fun get(id: String): T? {
        return collection[id]
    }

    override fun save(dto: T): String {
        val id = newUUID()
        val newDto = dto.dup(id)
        collection[id] = newDto as T
        return id
    }

    override fun update(dto: T) {
        val id = dto.id
        if (id == null) throw DaoException("dto id should not be null in update.", null)
        collection[id] = dto
    }

    override fun delete(id: String) {
        collection.remove(id)
    }
}