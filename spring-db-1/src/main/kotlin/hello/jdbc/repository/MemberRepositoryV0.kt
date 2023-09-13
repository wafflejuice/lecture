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

    fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, memberId)
            rs = pstmt.executeQuery()
            if (rs.next()) {
                return Member(
                    memberId = rs.getString("member_id"),
                    money = rs.getInt("money"),
                )
            } else {
                throw NoSuchElementException("member not found memberId=$memberId")
            }
        } catch (e: SQLException) {
            logger.error("db error", e)
            throw e
        } finally {
            close(
                con = con,
                stmt = pstmt,
                rs = rs,
            )
        }
    }

    fun update(memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setInt(1, money)
            pstmt.setString(2, memberId)

            val resultSize = pstmt.executeUpdate()
            logger.info("resultSize=$resultSize")
        } catch (e: SQLException) {
            logger.error("db error", e)
            throw e
        } finally {
            close(con = con, stmt = pstmt, rs = null)
        }
    }

    fun delete(memberId: String) {
        val sql = "delete from member where member_id=?"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, memberId)

            pstmt.executeUpdate()
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
