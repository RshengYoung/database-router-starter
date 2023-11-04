package com.rshenghub.db.entity.embedded;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Author {

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "modify_user_id")
    private String modifyUserId;

    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

}
