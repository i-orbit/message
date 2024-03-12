package com.inmaytide.orbit.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.message.domain.MessageReceiver;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2024/3/4
 */
@Mapper
public interface MessageReceiverMapper extends BaseMapper<MessageReceiver> {
}
