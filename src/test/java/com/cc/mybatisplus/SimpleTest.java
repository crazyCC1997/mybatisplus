package com.cc.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cc.mybatisplus.dao.UserMapper;
import com.cc.mybatisplus.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void select(){
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }


    /**
     * 名字中有“国”并且年龄小于30    lt 小于
     */
    @Test
    public void selectByWrapper(){
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().like("name", "国").lt("age", "30"));
        for (User user : userList) {
            System.out.println(user);
        }
    }

    @Test
    public void selectByWrapper2(){
        List<User> users = userMapper.selectList(new QueryWrapper<User>()
                .like("name", "国")
                .between("age", 20, 40)
                .isNotNull("email")
        );
    }

    // name like '王%' or age>= 40 order by age desc,id asc
    @Test
    public void selectByWrapper3(){
        userMapper.selectList(new QueryWrapper<User>()
                .likeRight("name", "王")
                .or().ge("age", 40).orderByDesc("age").orderByAsc("id")
        );
    }

    //创建时间为。。。并且直属上级姓名为王姓
    // data_format(create_time,'%Y-%m-%d') and manager_id in (select id from user where name like '王%')
    // data_format 格式化日期
    @Test
    public void selectByWarpper4(){
        userMapper.selectList(new QueryWrapper<User>()
        .apply("data_format(create_time,'%Y-%m-%d')=0", "2018-05-14")
        .inSql("manager_id", "select id from user where name like '王%'"));
    }

    //name like '王%' and (age < 40 or email is not null)
    @Test
    public void selectByWarpper5(){
        userMapper.selectList(new QueryWrapper<User>()
        .likeRight("name", "王")
        .and(qw->qw.lt("age", 40).or().isNotNull("email")));
    }

    //王姓或者（age<40 and age > 20 and email is not null）
    @Test
    public void selectByWarpper6(){
        userMapper.selectList(new QueryWrapper<User>().likeRight("name", "王")
                .or(qw->qw.lt("age", 40).gt("age", 20).isNotNull("email")));
    }

    //(age < 40 or email is not null) and name like '王%'
    @Test
    public void selectByWarpper7(){
        userMapper.selectList(new QueryWrapper<User>()
                .nested(qw->qw.lt("age", 40).or().isNotNull("email")
                .likeRight("name", "王")));
    }

    // age in (30,31,32,33,34,35)
    @Test
    public void selectByWarpper8(){
        userMapper.selectList(new QueryWrapper<User>().in("age", 30,31,32,33,34,35));
    }

    //只返回满足条件的一条数据    limit 1
    @Test
    public void selectByWarpper9(){
        userMapper.selectList(new QueryWrapper<User>()
                .in("age", 30,31,32,33,34,35).last("limit 1"));
    }

    //查询王姓并且年龄小于40的人的id和name
    @Test
    public void selectByWrapperSupper1(){
        userMapper.selectList(new QueryWrapper<User>().select("id","name")
                .likeRight("name", "王").lt("age",40));
    }

    //查询王姓并且年龄小于40的人除了create_time和manager_id的其他全部字段
    @Test
    public void selectByWrapperSupper2(){
        userMapper.selectList(new QueryWrapper<User>().likeRight("name", "王")
            .lt("age", 40)
            .select(User.class, info->!info.getColumn().equals("create_time")&&
                    !info.getColumn().equals("manager_id")));
    }

    @Test
    //condition  like(boolean condition,column,val)  当boolean返回true时，该条件成立
    public void testConditon(String name, String email){
       userMapper.selectList(new QueryWrapper<User>().like(StringUtils.isNotEmpty(name),"name",name)
       .like(StringUtils.isNotEmpty(email),"email",email));
    }

    //实体作为条件构造器构造方法的参数  条件构造器构造方法中的参数与后面的条件互不干扰
    @Test
    public void selectByWrapperEntity(){
        User user = new User();
        user.setName("朱颖");
        user.setAge(32);

        userMapper.selectList(new QueryWrapper<User>(user)
                .like("name", "雨").lt("age", 32));
    }

    // AllEq
    @Test
    public void selectByWrapperAllEq(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "王天风");
        params.put("age", 25);
        userMapper.selectList(new QueryWrapper<User>().allEq(params));
    }

    //Maps返回键值对的形式  应用场景：指定输出字段可以舍掉那些key对应的value为空的字段
    @Test
    public void selectByWrapperMaps(){
        userMapper.selectMaps(new QueryWrapper<User>()
                .select("id","name","create_time").like("name","雨").lt("age", 25));
    }

    //按照直属上级分组，查询每组的平均年龄、最大年龄、最小年龄。并且只取年龄总和小于500的组
    // select avg(age) avg_age,min(age) min_age,max(age) max_age,
    // from user
    // group by manager_id
    // having sum(age) < 500
    @Test
    public void selectByWrapperMaps2(){
        userMapper.selectMaps(new QueryWrapper<User>()
                .select("avg(age) avg_age", "max(age) max_age", "min(age) min_age")
                .groupBy("manager_id").having("sum(age)<{0}", 500));
    }

    //selectObjs 虽然我查询了id和name两个字段 但是selectObjs只能输出第一个字段
    @Test
    public void selectByWrapperObjs(){
        userMapper.selectObjs(new QueryWrapper<User>().select("id","name")
                .like("name", "雨").lt("age", 25));
    }

    //selectCount 该方法不能指定查询的列表 会自动生成count(1)、count(2)...
    @Test
    public void selectByWrapperCount(){
        userMapper.selectCount(new QueryWrapper<User>()
                .like("name", "雨").lt("age", 25));
    }

    //selectOne   因为查询的是一条记录 所以返回的是T entity
    @Test
    public void selectByWrapperOne(){
        userMapper.selectOne(new QueryWrapper<User>()
                .like("name","王天风").lt("age", 40));
    }
}
