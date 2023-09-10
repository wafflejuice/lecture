package hello.jdbc.connection

import hello.jdbc.connection.ConnectionConst.PASSWORD
import hello.jdbc.connection.ConnectionConst.URL
import hello.jdbc.connection.ConnectionConst.USERNAME
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DBConnectionUtil {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getConnection(): Connection {
        try {
            val connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)
            logger.info("get connection=${connection}, class=${connection.javaClass}")
            return connection
        } catch (e: SQLException) {
            error(e)
        }
    }
}
