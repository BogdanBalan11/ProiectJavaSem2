-- app_user Table
CREATE TABLE IF NOT EXISTS app_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  balance DECIMAL(15, 2) DEFAULT 0.00,
  PRIMARY KEY (id)
);

-- Category Table
CREATE TABLE IF NOT EXISTS category (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(500),
  PRIMARY KEY (id)
);

-- Bill Table
CREATE TABLE IF NOT EXISTS bill (
  id BIGINT NOT NULL AUTO_INCREMENT,
  bill_name VARCHAR(255) NOT NULL,
  amount DECIMAL(15, 2) NOT NULL,
  next_due_date DATE NOT NULL,
  description VARCHAR(500),
  app_user_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (app_user_id) REFERENCES app_user (id) ON DELETE CASCADE
);

-- Budget Table
CREATE TABLE IF NOT EXISTS budget (
  id BIGINT NOT NULL AUTO_INCREMENT,
  amount DECIMAL(15, 2) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  app_user_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (app_user_id) REFERENCES app_user (id) ON DELETE CASCADE
);

-- Goal Table
CREATE TABLE IF NOT EXISTS goal (
  id BIGINT NOT NULL AUTO_INCREMENT,
  goal_name VARCHAR(255) NOT NULL,
  target_amount DECIMAL(15, 2) NOT NULL,
  saved_amount DECIMAL(15, 2) DEFAULT 0.00,
  deadline DATE NOT NULL,
  app_user_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (app_user_id) REFERENCES app_user (id) ON DELETE CASCADE
);

-- Transaction Table
CREATE TABLE IF NOT EXISTS transaction (
  id BIGINT NOT NULL AUTO_INCREMENT,
  amount DECIMAL(15, 2) NOT NULL,
  transaction_date DATE NOT NULL,
  description VARCHAR(500),
  transaction_type ENUM('INCOME', 'EXPENSE') NOT NULL,
  app_user_id BIGINT NOT NULL,
  category_id BIGINT,
  PRIMARY KEY (id),
  FOREIGN KEY (app_user_id) REFERENCES app_user (id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL
);