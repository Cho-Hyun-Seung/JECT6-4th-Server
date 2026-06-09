package com.ject6.boost.domain.user.domain.entity;

import com.ject6.boost.domain.user.domain.constant.BlogPlatform;
import com.ject6.boost.domain.user.domain.constant.BlogStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name = "user_blogs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "blog_url", nullable = false, length = 255)
    private String blogUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 50)
    private BlogPlatform platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private BlogStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public static UserBlog create(User user, String blogUrl, BlogPlatform platform) {
        UserBlog blog = new UserBlog();
        blog.user = user;
        blog.blogUrl = blogUrl;
        blog.platform = platform;
        blog.status = BlogStatus.ACTIVE;
        return blog;
    }

    public void update(String blogUrl) {
        this.blogUrl = blogUrl;
        this.status = BlogStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = BlogStatus.INACTIVE;
    }
}
