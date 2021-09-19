package com.belenot.skilltree.dao

interface BaseDto {
    val id: String?
    fun dup(id: String?): BaseDto
}

data class SkillDto (
    override val id: String? = null,
    val title: String? = null
): BaseDto {
    override fun dup(id: String?): BaseDto = copy(id = id)
}

data class NodeDto (
    override val id: String? = null,
    val skillId: String? = null,
    val childrenIds: Collection<String> = emptySet(),
    val parentId: String
): BaseDto {
    override fun dup(id: String?): BaseDto = copy(id = id)
}

data class TreeDto (
    override val id: String?,
    val rootId: String? = null,
    val description: String? = null
): BaseDto {
    override fun dup(id: String?): BaseDto = copy(id = id)
}

@FunctionalInterface
interface BaseGetDao<T: BaseDto> {
    /**
     * @param id key associated with entity
     * @return dto or null if not found
     */
    fun get(id: String): T?
}
@FunctionalInterface
interface BaseSaveDao<T: BaseDto> {
    /**
     * After successful execution of save operation returned id.
     * It must satisfy given property get(id) != null
     * @param dto id may be any value and doesn't matter
     * @return id of new entity
     */
    fun save(dto: T): String
}
@FunctionalInterface
interface BaseUpdateDao<T: BaseDto> {
    /**
     * @param dto id must be one of those for which get(id) != null
     * @throws DaoException if dto.id is null
     */
    fun update(dto: T)
}
@FunctionalInterface
interface BaseDeleteDao<T: BaseDto> {
    /**
     * After successful execution of delete
     * it must satisfy given property get(id) == null
     * @param dto id must be one of those for which get(id) != null
     */
    fun delete(id: String)
}
@FunctionalInterface
interface BaseGetAllDao<T: BaseDto> {
    /**
     * @param skip how many entities skip before start of result collection
     * @param size how many entities count after start
     * @return list of entities
     */
    fun get(skip: Int, size: Int): List<T>
}


interface BaseDao<T: BaseDto> : BaseGetDao<T>, BaseSaveDao<T>, BaseUpdateDao<T>, BaseDeleteDao<T>, BaseGetAllDao<T>

interface SkillDao : BaseDao<SkillDto>
interface NodeDao : BaseDao<NodeDto>
interface TreeDao : BaseDao<TreeDto>

class DaoException(message: String?, cause: Throwable?) : Exception(message, cause)