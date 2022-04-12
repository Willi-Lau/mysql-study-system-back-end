package com.lwy.demo.entity;


import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ManagerLoginHistory implements Serializable {
    Integer id;
    Integer userId;
    String loginTime;
    String localHostAddress;
}


