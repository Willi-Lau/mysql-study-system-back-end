package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.TO.SchoolDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.dao.mybatis.LoginHistoryDao;
import com.lwy.demo.dao.mybatis.ManagerDao;
import com.lwy.demo.dao.mybatis.SchoolDao;
import com.lwy.demo.dao.mybatis.UserDao;
import com.lwy.demo.entity.School;
import com.lwy.demo.entity.User;
import com.lwy.demo.entity.UserLoginHistory;
import com.lwy.demo.service.ManagerService;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.TimeUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        resultDTO.setMap(new HashMap<>());
        resultDTO.getMap().put("userLoginHistory",userLoginHistoryList);
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
        //1649779200000 1649692800000
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("userLoginHistory",allUserLoginHistoryByDateList);
        resultDTO.setType(true);
        return resultDTO;
    }


}
