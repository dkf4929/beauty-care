package com.project.beauty_care.domain.menu;

import com.project.beauty_care.domain.menu.dto.AdminMenuCreateRequest;
import com.project.beauty_care.domain.menu.dto.AdminMenuUpdateRequest;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.springframework.stereotype.Component;

@Component
public class MenuValidator {
    //메뉴 생성 시 메뉴 Level과 최하위 메뉴 여부 검증
    public void validateMenuLevelAndIsLeaf(AdminMenuCreateRequest request) {
        if (request.getMenuLevel() > 3 || (request.getMenuLevel() == 3 && !request.getIsLeaf())) {
            throw new RequestInvalidException(Errors.MAX_MENU_DEPTH_ERROR);
        }
    }

    // 메뉴 수정 시 부모 메뉴와 leaf 검증
    public void validateParentMenu(AdminMenuUpdateRequest request, Menu parent) {
        if (parent.getIsLeaf() && request.getIsLeaf()) {
            throw new RequestInvalidException(Errors.MAX_MENU_DEPTH_ERROR);
        }
    }

    // 상위 메뉴가 사용 불가 상태일 때, 하위 메뉴 활성화 불가
    public void validateParentMenuIsUse(AdminMenuCreateRequest request, Menu parent) {
        if (request.getIsUse() && !parent.getIsUse()) {
            throw new RequestInvalidException(Errors.PARENT_MENU_NOT_USE);
        }
    }
}
