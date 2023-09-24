package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.jdbc.support.JdbcUtils
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

/**
 * SQLExceptionTranslator 추가
 */
class MemberRepositoryV4_2(
    private val dataSource: DataSource,
) : MemberRepository {
    private val exTranslator: SQLExceptionTranslator = SQLErrorCodeSQLExceptionTranslator(dataSource)

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun save(member: Member): Member {
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
            throw exTranslator.translate("save", sql, e)!!
        } finally {
            close(con = con, stmt = pstmt, rs = null)
        }
    }

    override fun findById(memberId: String): Member {
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
            throw exTranslator.translate("findById", sql, e)!!
        } finally {
            close(con = con, stmt = pstmt, rs = rs)
        }
    }

    override fun update(memberId: String, money: Int) {
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
            throw exTranslator.translate("update", sql, e)!!
        } finally {
            close(con = con, stmt = pstmt, rs = null)
        }
    }

    override fun delete(memberId: String) {
        val sql = "delete from member where member_id=?"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, memberId)

            pstmt.executeUpdate()
        } catch (e: SQLException) {
            throw exTranslator.translate("delete", sql, e)!!
        } finally {
            close(con = con, stmt = pstmt, rs = null)
        }
    }

    private fun close(con: Connection?, stmt: Statement?, rs: ResultSet?) {
        JdbcUtils.closeResultSet(rs)
        JdbcUtils.closeStatement(stmt)

        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource)
    }

    private fun getConnection(): Connection {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        val con = DataSourceUtils.getConnection(dataSource)

        logger.info("get connection=${con}, class=${con::class}")
        return con
    }
}
