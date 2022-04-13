package com.lwy.demo.TO;

import com.lwy.demo.entity.School;
import com.lwy.demo.utils.TimeUtils;
import lombok.*;

import java.io.Serializable;
import java.text.ParseException;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class SchoolDTO implements Serializable {
    Integer id;
    String name;
    String createTime;
    String deadline;
    Integer studentNum;

    public static SchoolDTO schoolTransformSchoolDTO(School school) throws ParseException {
        SchoolDTO schoolDTO = new SchoolDTO();
        schoolDTO.setId(school.getId());
        schoolDTO.setName(school.getName());
        schoolDTO.setStudentNum(school.getStudentNum());
        String createTime = TimeUtils.changeTimeLongToString(Long.parseLong(school.getCreateTime()));
        String deadline = TimeUtils.changeTimeLongToString(Long.parseLong(school.getDeadline()));
        schoolDTO.setCreateTime(createTime);
        schoolDTO.setDeadline(deadline);
        return schoolDTO;
    }

}
