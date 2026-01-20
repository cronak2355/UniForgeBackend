-- Comments Table Creation Script
-- Run this if ddl-auto update fails to create the table.

CREATE TABLE IF NOT EXISTS comments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    content VARCHAR(2000) NOT NULL,
    game_id VARCHAR(36) NOT NULL,
    author_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_comments_game FOREIGN KEY (game_id) REFERENCES games (id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (id)
);

-- Index for performance (finding comments by game)
CREATE INDEX idx_comments_game_id ON comments (game_id);
