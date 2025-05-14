package com.project.beauty_care;

import com.project.beauty_care.global.config.QueryDslConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
public abstract class RepositoryTestSupport extends DataBaseConnectionSupport {
}
