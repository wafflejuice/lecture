package hello.itemservice.config

import hello.itemservice.repository.ItemRepository
import hello.itemservice.repository.jdbctemplate.JdbcTemplateItemRepositoryV1
import hello.itemservice.service.ItemService
import hello.itemservice.service.ItemServiceV1
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class JdbcTemplateV1Config(
    private val dataSource: DataSource,
) {

    @Bean
    fun itemService(): ItemService {
        return ItemServiceV1(itemRepository())
    }

    @Bean
    fun itemRepository(): ItemRepository {
        return JdbcTemplateItemRepositoryV1(dataSource = dataSource)
    }

}
