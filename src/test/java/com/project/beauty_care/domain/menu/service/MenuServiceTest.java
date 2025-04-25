package com.project.beauty_care.domain.menu.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.MenuValidator;
import com.project.beauty_care.domain.menu.dto.AdminMenuCreateRequest;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.dto.UserMenuResponse;
import com.project.beauty_care.domain.menu.repository.MenuRepository;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.menuRole.repository.MenuRoleRepository;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Authentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

// 외부 의존 너무 많음 => 통합 테스트
@Transactional
class MenuServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private MenuService service;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuRoleRepository menuRoleRepository;

    @MockitoBean
    private MenuValidator validator;
    
    final String ROLE_ADMIN = Authentication.ADMIN.getName();

    @DisplayName("메뉴 생성 (최상위 메뉴)")
    @Test
    void createTopMenu() {
        // given
        buildRoleAndSave();

        final String menuName = "TOP 메뉴";
        final String menuPath = "/beauty-care";
        final Boolean isLeaf = Boolean.FALSE;
        final Boolean isUse = Boolean.TRUE;
        final int sortNumber = 0;

        AdminMenuCreateRequest request = buildRequest(List.of(ROLE_ADMIN),
                menuName,
                menuPath,
                isLeaf,
                isUse,
                sortNumber,
                null);

        // when
        mockingValidator();

        AdminMenuResponse response = service.createMenu(request);

        // then
        assertThat(response)
                .extracting("menuName", "menuPath", "isLeaf", "sortNumber", "isUse")
                .containsExactly(menuName, menuPath, isLeaf, sortNumber, isUse);
    }

    @DisplayName("메뉴 생성(하위 메뉴)")
    @Test
    void createChildrenMenu() {
        // given
        Menu parent = createBaseMenuWithChildren();

        final String menuName = "테스트 메뉴";
        final String menuPath = "/beauty-care/admin/menu/test";
        final Boolean isLeaf = Boolean.TRUE;
        final Integer sortNumber = 0;
        final Boolean isUse = Boolean.TRUE;

        // request 생성
        AdminMenuCreateRequest request = buildRequest(
                List.of(ROLE_ADMIN),
                menuName,
                menuPath,
                isLeaf,
                isUse,
                sortNumber,
                parent.getId());

        // when
        mockingValidator();

        // create menu
        AdminMenuResponse response = service.createMenu(request);

        List<Menu> children = parent.getChildren();

        // then
        assertThat(response)
                .extracting("menuName", "menuPath", "isLeaf", "sortNumber", "isUse")
                .containsExactly(menuName, menuPath, isLeaf, sortNumber, isUse);

        assertThat(children).hasSize(1);

        Menu child = children.getFirst();

        assertThat(response)
                .extracting("menuName", "menuPath", "isLeaf", "sortNumber", "isUse")
                .containsExactly(child.getMenuName(),
                        child.getMenuPath(),
                        child.getIsLeaf(),
                        child.getSortNumber(),
                        child.getIsUse()
                );
    }

    @DisplayName("계층형 구조로 메뉴를 조회한다.")
    @ParameterizedTest
    @CsvSource({"ADMIN", "' '", "USER"})
    void findMenuWithHierarchyStructure(String role) {
        // given
        createBaseMenuWithChildren();

        // when
        final AdminMenuResponse response = service.findAllMenu(role);

        AtomicInteger menuLevel = new AtomicInteger(1);
        AdminMenuResponse bottomMenu = getBottomMenuAndMenuLevelIncrement(response, menuLevel);

        // then
        if (role.equals(ROLE_ADMIN) || role.isEmpty()) {
            assertThat(menuLevel.get()).isEqualTo(4);
            assertThat(bottomMenu)
                    .isNotNull()
                    .extracting("menuName", "menuPath", "isLeaf")
                    .containsExactly("메뉴 관리",
                            "/beauty-care/admin/menu/manage",
                            Boolean.TRUE);
        } else {
            assertThat(menuLevel.get()).isEqualTo(3);

            assertThat(bottomMenu)
                    .isNotNull()
                    .extracting("menuName", "menuPath", "isLeaf")
                    .containsExactly("메뉴",
                            "/beauty-care/admin/menu",
                            Boolean.FALSE);
        }
    }

    @DisplayName("권한이 있는 메뉴를 계층형 구조로 조회한다.")
    @ParameterizedTest
    @CsvSource({"ADMIN", "ANONYMOUS"})
    void findMyMenuByAuthority(String role) {
        // given
        createBaseMenuWithChildren();

        // when
        final UserMenuResponse response = service.findMenuByAuthority(role);

        AtomicInteger menuLevel = new AtomicInteger(1);
        UserMenuResponse bottomMenu = getBottomMenuAndMenuLevelIncrement(response, menuLevel);

        // then
        if (role.equals(ROLE_ADMIN) || role.isEmpty()) {
            assertThat(menuLevel.get()).isEqualTo(4);
            assertThat(bottomMenu)
                    .isNotNull()
                    .extracting("menuName", "menuPath", "isLeaf")
                    .containsExactly("메뉴 관리",
                            "/beauty-care/admin/menu/manage",
                            Boolean.TRUE);
        } else {
            assertThat(menuLevel.get()).isEqualTo(1);
            assertThat(bottomMenu).isNull();
        }
    }

    private AdminMenuResponse getBottomMenuAndMenuLevelIncrement(AdminMenuResponse response, AtomicInteger menuLevel) {
        List<AdminMenuResponse> children = response.getChildren();

        if (children.isEmpty()) {
            return response;
        } else {
            menuLevel.incrementAndGet();
            return getBottomMenuAndMenuLevelIncrement(children.getFirst(), menuLevel);
        }
    }

    private UserMenuResponse getBottomMenuAndMenuLevelIncrement(UserMenuResponse response, AtomicInteger menuLevel) {
        if (response == null) return null;

        List<UserMenuResponse> children = response.getChildren();

        if (children == null) return response;
        else {
            menuLevel.incrementAndGet();
            return getBottomMenuAndMenuLevelIncrement(children.getFirst(), menuLevel);
        }
    }


    private Menu buildMenu(String menuName,
                           String menuPath,
                           Boolean isUse,
                           Integer sortNumber,
                           Boolean isLeaf) {
        return Menu.builder()
                .menuName(menuName)
                .menuPath(menuPath)
                .isUse(isUse)
                .sortNumber(sortNumber)
                .isLeaf(isLeaf)
                .build();
    }

    private Role buildRole(Boolean isUse, String role) {
        return Role.builder()
                .isUse(isUse)
                .roleName(role)
                .build();
    }

    private MenuRole buildMenuRole(Menu children, Role role) {
        return MenuRole.builder()
                .menu(children)
                .role(role)
                .build();
    }

    private Menu createBaseMenuWithChildren() {
        Role role = buildRole(Boolean.TRUE, ROLE_ADMIN);

        Menu level1 = buildMenu("TOP",
                "/beauty-care",
                Boolean.TRUE,
                0,
                Boolean.FALSE);

        Menu level2 = buildMenu("관리자",
                "/beauty-care/admin",
                Boolean.TRUE,
                0,
                Boolean.FALSE);

        Menu level3 = buildMenu("메뉴",
                "/beauty-care/admin/menu",
                Boolean.TRUE,
                0,
                Boolean.FALSE);

        Menu level4 = buildMenu("메뉴 관리",
                "/beauty-care/admin/menu/manage",
                Boolean.TRUE,
                0,
                Boolean.TRUE);

        MenuRole menuRole = buildMenuRole(level4, role);

        level1.getChildren().add(level2);
        level2.getChildren().add(level3);
        level3.getChildren().add(level4);

        level4.updateParent(level3);
        level3.updateParent(level2);
        level2.updateParent(level1);

        menuRepository.saveAllAndFlush(List.of(level4, level3, level2, level1));
        roleRepository.saveAndFlush(role);
        menuRoleRepository.saveAndFlush(menuRole);

        level4.updateMenuRole(List.of(menuRole));

        return level4;
    }

    private Role buildRoleAndSave() {
        Role role = buildRole(Boolean.TRUE, ROLE_ADMIN);

        roleRepository.save(role);

        return role;
    }

    private AdminMenuCreateRequest buildRequest(List<String> role,
                                                String menuName,
                                                String menuPath,
                                                Boolean isLeaf,
                                                Boolean isUse,
                                                Integer sortNumber,
                                                Long parentId) {
        return AdminMenuCreateRequest.builder()
                .roleNames(role)
                .menuName(menuName)
                .menuPath(menuPath)
                .isLeaf(isLeaf)
                .isUse(isUse)
                .sortNumber(sortNumber)
                .parentMenuId(parentId)
                .build();
    }

    private void mockingValidator() {
        doNothing().when(validator).validateMenuLevelAndIsLeaf(any());
        doNothing().when(validator).validateParentMenuIsUse(any(), any());
        doNothing().when(validator).validateParentMenuIsLeafFalse(any());
    }
}