package hello.jdbc.repository

import hello.jdbc.connection.DBConnectionUtil
import hello.jdbc.domain.Member
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * JDBC - DriverManager 사용
 */
class MemberRepositoryV0 {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values (?, ?)"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, member.memberId)
            pstmt.setInt(2, member.money)
            pstmt.executeUpdate()

            return member
        } catch (e: SQLException) {
            logger.error("db error", e)
            throw e
        } finally {
            close(con = con, stmt = pstmt, rs = null)
        }
    }

    private fun close(con: Connection?, stmt: Statement?, rs: ResultSet?) {
        try {
            rs?.close()
        } catch (e: SQLException) {
            logger.info("error", e)
        }

        try {
            stmt?.close()
        } catch (e: SQLException) {
            logger.info("error", e)
        }

        try {
            con?.close()
        } catch (e: SQLException) {
            logger.info("error", e)
        }
    }

    private fun getConnection() =
        DBConnectionUtil.getConnection()
}
