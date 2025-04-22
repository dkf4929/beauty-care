package com.project.beauty_care.domain.menu;

import com.project.beauty_care.domain.menu.dto.MenuBaseRequest;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.springframework.stereotype.Component;

@Component
public class MenuValidator {
    //메뉴 생성 시 메뉴 Level과 최하위 메뉴 여부 검증
    public void validateMenuLevelAndIsLeaf(MenuBaseRequest request) {
        if (request.getMenuLevel() < 3 && request.getIsLeaf())
            throw new RequestInvalidException(Errors.CAN_NOT_BE_LEAF_MENU);

        if (request.getMenuLevel() > 3 || (request.getMenuLevel() == 3 && !request.getIsLeaf()))
            throw new RequestInvalidException(Errors.MAX_MENU_DEPTH_ERROR);
    }

    // 상위 메뉴가 사용 불가 상태일 때, 하위 메뉴 활성화 불가
    public void validateParentMenuIsUse(MenuBaseRequest request, Menu parent) {
        if (request.getIsUse() && !parent.getIsUse()) {
            throw new RequestInvalidException(Errors.PARENT_MENU_NOT_USE);
        }
    }

    // 상위 메뉴가 leaf menu => 예외
    public void validateParentMenuIsLeafFalse(Boolean parentIsLeaf) {
        if (parentIsLeaf)
            throw new RequestInvalidException(Errors.MAX_MENU_DEPTH_ERROR);
    }
}
