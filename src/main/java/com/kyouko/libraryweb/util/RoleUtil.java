package com.kyouko.libraryweb.util;

import com.kyouko.libraryweb.domain.User;

import java.util.Objects;

import static com.kyouko.libraryweb.common.LibraryConstants.*;

public class RoleUtil {

    public static boolean isAdmin(User user) {
        return Objects.equals(user.getRole(), ROLE_ADMIN_NUM);
    }

    public static boolean isReader(User user) {
        return Objects.equals(user.getRole(), ROLE_READER_NUM);
    }

}
