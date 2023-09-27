package study.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import study.querydsl.entity.Hello
import study.querydsl.entity.QHello

@SpringBootTest
@Transactional
class InflearnQuerydslApplicationTests {

    @Autowired
    private lateinit var em: EntityManager

    @Test
    fun contextLoads() {
        val hello = Hello()
        em.persist(hello)

        val query = JPAQueryFactory(em)
        val qHello = QHello.hello

        val result = query
            .selectFrom(qHello)
            .fetchOne()

        assertEquals(hello, result)
        assertEquals(hello.id, result!!.id)
    }

}
