package hello.itemservice.domain;

class Item(
    id: Long,
    itemName: String,
    price: Int,
    quantity: Int,
) {
    var id: Long = id

    var itemName: String = itemName

    var price: Int = price

    var quantity: Int = quantity

    companion object {
        fun create(
            itemName: String,
            price: Int,
            quantity: Int,
        ): Item =
            Item(
                id = 0L,
                itemName = itemName,
                price = price,
                quantity = quantity,
            )
    }
}
