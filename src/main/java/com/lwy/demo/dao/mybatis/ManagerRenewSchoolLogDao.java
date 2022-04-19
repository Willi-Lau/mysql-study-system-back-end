package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.ManagerRenewSchoolLog;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRenewSchoolLogDao {

    @Insert("insert into managerenewschoollog(managerId,schoolId,renewDurationDay) values(#{managerId},#{schoolId},#{renewDurationDay})")
    void insertManagerRenewSchoolLog(ManagerRenewSchoolLog managerRenewSchoolLog);
}
