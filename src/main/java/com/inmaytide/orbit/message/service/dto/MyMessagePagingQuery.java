package com.inmaytide.orbit.message.service.dto;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.message.domain.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author inmaytide
 * @since 2024/3/13
 */
@Schema(title = "我的消息分页查询")
public class MyMessagePagingQuery extends Pageable<Message> {

    @Schema(title = "按创建时间查询-时间范围开始时间")
    private Instant start;

    @Schema(title = "按创建时间查询-时间范围结束时间")
    private Instant end;

    @Schema(title = "消息业务类型", description = "业务编码, 多个逗号隔开")
    private String businesses;

    @Schema(title = "阅读状态")
    private String readingStatuses;

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public List<String> getBusinesses() {
        if (StringUtils.isBlank(businesses)) {
            return Collections.emptyList();
        }
        return CommonUtils.splitByCommas(businesses);
    }

    public void setBusinesses(String businesses) {
        this.businesses = businesses;
    }

    public List<String> getReadingStatuses() {
        if (StringUtils.isBlank(readingStatuses)) {
            return Collections.emptyList();
        }
        return CommonUtils.splitByCommas(readingStatuses);
    }

    public void setReadingStatuses(String readingStatuses) {
        this.readingStatuses = readingStatuses;
    }

    @Override
    public LambdaQueryWrapper<Message> toWrapper() {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.isNotBlank(getQueryName()), w -> w.like(Message::getTitle, getQueryName()).or().like(Message::getContent, getQueryName()));
        wrapper.ge(getStart() != null, Message::getCreatedTime, getStart());
        wrapper.le(getEnd() != null, Message::getCreatedTime, getEnd());
        wrapper.in(CollectionUtils.isNotEmpty(getBusinesses()), Message::getBusiness, getBusinesses());
        addSubQuery(wrapper);
        return wrapper;
    }

    private void addSubQuery(LambdaQueryWrapper<Message> wrapper) {
        String subSQL = "id in (select message_id from message_receiver where sending_mode = {0} and receiver = {1} %s)";
        List<Object> values = new ArrayList<>();
        values.add(MessageSendingMode.SYSTEM.name());
        values.add(SecurityUtils.getAuthorizedUserId());
        String condition = "";
        if (CollectionUtils.isNotEmpty(getReadingStatuses())) {
            String params = IntStream.range(2, getReadingStatuses().size() + 2).mapToObj("{%d}"::formatted).collect(Collectors.joining(","));
            condition = "and reading_status in (%s)".formatted(params);
            values.addAll(getReadingStatuses());
        }
        wrapper.apply(subSQL.formatted(condition), values.toArray());
    }

}
