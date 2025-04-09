-- liquibase formatted sql

-- changeset sbakhareva:1
CREATE INDEX student_name_index ON students (name)

-- changeset sbakhareva:2
CREATE INDEX faculty_nc_index ON faculties (name, color)