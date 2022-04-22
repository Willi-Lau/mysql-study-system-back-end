package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.ManagerRenewSchoolLog;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRenewSchoolLogDao {

    @Insert("insert into managerenewschoollog(managerId,schoolId,renewDurationDay,createTime) values(#{managerId},#{schoolId},#{renewDurationDay},#{createTime})")
    void insertManagerRenewSchoolLog(ManagerRenewSchoolLog managerRenewSchoolLog);
}
