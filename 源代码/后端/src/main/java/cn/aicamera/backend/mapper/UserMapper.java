package cn.aicamera.backend.mapper;

import cn.aicamera.backend.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    /**
     * 插入用户
     *
     * @param user 用户对象
     */
    void insert(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     */
    void update(User user);

    /**
     * 根据用户名查找用户
     *
     * @param email 用户email
     * @return 用户对象
     */
    User findByEmail(String email);
}
