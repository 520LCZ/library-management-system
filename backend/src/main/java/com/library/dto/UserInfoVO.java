package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户信息视图(无 password)
 *
 * @author library
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO implements Serializable {

    private Long id;
    private String username;
    private String nickname;
    private String role;
    private String avatar;
    private String email;
    private String phone;
    private Integer status;
}
