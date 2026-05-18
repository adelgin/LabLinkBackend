-- Группы и участие
CREATE TABLE groups (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        owner_id BIGINT NOT NULL,
                        avatar_url VARCHAR(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_group_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE group_members (
                               group_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               role VARCHAR(50) DEFAULT 'MEMBER', -- OWNER, ADMIN, MEMBER
                               joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (group_id, user_id),
                               CONSTRAINT fk_gm_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                               CONSTRAINT fk_gm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Посты, репосты и вложения
CREATE TABLE posts (
                       id BIGSERIAL PRIMARY KEY,
                       author_id BIGINT NOT NULL,
                       group_id BIGINT,
                       content TEXT,
                       parent_post_id BIGINT,
                       is_deleted BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
                       CONSTRAINT fk_post_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                       CONSTRAINT fk_post_parent FOREIGN KEY (parent_post_id) REFERENCES posts(id) ON DELETE SET NULL
);

CREATE TABLE post_attachments (
                                  id BIGSERIAL PRIMARY KEY,
                                  post_id BIGINT NOT NULL,
                                  file_url VARCHAR(255) NOT NULL,
                                  file_type VARCHAR(50),
                                  file_name VARCHAR(255),
                                  CONSTRAINT fk_attachment_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Социальное взаимодействие
CREATE TABLE post_comments (
                               id BIGSERIAL PRIMARY KEY,
                               post_id BIGINT NOT NULL,
                               author_id BIGINT NOT NULL,
                               content TEXT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                               CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE post_likes (
                            post_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            PRIMARY KEY (post_id, user_id),
                            CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                            CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Дружба и подписки
CREATE TABLE friendships (
                             user_id BIGINT NOT NULL,
                             friend_id BIGINT NOT NULL,
                             status VARCHAR(50) DEFAULT 'PENDING',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (user_id, friend_id),
                             CONSTRAINT fk_friend_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             CONSTRAINT fk_friend_target FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_subscriptions (
                                    follower_id BIGINT NOT NULL,
                                    target_id BIGINT NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (follower_id, target_id),
                                    CONSTRAINT fk_sub_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
                                    CONSTRAINT fk_sub_target FOREIGN KEY (target_id) REFERENCES users(id) ON DELETE CASCADE
);