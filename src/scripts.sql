SELECT * FROM students
SELECT name FROM students
SELECT * FROM students WHERE name LIKE '%о%' OR name LIKE '%О%'
SELECT * FROM students WHERE age > 10 AND age < 20
тут формулировка задания "возраст меньше идентификатора", сомнительно но окэй, но у меня id на начинаются с 1 и три студента в базе
SELECT * FROM students WHERE age < id
SELECT * FROM students ORDER BY age
