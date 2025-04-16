package com.project.beauty_care;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
public abstract class RepositoryTestSupport extends DataBaseConnectionSupport {
    // 테스트 시 별도로 주입 필요.
    @MockitoBean
    private JPAQueryFactory queryFactory;
}
