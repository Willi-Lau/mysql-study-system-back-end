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
public class ManagerRenewSchoolLog implements Serializable {
    Integer id;
    Integer managerId;
    Integer schoolId;
    Integer renewDurationDay;
    String createTime;
}
