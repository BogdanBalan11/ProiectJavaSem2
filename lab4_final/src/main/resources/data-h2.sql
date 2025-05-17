-- Insert into app_user
INSERT INTO app_user (email, password, name, balance)
VALUES
('john.doe@example.com', 'hashed_password_1', 'John Doe', 1500.00),
('jane.smith@example.com', 'hashed_password_2', 'Jane Smith', 2450.50);

-- Insert into category
INSERT INTO category (name, description)
VALUES
('Groceries', 'Expenses related to food and household items'),
('Salary', 'Monthly salary income'),
('Utilities', 'Bills such as electricity, water, and gas');

-- Insert into bill
INSERT INTO bill (bill_name, amount, next_due_date, description, app_user_id)
VALUES
('Electricity Bill', 120.75, '2025-06-10', 'June electric bill', 1),
('Water Bill', 45.60, '2025-06-15', 'Monthly water usage', 2);

-- Insert into budget
INSERT INTO budget (amount, start_date, end_date, app_user_id)
VALUES
(1000.00, '2025-05-01', '2025-05-31', 1),
(1500.00, '2025-05-01', '2025-05-31', 2);

-- Insert into goal
INSERT INTO goal (goal_name, target_amount, saved_amount, deadline, app_user_id)
VALUES
('Vacation Fund', 2000.00, 500.00, '2025-12-01', 1),
('New Laptop', 1200.00, 200.00, '2025-09-01', 2);

-- Insert into transaction
INSERT INTO transaction (amount, transaction_date, description, transaction_type, app_user_id, category_id)
VALUES
(2000.00, '2025-05-01', 'Salary for May', 'INCOME', 1, 2),
(150.00, '2025-05-05', 'Grocery shopping', 'EXPENSE', 1, 1),
(100.00, '2025-05-06', 'Utility payment', 'EXPENSE', 2, 3),
(2500.00, '2025-05-01', 'Salary for May', 'INCOME', 2, 2);
