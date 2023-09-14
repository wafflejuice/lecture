package hello.jdbc.connection

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

    private fun useDataSource(dataSource: DataSource) {
        val con1 = dataSource.connection
        val con2 = dataSource.connection

        logger.info("connection=$con1, class=${con1.javaClass}")
        logger.info("connection=$con2, class=${con2.javaClass}")
    }
}
