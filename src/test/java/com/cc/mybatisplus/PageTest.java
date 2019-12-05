package com.cc.mybatisplus;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class PageTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void pageTest(){

        Page<User> userPage = new Page<>(1, 4);
        IPage<User> iPage = userMapper.selectPage(userPage, new QueryWrapper<User>().gt("age", 20));
        System.out.println("总页数" + iPage.getPages());
        System.out.println("总记录数" + iPage.getTotal());
        List<User> userList = iPage.getRecords();
        userList.forEach(System.out::println);



    }
}
