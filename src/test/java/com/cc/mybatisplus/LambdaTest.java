package com.cc.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.cc.mybatisplus.dao.UserMapper;
import com.cc.mybatisplus.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
/**
 *   lambda条件构造器 相比于 其他构造器 具有防误写的作用
 *   例如： 数据库中的字段 name 查询时被写成了mame 运行时才会报错
 *          而lambda条件构造器 User::getMame 编写时就会报错
 */
public class LambdaTest {

    @Autowired
    private UserMapper userMapper;

    //创建lambda条件构造器的三种方式
    @Test
    public void selectLambda(){
//        new QueryWrapper<User>().lambda();
//        new LambdaQueryWrapper<User>();
//        Wrappers.<User>lambdaQuery();

//        LambdaQueryWrapper<User> lambdaQuery = Wrappers.<User>lambdaQuery();
//        // 相当于  where name like '%雨%' and age < 40
//        lambdaQuery.like(User::getName,"雨").lt(User::getAge, 40);
//        userMapper.selectList(lambdaQuery);

        userMapper.selectList(new LambdaQueryWrapper<User>()
            .like(User::getName, "雨").lt(User::getAge, 40));
    }

    // 王姓并且（年龄小于40或者邮箱不为空）
    // name like '王%' and(age < 40 or email is not null)
    @Test
    public void selectLambda2(){
        userMapper.selectList(new LambdaQueryWrapper<User>()
                .likeRight(User::getName, "王")
                .and(lqw->lqw.lt(User::getAge, 40).or().isNotNull(User::getEmail)));
    }

    //lambda新增用法  LambdaQueryChainWrapper
    @Test
    public void selectByLambdaQueryChainWrapper(){
        List<User> userList = new LambdaQueryChainWrapper<User>(userMapper)
                .like(User::getName, "雨").ge(User::getAge, 20).list();
    }
}
