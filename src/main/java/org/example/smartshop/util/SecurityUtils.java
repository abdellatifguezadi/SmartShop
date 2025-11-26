package org.example.smartshop.util;

import jakarta.servlet.http.HttpSession;
import org.example.smartshop.enums.UserRole;
import org.example.smartshop.exception.ForbiddenException;
import org.example.smartshop.exception.UnauthorizedException;

public class SecurityUtils {


    public static Long getAuthenticatedUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("Non authentifie");
        }
        return userId;
    }

    public static void requireAdmin(HttpSession session) {
        Long userId = getAuthenticatedUserId(session);
        UserRole role = (UserRole) session.getAttribute("role");

        if (role != UserRole.ADMIN) {
            throw new ForbiddenException("Acces reserve aux administrateurs");
        }
    }

}
