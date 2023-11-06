package hello.itemservice.repository.jdbctemplate

import hello.itemservice.domain.Item
import hello.itemservice.repository.ItemRepository
import hello.itemservice.repository.ItemSearchCond
import hello.itemservice.repository.ItemUpdateDto
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.util.StringUtils
import java.util.*
import javax.sql.DataSource

/**
 * SimpleJdbcInsert
 *
 */
class JdbcTemplateItemRepositoryV3(
    dataSource: DataSource,
) : ItemRepository {

    private val template = NamedParameterJdbcTemplate(dataSource)
    private val jdbcInsert = SimpleJdbcInsert(dataSource)
        .withTableName("item")
        .usingGeneratedKeyColumns("id")
//        .usingColumns("item_name", "price", "quantity") // 생략 가능

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun save(item: Item): Item {
        val param: SqlParameterSource = BeanPropertySqlParameterSource(item)
        val key = jdbcInsert.executeAndReturnKey(param)
        item.id = key.toLong()

        return item
    }

    override fun update(itemId: Long, updateParam: ItemUpdateDto) {
        val sql = "update item set item_name=:itemName, price=:price, quantity=:quantity where id=:id"

        val param: SqlParameterSource = MapSqlParameterSource()
            .addValue("itemName", updateParam.itemName)
            .addValue("price", updateParam.price)
            .addValue("quantity", updateParam.quantity)
            .addValue("id", itemId)

        template.update(sql, param)
    }

    override fun findById(id: Long): Optional<Item> {
        val sql = "select id, item_name, price, quantity from item where id=:id"
        return try {
            val param = mapOf("id" to id)
            val item = template.queryForObject(sql, param, itemRowMapper())
            Optional.of(item!!)
        } catch (e: EmptyResultDataAccessException) {
            Optional.empty()
        }

    }

    override fun findAll(cond: ItemSearchCond): List<Item> {
        var sql = "select id, item_name, price, quantity from item"

        val itemName = cond.itemName
        val maxPrice = cond.maxPrice

        val param: SqlParameterSource = BeanPropertySqlParameterSource(cond)

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where"
        }
        var andFlag = false
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%', :itemName, '%')"
            andFlag = true
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and"
            }
            sql += " price <= :maxPrice"
        }

        logger.info("sql={}", sql);

        return template.query(sql, param, itemRowMapper())
    }

    private fun itemRowMapper(): RowMapper<Item> =
        BeanPropertyRowMapper.newInstance(Item::class.java) // camel 변환 지원
}
