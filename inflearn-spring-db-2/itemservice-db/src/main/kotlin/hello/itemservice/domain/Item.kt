package hello.itemservice.domain;

class Item(
    id: Long,
    itemName: String,
    price: Int,
    quantity: Int,
) {

    constructor() :
            this(id = 0L, itemName = "", price = 0, quantity = 0)

    var id: Long = id

    var itemName: String = itemName

    var price: Int = price

    var quantity: Int = quantity

    override fun hashCode(): Int {
        return id.hashCode() + itemName.hashCode() + price.hashCode() + quantity.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if ((other is Item).not()) return false
        other as Item

        return id == other.id && itemName == other.itemName && price == other.price && quantity == other.quantity
    }

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
