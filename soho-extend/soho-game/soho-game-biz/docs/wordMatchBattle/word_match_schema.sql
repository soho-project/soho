-- Word Match Battle schema (V1.2)

CREATE TABLE IF NOT EXISTS game_word_match_word (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  word VARCHAR(128) NOT NULL,
  meaning VARCHAR(512),
  level VARCHAR(32),
  freq DOUBLE,
  updated_time DATETIME,
  created_time DATETIME,
  UNIQUE KEY uk_word_level (word, level),
  KEY idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS game_word_match_battle (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id VARCHAR(64) NOT NULL,
  mode VARCHAR(32),
  status VARCHAR(32),
  winner_id VARCHAR(64),
  end_reason VARCHAR(64),
  scores_json LONGTEXT,
  started_at DATETIME,
  ended_at DATETIME,
  updated_time DATETIME,
  created_time DATETIME,
  UNIQUE KEY uk_room_id (room_id),
  KEY idx_status (status),
  KEY idx_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS game_word_match_battle_event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  battle_id BIGINT,
  room_id VARCHAR(64),
  seq BIGINT,
  type VARCHAR(64),
  payload_json LONGTEXT,
  created_time DATETIME,
  KEY idx_battle_seq (battle_id, seq),
  KEY idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS game_word_match_rank_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  player_id VARCHAR(64) NOT NULL,
  rank_score INTEGER,
  wins INTEGER,
  losses INTEGER,
  updated_time DATETIME,
  created_time DATETIME,
  UNIQUE KEY uk_player_id (player_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
