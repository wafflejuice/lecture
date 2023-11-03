package hello.itemservice.service

import hello.itemservice.domain.Item
import hello.itemservice.repository.ItemRepository
import hello.itemservice.repository.ItemSearchCond
import hello.itemservice.repository.ItemUpdateDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class ItemServiceV1(
    private val itemRepository: ItemRepository,
) : ItemService {
    override fun save(item: Item): Item {
        return itemRepository.save(item)
    }

    override fun update(itemId: Long, updateParam: ItemUpdateDto) {
        itemRepository.update(itemId, updateParam)
    }

    override fun findById(id: Long): Optional<Item> {
        return itemRepository.findById(id)
    }

    override fun findItems(cond: ItemSearchCond): List<Item> {
        return itemRepository.findAll(cond)
    }
}
