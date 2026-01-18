-- =====================================================
-- Hospital Management System - Database Fixtures
-- =====================================================

-- Clear existing data (optional - comment out in production)
-- TRUNCATE TABLE appointments, patients, doctors, insurance, departments, users CASCADE;

-- =====================================================
-- DEPARTMENTS
-- =====================================================
INSERT INTO department (
    id,
    name,
    description,
    created_at,
    updated_at
) VALUES
      (1,  'Cardiology',        'Heart and cardiovascular system care',        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (2,  'Neurology',         'Brain and nervous system treatment',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (3,  'Orthopedics',       'Bone, joint and muscle care',                  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (4,  'Pediatrics',        'Child health and development',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (5,  'General Medicine',  'General health consultation',                  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (6,  'Dermatology',       'Skin and hair treatment',                      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (7,  'ENT',               'Ear, Nose and Throat specialist',              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (8,  'Gynecology',        'Women health and wellness',                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (9,  'Ophthalmology',     'Eye care and vision treatment',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (10, 'Psychiatry',        'Mental health and counseling',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- USERS (Admin, Doctors, Patients)
-- =====================================================

-- Admin Users
INSERT INTO users (
    id,
    username,
    password,
    name,
    email,
    roles,
    auth_provider,
    created_at,
    updated_at
) VALUES
      (1, 'admin',  '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Rajesh Kumar', 'rajesh.kumar@hospital.com', 'ADMIN', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (2, 'admin2', '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Priya Sharma', 'priya.sharma@hospital.com', 'ADMIN', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;

-- Doctor Users
INSERT INTO users (
    id,
    username,
    password,
    name,
    email,
    roles,
    auth_provider,
    created_at,
    updated_at
) VALUES
      (3,  'dr.mehta',  '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Amit Mehta',    'amit.mehta@hospital.com',    'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (4,  'dr.verma',  '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Neha Verma',    'neha.verma@hospital.com',    'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (5,  'dr.singh',  '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Vikram Singh',  'vikram.singh@hospital.com',  'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (6,  'dr.reddy',  '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Lakshmi Reddy', 'lakshmi.reddy@hospital.com', 'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (7,  'dr.patel',  '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Karan Patel',   'karan.patel@hospital.com',   'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (8,  'dr.chopra', '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Anjali Chopra', 'anjali.chopra@hospital.com', 'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (9,  'dr.nair',   '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Suresh Nair',   'suresh.nair@hospital.com',   'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (10, 'dr.iyer',   '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Divya Iyer',    'divya.iyer@hospital.com',    'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (11, 'dr.gupta',  '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Rahul Gupta',   'rahul.gupta@hospital.com',   'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (12, 'dr.das',    '$2a$10$N9qo8uLOickgx2ZMRZoMye7IxQWqEV3YEJxWmNXMlCGWKGxqPl9ua',
       'Dr. Sneha Das',     'sneha.das@hospital.com',     'DOCTOR', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- Reset sequences
-- =====================================================
SELECT setval('department_id_seq',  (SELECT MAX(id) FROM department));
SELECT setval('users_id_seq',       (SELECT MAX(id) FROM users));
SELECT setval('doctor_id_seq',      (SELECT MAX(id) FROM doctor));
SELECT setval('patient_id_seq',     (SELECT MAX(id) FROM patient));
SELECT setval('insurance_id_seq',   (SELECT MAX(id) FROM insurance));
SELECT setval('appointment_id_seq', (SELECT MAX(id) FROM appointment));


-- Default password -> password123