package com.lwy.demo.entity;


import lombok.*;

import java.io.Serializable;

/**
 * @author Administrator
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class ManagerBroadcast implements Serializable {
    Integer id;
    Integer managerId;
    String time;
    String context;
}
