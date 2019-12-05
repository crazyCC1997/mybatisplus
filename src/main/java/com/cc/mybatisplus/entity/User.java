package com.cc.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    private Long id;

    private String name;

    private Integer age;

    private String email;

    private Long manager_id;

    private LocalDateTime create_time;
}
