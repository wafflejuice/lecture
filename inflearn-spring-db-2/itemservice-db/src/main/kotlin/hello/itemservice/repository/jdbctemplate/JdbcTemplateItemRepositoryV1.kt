package hello.itemservice.repository.jdbctemplate

import hello.itemservice.domain.Item
import hello.itemservice.repository.ItemRepository
import hello.itemservice.repository.ItemSearchCond
import hello.itemservice.repository.ItemUpdateDto
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.util.StringUtils
import java.util.*
import javax.sql.DataSource


/**
 * JdbcTemplate
 */
class JdbcTemplateItemRepositoryV1(
    dataSource: DataSource,
) : ItemRepository {

    private val template = JdbcTemplate(dataSource)

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun save(item: Item): Item {
        val sql = "insert into item(item_name, price, quantity) values (?,?,?)"
        val keyHolder = GeneratedKeyHolder()
        template.update({ connection ->
            // 자동 증가 키
            connection.prepareStatement(sql, arrayOf("id"))
                .apply {
                    setString(1, item.itemName)
                    setInt(2, item.price)
                    setInt(3, item.quantity)
                }
        }, keyHolder)

        val key = keyHolder.key?.toLong()!!
        item.id = key

        return item
    }

    override fun update(itemId: Long, updateParam: ItemUpdateDto) {
        val sql = "update item set item_name=?, price=?, quantity=? where id=?"
        template.update(
            sql,
            updateParam.itemName,
            updateParam.price,
            updateParam.quantity,
            itemId,
        )
    }

    override fun findById(id: Long): Optional<Item> {
        val sql = "select id, item_name, price, quantity from item where id=?"
        return try {
            val item = template.queryForObject(sql, itemRowMapper(), id)
            Optional.of(item!!)
        } catch (e: EmptyResultDataAccessException) {
            Optional.empty()
        }

    }

    override fun findAll(cond: ItemSearchCond): List<Item> {
        var sql = "select id, item_name, price, quantity from item"

        val itemName = cond.itemName
        val maxPrice = cond.maxPrice

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where"
        }
        var andFlag = false
        val param: MutableList<Any> = ArrayList()
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')"
            param.add(itemName!!)
            andFlag = true
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and"
            }
            sql += " price <= ?"
            param.add(maxPrice)
        }

        logger.info("sql={}", sql);

        return template.query(sql, itemRowMapper(), param.toTypedArray())
    }

    private fun itemRowMapper(): RowMapper<Item> =
        RowMapper<Item> { rs, _ ->
            Item(
                id = rs.getLong("id"),
                itemName = rs.getString("item_name"),
                price = rs.getInt("price"),
                quantity = rs.getInt("quantity"),
            )
        }
}
