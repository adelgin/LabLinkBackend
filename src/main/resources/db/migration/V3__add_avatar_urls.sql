-- Добавляем аватарку для пользователей
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);

-- Индекс для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_users_avatar ON users(avatar_url);

-- Убедимся что в groups уже есть avatar_url (добавим если нет)
ALTER TABLE groups ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);
CREATE INDEX IF NOT EXISTS idx_groups_avatar ON groups(avatar_url);

-- Комментарии
COMMENT ON COLUMN users.avatar_url IS 'URL аватарки пользователя';
COMMENT ON COLUMN groups.avatar_url IS 'URL аватарки группы';