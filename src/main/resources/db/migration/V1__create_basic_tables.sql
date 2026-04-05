-- 1. География
CREATE TABLE countries (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    iso_code CHAR(2) UNIQUE
);

CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    country_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE
);

-- 2. Пользователи
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    patronymic VARCHAR(100),
    birth_date DATE,
    city_id INT,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE SET NULL
);

-- 3. Реестр организаций
CREATE TABLE organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    org_type VARCHAR(50) NOT NULL, -- UNIVERSITY, COMPANY, LABORATORY, etc.
    city_id INT,
    website VARCHAR(255),
    CONSTRAINT fk_org_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE SET NULL
);

-- 4. Образование
CREATE TABLE user_education (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    degree VARCHAR(100),         -- Bachelor, Master, PhD
    field_of_study VARCHAR(255),
    start_date DATE,
    end_date DATE,
    CONSTRAINT fk_edu_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_edu_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- 5. Карьера
CREATE TABLE career_experience (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    department VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    CONSTRAINT fk_career_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_career_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_users_city ON users(city_id);
CREATE INDEX idx_org_city ON organizations(city_id);
CREATE INDEX idx_edu_user ON user_education(user_id);
CREATE INDEX idx_career_user ON career_experience(user_id);

-- Таблица-справочник уникальных тегов
CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица связей пользователей с тегами
CREATE TABLE user_tags (
    user_id BIGINT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (user_id, tag_id),
    CONSTRAINT fk_ut_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ut_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Индекс для поиска людей по тегу
CREATE INDEX idx_user_tags_tag ON user_tags(tag_id);

-- =====================================================
-- ПУБЛИКАЦИИ
-- =====================================================
CREATE TABLE publications (
                              id BIGSERIAL PRIMARY KEY,
                              doi VARCHAR(255) UNIQUE,
                              journal_title VARCHAR(255),
                              source VARCHAR(255),
                              title VARCHAR(255) NOT NULL,
                              description TEXT,
                              url VARCHAR(255),
                              year INTEGER,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- СВЯЗЬ ПУБЛИКАЦИЙ С АВТОРАМИ
-- =====================================================
-- Поддерживает соавторство, порядок авторов и ответственных авторов
CREATE TABLE publication_authors (
                                     publication_id BIGINT NOT NULL,
                                     user_id BIGINT NOT NULL,
                                     author_order INTEGER NOT NULL,
                                     is_corresponding_author BOOLEAN DEFAULT FALSE,
                                     PRIMARY KEY (publication_id, user_id),
                                     CONSTRAINT fk_pub_auth_publication FOREIGN KEY (publication_id) REFERENCES publications(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_pub_auth_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Индексы для публикаций и авторов
CREATE INDEX idx_publications_year ON publications(year);
CREATE INDEX idx_publications_doi ON publications(doi);
CREATE INDEX idx_pub_authors_publication ON publication_authors(publication_id);
CREATE INDEX idx_pub_authors_user ON publication_authors(user_id);
CREATE INDEX idx_pub_authors_order ON publication_authors(publication_id, author_order);