package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * JdbcTemplate 사용
 */
class MemberRepositoryV5(
    private val dataSource: DataSource,
) : MemberRepository {
    private val template = JdbcTemplate(dataSource)

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values (?, ?)"
        template.update(sql, member.memberId, member.money)
        return member
    }

    override fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"
        return template.queryForObject(sql, memberRowMapper(), memberId)!!
    }

    private fun memberRowMapper(): RowMapper<Member> {
        return RowMapper<Member> { rs: ResultSet, rowNum: Int ->
            Member(
                memberId = rs.getString("member_id"),
                money = rs.getInt("money"),
            )
        }
    }

    override fun update(memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"
        template.update(sql, money, memberId)
    }

    override fun delete(memberId: String) {
        val sql = "delete from member where member_id=?"
        template.update(sql, memberId)
    }
}
