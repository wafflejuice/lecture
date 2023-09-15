package hello.jdbc.connection

import com.zaxxer.hikari.HikariDataSource
import hello.jdbc.connection.ConnectionConst.PASSWORD
import hello.jdbc.connection.ConnectionConst.URL
import hello.jdbc.connection.ConnectionConst.USERNAME
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.DriverManager
import javax.sql.DataSource

internal class ConnectionTest {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Test
    fun driverManager() {
        val con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD)
        val con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD)

        logger.info("connection=$con1, class=${con1.javaClass}")
        logger.info("connection=$con2, class=${con2.javaClass}")
    }

    @Test
    fun dataSourceDriverManager() {
        // DriverManagerDataSource - 항상 새로운 커넥션을 획득
        val dataSource: DataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)
        useDataSource(dataSource = dataSource)
    }

    @Test
    fun dataSourceConnectionPool() {
        // Connection Pooling
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = URL
        dataSource.username = USERNAME
        dataSource.password = PASSWORD
        dataSource.maximumPoolSize = 10
        dataSource.poolName = "MyPool"

        useDataSource(dataSource)
        Thread.sleep(3000)
    }

    private fun useDataSource(dataSource: DataSource) {
        val con1 = dataSource.connection
        val con2 = dataSource.connection

        logger.info("connection=$con1, class=${con1.javaClass}")
        logger.info("connection=$con2, class=${con2.javaClass}")

        /* stuck example
        val connections = mutableListOf<Connection>()
        (1..11).forEach { _ ->
            val con = dataSource.connection
            connections.add(con)
            logger.info("connection=$con, class=${con.javaClass}")
        }
         */
    }
}
