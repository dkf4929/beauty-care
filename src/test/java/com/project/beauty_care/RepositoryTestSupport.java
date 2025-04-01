package com.project.beauty_care;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
public abstract class RepositoryTestSupport extends DataBaseConnectionSupport {
}
