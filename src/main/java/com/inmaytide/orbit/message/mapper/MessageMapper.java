package com.inmaytide.orbit.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.message.domain.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2024/2/29
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
