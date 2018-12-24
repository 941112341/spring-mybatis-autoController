package com.dao;

import com.model.User;
import com.model.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

@RestController
public interface UserMapper {


    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer userId);

    @PostMapping
    int insert(@RequestBody User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    @RequestMapping("find")
    User selectByPrimaryKey(@RequestParam("id") Integer userId);

    @RequestMapping("select")
    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}