package com.lwy.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Builder
@AllArgsConstructor
public class User implements Serializable {
    String studentNumber;
    String name;
    String identityCardNumber;
    String password;
    String phone;
    String university;
    String className;
}
