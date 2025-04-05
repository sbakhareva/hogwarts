SELECT students.name, faculties.name FROM students INNER JOIN faculties ON students.faculty_id = faculties.id

SELECT students.name FROM students INNER JOIN avatar ON students.id = avatar.student_id