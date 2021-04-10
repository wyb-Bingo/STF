package com.ubin.stf.service;

import com.ubin.stf.mapper.AttendanceMapper;
import com.ubin.stf.model.*;
import com.ubin.stf.utils.Distance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AttendanceService {
    @Autowired
    DepartmentService departmentService;

    @Autowired
    UserService userService;

    @Autowired
    WeekClazzService weekClazzService;

    @Autowired
    AttendanceMapper attendanceMapper;

    @Autowired
    AdminService adminService;

    @Autowired
    RoleService roleService;

    @Autowired
    TeamService teamService;

    public List<AttendanceLeaderEntity> getAllDepartmentWithChildrenAndUser() {
        List<Department> allDepartmentWithChildren = departmentService.getAllDepartmentWithChildren();
        return recur(allDepartmentWithChildren);

    }

    public List<AttendanceLeaderEntity> recur(List<Department> allDepartmentWithChildren){
        if (allDepartmentWithChildren==null || allDepartmentWithChildren.size() == 0){
            return new ArrayList<>();
        }
        List<AttendanceLeaderEntity> result = new ArrayList<>();
        for (Department department:allDepartmentWithChildren){
            AttendanceLeaderEntity leaderEntity = new AttendanceLeaderEntity();
            leaderEntity.setLabel(department.getName());

            List<AttendanceLeaderEntity> recur = recur(department.getChildren());
            List<User> departmentIsUser = userService.getDepartmentIsUser(department.getId());
            if (departmentIsUser!=null && !departmentIsUser.isEmpty()){
                for (User user: departmentIsUser){
                    AttendanceLeaderEntity attendanceLeaderEntity = new AttendanceLeaderEntity();
                    attendanceLeaderEntity.setLabel(user.getName());
                    attendanceLeaderEntity.setChildren(null);
                    user.setRoleList(null);
                    user.setUserIsDepartmentUnderTeam(null);
                    attendanceLeaderEntity.setValue(user);
                    recur.add(attendanceLeaderEntity);
                }
            }


            leaderEntity.setChildren(recur);
            department.setChildren(null);
            department.setAdminList(null);
            leaderEntity.setValue(department);
            result.add(leaderEntity);
        }
        return result;
    }

    public List<AttendanceLeaderEntity> getAllDepartmentWithChildren() {
        List<Department> allDepartmentWithChildren = departmentService.getAllDepartmentWithChildren();
        return recur_recur(allDepartmentWithChildren);

    }
    private List<AttendanceLeaderEntity> recur_recur(List<Department> allDepartmentWithChildren){
        if (allDepartmentWithChildren==null || allDepartmentWithChildren.size() == 0){
            return new ArrayList<>();
        }
        List<AttendanceLeaderEntity> result = new ArrayList<>();
        for (Department department:allDepartmentWithChildren){
            AttendanceLeaderEntity leaderEntity = new AttendanceLeaderEntity();
            leaderEntity.setLabel(department.getName());
            leaderEntity.setChildren(recur_recur(department.getChildren()));
            department.setChildren(null);
            department.setAdminList(null);
            leaderEntity.setValue(department);
            result.add(leaderEntity);
        }
        return result;
    }

    //生成考勤组
    //先将weekClazz一条一条的插入，返回得到id，组成ids
    //再将leader中的id，抽出来，组成ids
    //再将departmens中的id，抽出来，组成ids
    //再获取userId和teamId
    //插入attendance表
    //再将leader中的user设置为考勤组负责人
    //将user插入admin表(判断是否存在)
    //维护admin_role表（查找当前团队考勤负责人的roleId）
    //维护team_admin表(设置user为当前团队的负责人)
    @Transactional(rollbackFor = Exception.class)
    public int createAttendanceWithColume(Attendance attendance){
        List<WeekClazz> weekClazz = attendance.getWeekClazz();
        List<User> leader = attendance.getLeader();
        List<Department> departments = attendance.getDepartments();
        if (weekClazz == null || weekClazz.size() == 0 ||
                leader == null ||leader.size() == 0 ||
                departments == null || departments.size() == 0){
            return 0;
        }

        List<Integer> weekClazzIds = weekClazzService.BatchInsertWeekClazz(weekClazz);
        List<Integer> leaderIds = new ArrayList<>();
        for (User user : leader){
            leaderIds.add(user.getId());
        }
        List<Integer> departmentIds = new ArrayList<>();
        for (Department department : departments){
            departmentIds.add(department.getId());
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (Integer integer : weekClazzIds){
            stringBuffer.append(integer);
            stringBuffer.append(".");
        }
        String weekClazzStr = stringBuffer.substring(0, stringBuffer.length() - 1);
        stringBuffer.delete(0,stringBuffer.length());
        for (Integer integer : leaderIds){
            stringBuffer.append(integer);
            stringBuffer.append(".");
        }
        String leaderStr = stringBuffer.substring(0,stringBuffer.length()-1);
        stringBuffer.delete(0,stringBuffer.length());
        for (Integer integer : departmentIds){
            stringBuffer.append(integer);
            stringBuffer.append(".");
        }
        String departmentStr = stringBuffer.substring(0,stringBuffer.length()-1);
        stringBuffer.delete(0,stringBuffer.length());

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer adminId = principal.getId();
        Integer teamId = principal.getTeamId();

        String roleName = "ROLE_"+teamId+"_ATTENDANCELEADER";
        int targetRoleIdUnderTeam = roleService.findTargetRoleIdUnderTeam(roleName, teamId);
        for (Integer integer : leaderIds){
            //同样要判断是否已经存在，因为可能已经在别的考勤组已经设置过考勤负责人
            roleService.insertRoleAdmin(targetRoleIdUnderTeam,integer);
            adminService.insertAdminIfNoExists(integer);
            teamService.insertTeamAdminIfNotExsit(teamId,integer);
        }


        return attendanceMapper.insertAttendance(attendance.getName(),weekClazzStr,leaderStr,departmentStr,adminId,teamId);


    }

    public List<Attendance> getAttendanceList() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer teamId = principal.getTeamId();

        List<Attendance> res = attendanceMapper.getAttendanceListByTeamId(teamId);

        for (Attendance attendance : res){
            //获取weekClazz
            List<WeekClazz> weekClazzList = new ArrayList<>();
            String weekClazzStr = attendance.getWeekClazzStr();
            String[] split = weekClazzStr.split("\\.");
            for (String s : split){
                WeekClazz weekClazz = weekClazzService.getWeekClazzById(Integer.valueOf(s));
                weekClazzList.add(weekClazz);
            }
            attendance.setWeekClazz(weekClazzList);

            //获取Leader
            List<User> leaderList = new ArrayList<>();
            String leaderStr = attendance.getLeaderStr();
            String[] split1 = leaderStr.split("\\.");
            for (String s : split1){
                User simpleUserInfoById = userService.getSimpleUserInfoById(Integer.valueOf(s));
                leaderList.add(simpleUserInfoById);
            }
            attendance.setLeader(leaderList);

            //获取department
            List<Department> departmentList = new ArrayList<>();
            String departmentStr = attendance.getDepartmentStr();
            String[] split2 = departmentStr.split("\\.");
            for (String s : split2){
                Department simpleDepartmentInfoById = departmentService.getSimpleDepartmentInfoById(Integer.valueOf(s));
                departmentList.add(simpleDepartmentInfoById);
            }
            attendance.setDepartments(departmentList);
        }
        return res;
    }

    public int setAttendanceOfEnabled(Boolean value,Integer id) {
        return attendanceMapper.setAttendanceOfEnabled(value,id);
    }

    /**
     * 查询符合条件的考勤组
     * 1. 符合相应团队
     * 2. enabled = 1
     * 3.user.department = attendance.department
     * 4. 今天考勤才显示 week = week
     * @return
     */
    public List<Attendance> showAttendance() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Department department = userService.getUserIsDepartment(principal.getId(),principal.getTeamId());

        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"};
        LocalDateTime dateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        int weekValue = dayOfWeek.getValue()-1;

        List<Attendance> attendanceList = getAttendanceList();
        List<Attendance> res = new ArrayList<>();
        for (Attendance attendance : attendanceList){
            if (!attendance.getEnabled()) continue;

            String departmentStr = attendance.getDepartmentStr();
            boolean contains = departmentStr.contains(String.valueOf(department.getId()));
            if (!contains) continue;

            List<WeekClazz> weekClazz = attendance.getWeekClazz();
            for (WeekClazz weekClazz1 : weekClazz){
                if (weekClazz1.getWeek().equals(weekDays[weekValue])){
                    attendance.setToday(weekClazz1);
                    res.add(attendance);
                    break;
                }
            }
        }
        return res;
    }

    public int updateAddressOfAttendance(String address,Integer id) {
        return attendanceMapper.updateAddress(address,id);

    }

    /**
     * 获取对应管理员的管理的考勤组
     * 1. admin = userId
     * 2. 是今天考勤才显示
     * 3. enabled = 1
     * 4. 符合团队
     * @return
     */
    public List<Attendance> getAttendanceListOfAdmin() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer adminId = principal.getId();

        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"};
        LocalDateTime dateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        int weekValue = dayOfWeek.getValue()-1;

        List<Attendance> attendanceList = getAttendanceList();
        List<Attendance> res = new ArrayList<>();
        for (Attendance attendance : attendanceList){
            if (!attendance.getEnabled()) continue;

            if (!attendance.getAdmin().equals(adminId)) continue;

            List<WeekClazz> weekClazz = attendance.getWeekClazz();
            for (WeekClazz weekClazz1 : weekClazz){
                if (weekClazz1.getWeek().equals(weekDays[weekValue])){
                    attendance.setToday(weekClazz1);
                    res.add(attendance);
                    break;
                }
            }
        }
        return res;
    }

    /**
     * 根据id和userId去attendance_user表查看是否已经签到（还需根据日期，认准是今天签到）
     * 如果已经签到，则返回SUCCESS
     * 如果没有签到，则开始判断当前时间是否在time From-timeTo之中，若是则返回waiting,否则返回info或warn
     * 根据id获取对应考勤组的信息（today(week,clazz(timeFrom,timeTo))）
     * @param id
     * @return
     */
    public AttendanceStatusEnum getUserIsAttendanceStatus(Integer id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDate now = LocalDate.now();
        String dataStr = now.toString();
        int num = attendanceMapper.getAttendanceToUserByIdAndData(id,principal.getId(),dataStr);
        if (num == 1){
            return AttendanceStatusEnum.SUCCESS;
        }

        Attendance attendanceInfoById = getAttendanceInfoById(id);
        WeekClazz today = attendanceInfoById.getToday();
        LocalTime time = LocalTime.now();
        LocalTime timeFrom = LocalTime.parse(today.getClazz().getTimeFrom());
        LocalTime timeTo = LocalTime.parse(today.getClazz().getTimeTo());
        if (time.isBefore(timeFrom)){
            return AttendanceStatusEnum.INFO;
        }
        else if (time.isAfter(timeTo)){
            return AttendanceStatusEnum.WARN;
        }else {
            return AttendanceStatusEnum.WAITING;
        }
    }

    /**
     * 根据id去查询对应attendance的完整信息
     * @param id
     * @return
     */
    public Attendance getAttendanceInfoById(Integer id){
        Attendance attendance = attendanceMapper.getAttendanceInfoById(id);
        return setAttendanceIsListInfo(attendance);
    }
    public Attendance setAttendanceIsListInfo(Attendance attendance){
        List<WeekClazz> weekClazzList = new ArrayList<>();
        String weekClazzStr = attendance.getWeekClazzStr();
        String[] split = weekClazzStr.split("\\.");
        for (String s : split){
            WeekClazz weekClazz = weekClazzService.getWeekClazzById(Integer.valueOf(s));
            weekClazzList.add(weekClazz);
        }
        attendance.setWeekClazz(weekClazzList);

        //获取Leader
        List<User> leaderList = new ArrayList<>();
        String leaderStr = attendance.getLeaderStr();
        String[] split1 = leaderStr.split("\\.");
        for (String s : split1){
            User simpleUserInfoById = userService.getSimpleUserInfoById(Integer.valueOf(s));
            leaderList.add(simpleUserInfoById);
        }
        attendance.setLeader(leaderList);

        //获取department
        List<Department> departmentList = new ArrayList<>();
        String departmentStr = attendance.getDepartmentStr();
        String[] split2 = departmentStr.split("\\.");
        for (String s : split2){
            Department simpleDepartmentInfoById = departmentService.getSimpleDepartmentInfoById(Integer.valueOf(s));
            departmentList.add(simpleDepartmentInfoById);
        }
        attendance.setDepartments(departmentList);

        //获取today
        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"};
        LocalDateTime dateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        int weekValue = dayOfWeek.getValue()-1;

        List<WeekClazz> weekClazz = attendance.getWeekClazz();
        for (WeekClazz weekClazz1 : weekClazz){
            if (weekClazz1.getWeek().equals(weekDays[weekValue])){
                attendance.setToday(weekClazz1);
                break;
            }
        }
        return attendance;
    }

    /**
     * getUserIsAttendanceStatus（id）,根据方法返回信息，判断是否为waiting，如果是则可以签到，否则返回“时间不符合规则（未到或超时）”
     * 根据attendacneId获取对应attendance信息
     * 获取attendance的address，将address分成经度/纬度
     * 根据用户的经度和纬度，计算两者之间的距离，若距离满足，则进行签到，然后返回“签到成功”，若距离不满足，则返回“相距考勤地点xxx米，不符合规则（30米）”
     * @param userLocaltion
     * @return
     */
    public String insertAttendanceClickInfo(UserLocaltion userLocaltion) {
        AttendanceStatusEnum userIsAttendanceStatus = getUserIsAttendanceStatus(userLocaltion.getAttendanceId());
        if (!userIsAttendanceStatus.equals(AttendanceStatusEnum.WAITING)){
            return "时间不符合规则（未到或超时）";
        }
        Attendance attendanceInfoById = getAttendanceInfoById(userLocaltion.getAttendanceId());
        String address  = attendanceInfoById.getAddress();
        if (address == null || address.length() == 0){
            return "考勤组管理员还未设置签到地点";
        }
        String[] split = address.split("-");
        //纬度
        String latitude = split[0];
        //经度
        String longitude = split[1];
        //
        String latitude1 = userLocaltion.getLatitude();
        String longitude1 = userLocaltion.getLongitude();
        boolean aTrue = Distance.isTrue(Double.parseDouble(longitude),Double.parseDouble(latitude) ,Double.parseDouble(longitude1) ,Double.parseDouble(latitude1) , "30");
        if (aTrue){
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            attendanceMapper.insertAttendanceUser(userLocaltion.getAttendanceId(),principal.getId(),LocalDate.now(),LocalTime.now(),0);
            return "签到成功";
        }
        return "相距考勤地点"+ Distance.getDistance(Double.parseDouble(longitude),Double.parseDouble(latitude) ,Double.parseDouble(longitude1) ,Double.parseDouble(latitude1) ) +"米，不符合规则（30米）";


    }

    /**
     * 根据当前日期和考勤组ID获取考勤信息
     * 首先根据id获取对应的考勤组（name,weekClazz,leader,dep,admin）
     * 将date格式化，获取对应日期的星期
     * 遍历weekClazz，查看是否当前考勤组在当前日期是否需要考勤
     * 若是，则下一步，若不是，则将name = name，直接返回
     * 将timeFrom，timeTo赋值
     * 接下来根据dep，获取所有的用户信息user
     * 根据ID，date，获取对应考勤组的考勤数据
     * 将上两步的userList进行匹配，若存在，则加入ture，不存在，则加入false
     * @param date
     * @param id
     * @return
     */
    public AttendanceData getCurrentDateAttendanceData(String date, Integer id) {
        AttendanceData attendanceData = new AttendanceData();
        FormInline formInline = new FormInline();
        ArrayList<StatisticsData> trueData = new ArrayList<>();
        ArrayList<StatisticsData> falseData = new ArrayList<>();

        Attendance attendanceInfoById = getAttendanceInfoById(id);

        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"};
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate parse = LocalDate.parse(date);
        DayOfWeek dayOfWeek = parse.getDayOfWeek();
        int weekValue = dayOfWeek.getValue()-1;

        List<WeekClazz> weekClazz = attendanceInfoById.getWeekClazz();
        boolean flag = false;
        for (WeekClazz weekClazz1 : weekClazz){
            if (weekClazz1.getWeek().equals(weekDays[weekValue])){
                flag = true;
                formInline.setTimeFrom(weekClazz1.getClazz().getTimeFrom());
                formInline.setTimeTo(weekClazz1.getClazz().getTimeTo());
                break;
            }
        }
        formInline.setName(attendanceInfoById.getName());
        attendanceData.setForm(formInline);
        if (!flag){
            return attendanceData;
        }

        String departmentStr = attendanceInfoById.getDepartmentStr();
        String[] depSplit = departmentStr.split("\\.");
        Integer[] depIds = new Integer[depSplit.length];
       for (int i=0 ;i<depSplit.length ;i++){
           depIds[i] = Integer.valueOf(depSplit[i]);
       }
        List<User> departmentListIsUser = userService.getDepartmentListIsUser(depIds);

        List<AttendanceUser> attendanceWithUserByIdAndData = attendanceMapper.getAttendanceWithUserByIdAndData(id, date);

        for (User user:departmentListIsUser){
           // System.out.println(user.toString());
            boolean item = false;
            StatisticsData statisticsData = new StatisticsData();
            statisticsData.setName(user.getName());
            statisticsData.setDep(user.getUserIsDepartmentUnderTeam().getName());
            statisticsData.setStudentNumber(user.getStudentNumber());
            for (AttendanceUser attendanceUser:attendanceWithUserByIdAndData){
                if (user.getId().equals(attendanceUser.getUserId())){
                    item = true;
                    statisticsData.setDate(attendanceUser.getDate());
                    statisticsData.setTime(attendanceUser.getTime());
                    break;
                }
            }
            if (item){
                trueData.add(statisticsData);
            }else {
                falseData.add(statisticsData);
            }
        }

        attendanceData.setTrueData(trueData.toArray(new StatisticsData[trueData.size()]));
        attendanceData.setFalseData(falseData.toArray(new StatisticsData[falseData.size()]));

        return attendanceData;



    }

    /**
     *  获取当前用户当前考勤组的整个月的考勤数据
     *  获取userId，根据id，date（转换成dateFrom，dateTo），查询attendance_user记录
     *  根据id 获取attendance的信息
     *  遍历1-31号，判断当前日期是否需要考勤
     *  若需要考勤，则判断当前日期是否在attendance_user记录中
     * @param id
     * @param date
     * @return
     */
    public List<UserAttendanceData> getUserCurrentAttendanceData(Integer id, String date) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = principal.getId();

        String dateFrom = date + "-" + "01";
        String dateTo = date + "-" + "31";

        List<AttendanceUser> attendanceUsers =  attendanceMapper.getAttendaceToUserByIdAndFromToDate(userId,id,dateFrom,dateTo);
        HashSet<String> checkSet = new HashSet<>();
        for (AttendanceUser attendanceUser : attendanceUsers){
            checkSet.add(attendanceUser.getDate());
        }

        Attendance attendanceInfoById = getAttendanceInfoById(id);
        List<WeekClazz> weekClazz = attendanceInfoById.getWeekClazz();
        HashSet<String> weekSet = new HashSet<>();
        for (WeekClazz week : weekClazz){
            weekSet.add(week.getWeek());
        }

        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"};

        List<UserAttendanceData> res = new ArrayList<>();

        for (int i=1;i<=31;i++){
            String index = date ;
            if (i<=9){
                index = index + "-0" + i;
            }else {
                index = index + "-" + i;
            }

            LocalDate parse = null;
            try {
                parse = LocalDate.parse(index);
            }catch (DateTimeException exception){
                break;
            }

            DayOfWeek dayOfWeek = parse.getDayOfWeek();
            int weekValue = dayOfWeek.getValue()-1;
            String weekDay = weekDays[weekValue];

            if (weekSet.contains(weekDay)){
                //需要考勤
                UserAttendanceData userAttendanceData = new UserAttendanceData();
                userAttendanceData.setDate(index);
                if (checkSet.contains(index)){
                    //成功
                    userAttendanceData.setCheck(true);
                    res.add(userAttendanceData);
                }else {
                    userAttendanceData.setCheck(false);
                    res.add(userAttendanceData);
                }
            }
        }

        return  res;
    }
}
