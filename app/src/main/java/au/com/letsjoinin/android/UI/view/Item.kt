package au.com.letsjoinin.android.UI.view

open class Item(val title: String, val message: String) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val item = o as Item?

        return if (title != item!!.title) false else message == item.message
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }
}
