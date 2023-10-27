package hello.itemservice.repository.jdbctemplate

import hello.itemservice.domain.Item
import hello.itemservice.repository.ItemRepository
import hello.itemservice.repository.ItemSearchCond
import hello.itemservice.repository.ItemUpdateDto
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*
import javax.sql.DataSource

/**
 * JdbcTemplate
 */
class JdbcTemplateItemRepositoryV1(
    dataSource: DataSource,
) : ItemRepository {

    private val template = JdbcTemplate(dataSource)

    override fun save(item: Item?): Item {
        TODO("Not yet implemented")
    }

    override fun update(itemId: Long?, updateParam: ItemUpdateDto?) {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long?): Optional<Item> {
        TODO("Not yet implemented")
    }

    override fun findAll(cond: ItemSearchCond?): MutableList<Item> {
        TODO("Not yet implemented")
    }
}
