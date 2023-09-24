package hello.jdbc.exception.translator

import hello.jdbc.connection.ConnectionConst.PASSWORD
import hello.jdbc.connection.ConnectionConst.URL
import hello.jdbc.connection.ConnectionConst.USERNAME
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import java.sql.SQLException

internal class SpringExceptionTranslatorTest {

    private val dataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Test
    fun sqlExceptionErrorCode() {
        val sql = "select bad grammar"

        try {
            val con = dataSource.connection
            val stmt = con.prepareStatement(sql)
            stmt.executeQuery()
        } catch (e: SQLException) {
            val errorCode = e.errorCode
            assertEquals(42122, errorCode) // for H2 error
            logger.info("errorCode=$errorCode")
        }
    }

    @Test
    fun exceptionTranslator() {
        val sql = "select bad grammar"

        try {
            val con = dataSource.connection
            val stmt = con.prepareStatement(sql)
            stmt.executeQuery()
        } catch (e: SQLException) {
            val errorCode = e.errorCode
            assertEquals(42122, errorCode) // for H2 error

            //org.springframework.jdbc.support.sql-error-codes.xml
            val exTranslator = SQLErrorCodeSQLExceptionTranslator(dataSource)
            val resultEx = exTranslator.translate("select", sql, e)

            logger.info("resultEx=$resultEx")
            assertEquals(BadSqlGrammarException::class.java, resultEx!!.javaClass)
        }
    }
}
