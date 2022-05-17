package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.TO.SchoolDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.elasticsearch.ESSchoolDao;
import com.lwy.demo.dao.mybatis.*;
import com.lwy.demo.entity.*;
import com.lwy.demo.service.ManagerService;
import com.lwy.demo.utils.ElasticSearchSchoolUtils;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.TimeUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SchoolDao schoolDao;

    @Autowired
    private LoginHistoryDao loginHistoryDao;

    @Autowired
    private ManagerRenewSchoolLogDao managerRenewSchoolLogDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ElasticSearchSchoolUtils elasticSearchSchoolUtils;

    @Autowired
    private AllTestDiscribeDao allTestDiscribeDao;

    @Autowired
    private TableColumnAttributeDao tableColumnAttributeDao;

    /**
     * 获取所有的用户列表
     *
     * @return
     */
    @Override
    public ResultDTO getUserList() {
        ResultDTO resultDTO = new ResultDTO();
        HashMap<Object, Object> map = new HashMap<>();
        List<User> userList = userDao.getUserList();
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : userList) {
            UserDTO userDTO = UserDTO.userTransformUserDTO(user);
            userDTOList.add(userDTO);
        }
        map.put("userList", userDTOList);
        resultDTO.setMap(map);
        return resultDTO;
    }

    /**
     * 修改用户状态
     *
     * @param id
     * @param state
     */
    @Override
    public void changeState(Integer id, Boolean state) {
        Integer state1 = 0;
        if (state) {
            state1 = 1;
        }
        HashMap map = new HashMap(16);
        map.put("state", state1);
        map.put("id", id);
        //修改mysql里的状态
        userDao.changeState(map);
    }

    @Override
    public ResultDTO getSchoolList() throws ParseException {
        List<School> schoolList = schoolDao.getSchoolList();
        List<SchoolDTO> schoolDTOList = new ArrayList<>();
        for (School school : schoolList) {
            //没过期添加
           // if (Long.parseLong(school.getDeadline()) > System.currentTimeMillis()) {
                schoolDTOList.add(SchoolDTO.schoolTransformSchoolDTO(school));
          //  }
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("schoolList", schoolDTOList);
        return resultDTO;
    }

    @Override
    public ResultDTO getSchoolListTimeOut() throws ParseException {
        List<School> schoolList = schoolDao.getSchoolList();
        List<SchoolDTO> schoolDTOList = new ArrayList<>();
        for (School school : schoolList) {
            //没过期添加
            if (Long.parseLong(school.getDeadline()) > System.currentTimeMillis()) {
                schoolDTOList.add(SchoolDTO.schoolTransformSchoolDTO(school));
            }
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("schoolList", schoolDTOList);
        return resultDTO;
    }

    @Override
    public ResultDTO getConditionStudent(HashMap<String, String> map) {
        ResultDTO resultDTO = new ResultDTO();
        try {
            HashMap<String, Object> conditionMap = new HashMap<>(16);
            conditionMap.put("idOptionsValue", map.get("idOptionsValue"));
            //是否有精确查找 指 1,2,3 有则封装到idPreciseList
            conditionMap.put("idPreciseFlag", "no");
            //name部分
            conditionMap.put("nameOptionsValue", map.get("nameOptionsValue"));
            conditionMap.put("studentNumber", map.get("studentNumber"));
            conditionMap.put("identityCardNumber", map.get("identityCardNumber"));
            conditionMap.put("phone", map.get("phone"));
            conditionMap.put("university", map.get("university"));
            conditionMap.put("state", Integer.parseInt(map.get("state")));
            //解析id精确查找   1,2,3,4,5-7,9
            if (map.get("idOptionsValue").equals("精确查询")) {
                if (map.get("id").trim().length() == 0) {
                    resultDTO.setObject("id");
                    resultDTO.setType(false);
                    return resultDTO;
                }
                List<Integer> idList = new ArrayList<>();
                List<Map<Integer, Integer>> idSpanList = new ArrayList<>();
                String paramId = map.get("id");
                String[] idArray = paramId.split(",");

                boolean isAll = false;
                //判断是否包括范围 5-10这种  如果是 5-n 代表查询 >5 的所有 如果是 n - 5代表查询比5小的所有 如果是 n-n 则为所有
                for (String iteratorId : idArray) {
                    //是范围的条件
                    if (iteratorId.contains("-")) {
                        conditionMap.put("idPreciseFlag", "yes");
                        conditionMap.put("idSpanFlag", "yes");
                        //解析具体的范围
                        String[] idSpanArray = iteratorId.split("-");
                        String start = idSpanArray[0];
                        String end = idSpanArray[1];
                        //如果要查询全部则不进行任何查询
                        if (start.equals("n") && end.equals("n")) {
                            isAll = true;
                        }
                        //查询一共多少条数据
                        Integer maxId = userDao.getMaxId();
                        if ((!start.equals("n")) && (!end.equals("n"))) {
                            if (Integer.parseInt(start) > Integer.parseInt(end) || Integer.parseInt(end) > maxId || Integer.parseInt(start) < 0) {
                                resultDTO.setObject("id");
                                resultDTO.setType(false);
                                return resultDTO;
                            }
                        }
                        for (int i = (start.equals("n") ? 0 : Integer.parseInt(start)); i <= (end.equals("n") ? maxId : Integer.parseInt(end)); i++) {
                            idList.add(i);
                        }
                    } else {
                        conditionMap.put("idPreciseFlag", "yes");
                        idList.add(Integer.parseInt(iteratorId));
                    }
                }
                //包含全部 不进行任何查询
                if (isAll) {

                    conditionMap.put("idPreciseFlag", "no");
                }
                //id 封装条件 使用查询in()
                conditionMap.put("idPreciseList", idList);
            }
            //模糊查询
            else if (map.get("idOptionsValue").equals("模糊查询")) {
                if (map.get("id").trim().length() == 0) {
                    resultDTO.setObject("id");
                    resultDTO.setType(false);
                    return resultDTO;
                }
                conditionMap.put("idIndistinct", "%" + map.get("id") + "%");
            }
            //解析name部分
            if (map.get("nameOptionsValue").equals("精确查询")) {
                if (map.get("name").trim().length() == 0) {
                    resultDTO.setObject("name");
                    resultDTO.setType(false);
                    return resultDTO;
                }
                String paramName = map.get("name");
                String[] nameArr = paramName.split(",");
                conditionMap.put("namePreciseList", nameArr);
            } else if (map.get("nameOptionsValue").equals("模糊查询")) {
                if (map.get("name").trim().length() == 0) {
                    resultDTO.setObject("name");
                    resultDTO.setType(false);
                    return resultDTO;
                }
                conditionMap.put("nameIndistinct", "%" + map.get("name") + "%");
            }
            //解析身份证号
            String identityCardNumber = map.get("identityCardNumber");
            String[] identityCardNumberArr = identityCardNumber.split(",");
            conditionMap.put("identityCardNumberList", identityCardNumberArr);
            //解析学号
            String studentNumber = map.get("studentNumber");
            String[] studentNumberArr = studentNumber.split(",");
            conditionMap.put("studentNumberList", studentNumberArr);
            //解析手机号
            String phone = map.get("phone");
            String[] phoneArr = phone.split(",");
            conditionMap.put("phoneList", phoneArr);
            List<User> userByCondition = userByCondition = userDao.getUserByCondition(conditionMap);
            List<UserDTO> userDTOList = new ArrayList<>();
            for (User user : userByCondition) {
                UserDTO userDTO = UserDTO.userTransformUserDTO(user);
                userDTOList.add(userDTO);
            }
            resultDTO.setMap(new HashMap<>(16));
            resultDTO.getMap().put("userByConditionList", userDTOList);
            resultDTO.setType(true);
        } catch (Exception e) {
            resultDTO.setType(false);
            e.printStackTrace();
        } finally {
            return resultDTO;
        }

    }

    @Override
    public ResultDTO getUserLoginHistory(Integer id) throws ParseException {
        ResultDTO resultDTO = new ResultDTO();
        if(id == 0){
            resultDTO.setType(false);
            return resultDTO;
        }
        List<UserLoginHistory> userLoginHistoryList = loginHistoryDao.getUserLoginHistory(id);
        for (UserLoginHistory userLoginHistory : userLoginHistoryList){
            userLoginHistory.setLoginTime(TimeUtils.changeTimeLongToString(Long.parseLong(userLoginHistory.getLoginTime())));
        }

        resultDTO.setType(true);
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("userLoginHistory",userLoginHistoryList);
        return resultDTO;
    }

    @Override
    public ResultDTO getUserLoginHistoryByDate(Integer id, String startTime, String endTime) throws ParseException {
        ResultDTO resultDTO = new ResultDTO();
        String startTimeLong = String.valueOf(TimeUtils.changeTimeStringToLongDate(startTime) + 86400000L);
        String endTimeLong = String.valueOf(TimeUtils.changeTimeStringToLongDate(endTime) + 86400000L * 2);
        HashMap<String,Object> map = new HashMap<>(16);
        map.put("startTime",startTimeLong);
        map.put("endTime",endTimeLong);
        map.put("id",id);
        List<UserLoginHistory> userLoginHistoryByDate = loginHistoryDao.getUserLoginHistoryByDate(map);
        for (UserLoginHistory userLoginHistory : userLoginHistoryByDate){
            userLoginHistory.setLoginTime(TimeUtils.changeTimeLongToString(Long.parseLong(userLoginHistory.getLoginTime())));
        }
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("userLoginHistory",userLoginHistoryByDate);
        resultDTO.setType(true);
        return resultDTO;
    }

    @Override
    public ResultDTO getAllUserLoginHistory() throws ParseException {
        ResultDTO resultDTO = new ResultDTO();
        List<UserLoginHistory> allUserLoginHistoryList = loginHistoryDao.getAllUserLoginHistory();
        resultDTO.setMap(new HashMap<>(16));
        if(allUserLoginHistoryList.size() > 0){
            for (UserLoginHistory userLoginHistory : allUserLoginHistoryList){
                userLoginHistory.setLoginTime(TimeUtils.changeTimeLongToString(Long.parseLong(userLoginHistory.getLoginTime())));
            }
            resultDTO.getMap().put("userLoginHistory",allUserLoginHistoryList);
            resultDTO.setType(true);
        }
        else {
            resultDTO.getMap().put("userLoginHistory",new ArrayList<>());
        }

        return resultDTO;
    }

    @Override
    public ResultDTO getAllUserLoginHistoryByDate(String startTime,String endTime) throws ParseException {
        ResultDTO resultDTO = new ResultDTO();
        String startTimeLong = String.valueOf(TimeUtils.changeTimeStringToLongDate(startTime) + 86400000L);
        String endTimeLong = String.valueOf(TimeUtils.changeTimeStringToLongDate(endTime) + 86400000L * 2);
        HashMap<String,String> map = new HashMap<>(16);
        map.put("startTime",startTimeLong);
        map.put("endTime",endTimeLong);
        List<UserLoginHistory> allUserLoginHistoryByDateList = loginHistoryDao.getAllUserLoginHistoryByDate(map);
        for (UserLoginHistory userLoginHistory : allUserLoginHistoryByDateList){
            userLoginHistory.setLoginTime(TimeUtils.changeTimeLongToString(Long.parseLong(userLoginHistory.getLoginTime())));
        }
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("userLoginHistory",allUserLoginHistoryByDateList);
        resultDTO.setType(true);
        return resultDTO;
    }

    @Override
    public ResultDTO renewSchool(String token,Integer schoolId, String renewDuration) {
        //读取指定schoolId的到期时间
        School school = schoolDao.getSchool(schoolId);
        long time = Long.parseLong(school.getDeadline());
        //修改学校过期时间 转为天数
        Integer renewDurationDate = Integer.parseInt(renewDuration);
        Long renewDurationLong = (long) renewDurationDate * 1000 * 60 * 60 * 24;
        HashMap<String,Object> map = new HashMap<>(16);
        map.put("renewDuration",time + renewDurationLong);
        map.put("id",schoolId);
        schoolDao.renewSchool(map);
        //插入到学校过期日志中
        Integer managerId = (Integer) redisUtil.get(InfoConfig.REDIS_TOKEN_MANAGER_LOGIN_INFO + token);
        ManagerRenewSchoolLog managerRenewSchoolLog = new ManagerRenewSchoolLog();
        managerRenewSchoolLog.setManagerId(managerId);
        managerRenewSchoolLog.setSchoolId(schoolId);
        managerRenewSchoolLog.setRenewDurationDay(renewDurationDate);
        managerRenewSchoolLog.setCreateTime(String.valueOf(System.currentTimeMillis()));
        managerRenewSchoolLogDao.insertManagerRenewSchoolLog(managerRenewSchoolLog);
        //返回
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setType(true);
        return resultDTO;
    }

    @Override
    public ResultDTO insertSchool(String name ,String deadline) throws ParseException {
        ResultDTO resultDTO = new ResultDTO();
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(deadline)){
            resultDTO.setType(false);
            resultDTO.setObject("params empty");
            return resultDTO;
        }
        //查询所有的学校名字查看是否注册过
        List<School> schoolList = schoolDao.getSchoolList();
        for (School sqlSchool : schoolList){
            if(sqlSchool.getName().equals(name)){
                resultDTO.setType(false);
                resultDTO.setObject("include school");
                return resultDTO;
            }
        }

        School school = new School();
        school.setName(name);
        school.setCreateTime(String.valueOf(System.currentTimeMillis()));
        school.setDeadline(String.valueOf(TimeUtils.changeTimeStringToLongDate(deadline.substring(0,10) + " "+deadline.substring(11,19))));
        //存入mysql
        schoolDao.insertSchool(school);
        resultDTO.setType(true);
        return resultDTO;
    }

    @Override
    public ResultDTO getSchoolByName(String name) throws Exception {
        ResultDTO resultDTO = new ResultDTO();
        if(StringUtils.isEmpty(name)){
            resultDTO.setType(false);
            return resultDTO;
        }
        List<School> esNameList = elasticSearchSchoolUtils.getByName(name);
        //修改时间单位
        for(School school : esNameList){
            school.setDeadline(TimeUtils.changeTimeLongToString(Long.parseLong(school.getDeadline())));
            school.setCreateTime(TimeUtils.changeTimeLongToString(Long.parseLong(school.getCreateTime())));
        }
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("schoolList",esNameList);
        resultDTO.setType(true);
        return resultDTO;
    }

    @Override
    public ResultDTO getSchoolRenewHistory(Integer schoolId) throws Exception {
        //根据学校id查找续费历史
        List<ManagerRenewSchoolLog> renewHistoryList = managerRenewSchoolLogDao.getRenewHistoryBySchoolId(schoolId);
        for (ManagerRenewSchoolLog managerRenewSchoolLog : renewHistoryList){
            managerRenewSchoolLog.setCreateTime(TimeUtils.changeTimeLongToString(Long.parseLong(managerRenewSchoolLog.getCreateTime())));
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setType(true);
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("renewHistoryList",renewHistoryList);
        return resultDTO;
    }

    @Override
    public ResultDTO getAllTest() {
        List<AllTestDiscribe> all = allTestDiscribeDao.getAll();
        ResultDTO resultDTO = new ResultDTO();
        HashMap map = new HashMap(16);
        map.put("result",all);
        resultDTO.setMap(map);

        return resultDTO;
    }

    @Override
    public ResultDTO getAllTableColumnAttribute() {
        List<TableColumnAttribute> tableColumnAttributeDaoAll = tableColumnAttributeDao.getAll();
        ResultDTO resultDTO = new ResultDTO();
        HashMap map = new HashMap(16);
        map.put("result",tableColumnAttributeDaoAll);
        resultDTO.setMap(map);
        return resultDTO;
    }

    @Override
    public void updateTest(Integer id,String titleDiscribe,String tableName,String trueSQL) {
        //根据表名获取表字段信息
        TableColumnAttribute tableColumnAttribute = tableColumnAttributeDao.get(tableName);
        AllTestDiscribe allTestDiscribe = new AllTestDiscribe();
        allTestDiscribe.setId(id);
        allTestDiscribe.setTitleDiscribe(titleDiscribe);
        allTestDiscribe.setTableName(tableName);
        allTestDiscribe.setTableColumn(tableColumnAttribute.getTableColumn());
        allTestDiscribe.setTrueSQL(trueSQL);
        allTestDiscribeDao.update(allTestDiscribe);
    }

    @Override
    public void insertTest(String titleDiscribe, String tableName, String trueSQL) {
        TableColumnAttribute tableColumnAttribute = tableColumnAttributeDao.get(tableName);
        AllTestDiscribe allTestDiscribe = new AllTestDiscribe();
        allTestDiscribe.setTitleDiscribe(titleDiscribe);
        allTestDiscribe.setTableName(tableName);
        allTestDiscribe.setTableColumn(tableColumnAttribute.getTableColumn());
        allTestDiscribe.setTrueSQL(trueSQL);
        allTestDiscribeDao.insert(allTestDiscribe);
    }

    @Override
    public void deleteTest(Integer id) {
        allTestDiscribeDao.delete(id);
    }

    @Override
    public ResultDTO selectByCondition(Integer id, String describe, String table) {
        HashMap map = new HashMap();
        map.put("discribe","%"+describe+"%");
        map.put("id",id);
        map.put("table",table);
        List<AllTestDiscribe> allTestDiscribes = allTestDiscribeDao.selectByCondition(map);
        ResultDTO resultDTO = new ResultDTO();
        HashMap map2 = new HashMap(16);
        map2.put("result",allTestDiscribes);
        resultDTO.setMap(map2);
        return resultDTO;
    }


}
