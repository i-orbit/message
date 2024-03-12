package com.inmaytide.orbit.message.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.domain.FileMeta;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/27
 */
@Schema(title = "系统消息")
public class Message extends TombstoneEntity {

    private String title;

    private String content;

    private String category;

    private String url;

    @TableField(exist = false)
    private List<FileMeta> attachments;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<FileMeta> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileMeta> attachments) {
        this.attachments = attachments;
    }
}
