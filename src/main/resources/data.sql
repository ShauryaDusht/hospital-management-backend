INSERT INTO patient (name, gender, birth_date, email, blood_group)
VALUES
    ('Shaurya Bisht', 'MALE', '1999-02-18', 'shaurya.bisht@example.com', 'B_POSITIVE'),
    ('Ananya Roy', 'FEMALE', '1994-11-05', 'ananya.roy@example.com', 'O_NEGATIVE'),
    ('Rohit Malhotra', 'MALE', '1987-06-22', 'rohit.malhotra@example.com', 'A_NEGATIVE'),
    ('Pooja Kulkarni', 'FEMALE', '1991-09-14', 'pooja.kulkarni@example.com', 'AB_NEGATIVE'),
    ('Vikram Choudhary', 'MALE', '1985-01-30', 'vikram.choudhary@example.com', 'B_NEGATIVE');

INSERT INTO doctor (name, specialization, email)
VALUES
    ('Dr. Meenal Joshi', 'Neurology', 'meenal.joshi@example.com'),
    ('Dr. Kunal Bansal', 'Gastroenterology', 'kunal.bansal@example.com'),
    ('Dr. Farah Khan', 'Pulmonology', 'farah.khan@example.com');

INSERT INTO appointment (appointment_time, reason, doctor_id, patient_id)
VALUES
    ('2025-08-01 09:00:00', 'Migraine Evaluation', 1, 1),
    ('2025-08-02 10:15:00', 'Stomach Pain', 2, 2),
    ('2025-08-03 11:30:00', 'Breathing Difficulty', 3, 3),
    ('2025-08-04 15:00:00', 'Routine Follow-up', 1, 4),
    ('2025-08-05 17:45:00', 'Digestive Issues', 2, 5),
    ('2025-08-06 08:00:00', 'Asthma Review', 3, 1);
