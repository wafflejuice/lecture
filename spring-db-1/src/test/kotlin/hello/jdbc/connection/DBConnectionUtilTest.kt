package hello.jdbc.connection

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class DBConnectionUtilTest {

    @Test
    fun connection() {
        val connection = DBConnectionUtil.getConnection()
        assertNotNull(connection)
    }
}
