IF DB_ID('UniversityStudentManagementDB') IS NULL
BEGIN
    CREATE DATABASE UniversityStudentManagementDB;
END
GO

USE UniversityStudentManagementDB;
GO

IF OBJECT_ID('dbo.ActivityLogs', 'U') IS NOT NULL
    DROP TABLE dbo.ActivityLogs;
GO

IF OBJECT_ID('dbo.StudentScores', 'U') IS NOT NULL
    DROP TABLE dbo.StudentScores;
GO

IF OBJECT_ID('dbo.Subjects', 'U') IS NOT NULL
    DROP TABLE dbo.Subjects;
GO

IF OBJECT_ID('dbo.Students', 'U') IS NOT NULL
    DROP TABLE dbo.Students;
GO

IF OBJECT_ID('dbo.Teachers', 'U') IS NOT NULL
    DROP TABLE dbo.Teachers;
GO

IF OBJECT_ID('dbo.Users', 'U') IS NOT NULL
    DROP TABLE dbo.Users;
GO

IF OBJECT_ID('dbo.Majors', 'U') IS NOT NULL
    DROP TABLE dbo.Majors;
GO

CREATE TABLE dbo.Users
(
    UserID INT IDENTITY(1,1) NOT NULL,
    Username NVARCHAR(50) NOT NULL,
    PasswordHash CHAR(64) NOT NULL,
    FullName NVARCHAR(120) NOT NULL,
    RoleName NVARCHAR(30) NOT NULL CONSTRAINT DF_Users_RoleName DEFAULT ('ADMIN'),
    IsActive BIT NOT NULL CONSTRAINT DF_Users_IsActive DEFAULT (1),
    CreatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_Users_CreatedAt DEFAULT (SYSDATETIME()),
    UpdatedAt DATETIME2(0) NULL,

    CONSTRAINT PK_Users PRIMARY KEY (UserID),
    CONSTRAINT UQ_Users_Username UNIQUE (Username),
    CONSTRAINT CK_Users_RoleName CHECK (RoleName IN ('ADMIN', 'STAFF', 'TEACHER', 'STUDENT'))
);
GO

CREATE TABLE dbo.Majors
(
    MajorID INT IDENTITY(1,1) NOT NULL,
    MajorCode NVARCHAR(20) NOT NULL,
    MajorName NVARCHAR(120) NOT NULL,
    Faculty NVARCHAR(120) NOT NULL,
    IsActive BIT NOT NULL CONSTRAINT DF_Majors_IsActive DEFAULT (1),

    CONSTRAINT PK_Majors PRIMARY KEY (MajorID),
    CONSTRAINT UQ_Majors_MajorCode UNIQUE (MajorCode),
    CONSTRAINT UQ_Majors_MajorName UNIQUE (MajorName)
);
GO

CREATE TABLE dbo.Subjects
(
    SubjectID INT IDENTITY(1,1) NOT NULL,
    SubjectCode NVARCHAR(20) NOT NULL,
    SubjectName NVARCHAR(120) NOT NULL,
    MajorID INT NOT NULL,
    IsActive BIT NOT NULL CONSTRAINT DF_Subjects_IsActive DEFAULT (1),

    CONSTRAINT PK_Subjects PRIMARY KEY (SubjectID),
    CONSTRAINT UQ_Subjects_SubjectCode UNIQUE (SubjectCode),
    CONSTRAINT FK_Subjects_Majors FOREIGN KEY (MajorID) REFERENCES dbo.Majors(MajorID),
    CONSTRAINT CK_Subjects_IsActive CHECK (IsActive IN (0, 1))
);
GO

CREATE TABLE dbo.Students
(
    StudentID INT IDENTITY(1,1) NOT NULL,
    StudentCode NVARCHAR(20) NOT NULL,
    StudentType NVARCHAR(20) NOT NULL,
    FirstName NVARCHAR(80) NOT NULL,
    LastName NVARCHAR(80) NOT NULL,
    Gender NVARCHAR(10) NOT NULL,
    DOB DATE NOT NULL,
    Phone NVARCHAR(25) NOT NULL,
    Email NVARCHAR(120) NOT NULL,
    Address NVARCHAR(255) NOT NULL,
    MajorID INT NOT NULL,
    YearLevel INT NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    GPA DECIMAL(3,2) NOT NULL,
    PhotoPath NVARCHAR(500) NULL,
    UserID INT NULL,
    CreatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_Students_CreatedAt DEFAULT (SYSDATETIME()),
    UpdatedAt DATETIME2(0) NULL,

    CONSTRAINT PK_Students PRIMARY KEY (StudentID),
    CONSTRAINT UQ_Students_StudentCode UNIQUE (StudentCode),
    CONSTRAINT UQ_Students_Email UNIQUE (Email),
    CONSTRAINT FK_Students_Majors FOREIGN KEY (MajorID) REFERENCES dbo.Majors(MajorID),
    CONSTRAINT FK_Students_Users FOREIGN KEY (UserID) REFERENCES dbo.Users(UserID),
    CONSTRAINT CK_Students_StudentType CHECK (StudentType IN ('UNDERGRADUATE', 'GRADUATE')),
    CONSTRAINT CK_Students_Gender CHECK (Gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT CK_Students_YearLevel CHECK (YearLevel BETWEEN 1 AND 8),
    CONSTRAINT CK_Students_Status CHECK (Status IN ('ACTIVE', 'SUSPENDED', 'GRADUATED', 'INACTIVE')),
    CONSTRAINT CK_Students_GPA CHECK (GPA BETWEEN 0.00 AND 4.00),
    CONSTRAINT CK_Students_DOB CHECK (DOB < CONVERT(DATE, GETDATE()))
);
GO

CREATE TABLE dbo.Teachers
(
    TeacherID INT IDENTITY(1,1) NOT NULL,
    TeacherCode NVARCHAR(20) NOT NULL,
    FirstName NVARCHAR(80) NOT NULL,
    LastName NVARCHAR(80) NOT NULL,
    Gender NVARCHAR(10) NOT NULL,
    DOB DATE NOT NULL,
    Phone NVARCHAR(25) NOT NULL,
    Email NVARCHAR(120) NOT NULL,
    Address NVARCHAR(255) NOT NULL,
    Department NVARCHAR(120) NOT NULL,
    Qualification NVARCHAR(120) NOT NULL,
    HireDate DATE NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    Salary DECIMAL(12,2) NOT NULL,
    PhotoPath NVARCHAR(500) NULL,
    UserID INT NULL,
    CreatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_Teachers_CreatedAt DEFAULT (SYSDATETIME()),
    UpdatedAt DATETIME2(0) NULL,

    CONSTRAINT PK_Teachers PRIMARY KEY (TeacherID),
    CONSTRAINT UQ_Teachers_TeacherCode UNIQUE (TeacherCode),
    CONSTRAINT UQ_Teachers_Email UNIQUE (Email),
    CONSTRAINT FK_Teachers_Users FOREIGN KEY (UserID) REFERENCES dbo.Users(UserID),
    CONSTRAINT CK_Teachers_Gender CHECK (Gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT CK_Teachers_Status CHECK (Status IN ('ACTIVE', 'SUSPENDED', 'INACTIVE')),
    CONSTRAINT CK_Teachers_Salary CHECK (Salary >= 0),
    CONSTRAINT CK_Teachers_DOB CHECK (DOB < CONVERT(DATE, GETDATE())),
    CONSTRAINT CK_Teachers_HireDate CHECK (HireDate <= CONVERT(DATE, GETDATE()))
);
GO

CREATE TABLE dbo.StudentScores
(
    ScoreID INT IDENTITY(1,1) NOT NULL,
    StudentID INT NOT NULL,
    SubjectID INT NOT NULL,
    Attendance DECIMAL(5,2) NOT NULL,
    Assignment DECIMAL(5,2) NOT NULL,
    Project DECIMAL(5,2) NOT NULL,
    Exam DECIMAL(5,2) NOT NULL,
    Participation DECIMAL(5,2) NOT NULL,
    Remarks NVARCHAR(500) NULL,
    DateRecorded DATE NOT NULL,
    CreatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StudentScores_CreatedAt DEFAULT (SYSDATETIME()),
    UpdatedAt DATETIME2(0) NULL,

    CONSTRAINT PK_StudentScores PRIMARY KEY (ScoreID),
    CONSTRAINT FK_StudentScores_Students FOREIGN KEY (StudentID) REFERENCES dbo.Students(StudentID),
    CONSTRAINT FK_StudentScores_Subjects FOREIGN KEY (SubjectID) REFERENCES dbo.Subjects(SubjectID),
    CONSTRAINT CK_StudentScores_Attendance CHECK (Attendance BETWEEN 0 AND 100),
    CONSTRAINT CK_StudentScores_Assignment CHECK (Assignment BETWEEN 0 AND 100),
    CONSTRAINT CK_StudentScores_Project CHECK (Project BETWEEN 0 AND 100),
    CONSTRAINT CK_StudentScores_Exam CHECK (Exam BETWEEN 0 AND 100),
    CONSTRAINT CK_StudentScores_Participation CHECK (Participation BETWEEN 0 AND 100),
    CONSTRAINT CK_StudentScores_DateRecorded CHECK (DateRecorded <= CONVERT(DATE, GETDATE()))
);
GO

CREATE TABLE dbo.ActivityLogs
(
    LogID INT IDENTITY(1,1) NOT NULL,
    UserID INT NULL,
    Username NVARCHAR(50) NOT NULL,
    ActionName NVARCHAR(50) NOT NULL,
    EntityName NVARCHAR(50) NOT NULL,
    EntityID NVARCHAR(50) NULL,
    Description NVARCHAR(500) NOT NULL,
    CreatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_ActivityLogs_CreatedAt DEFAULT (SYSDATETIME()),

    CONSTRAINT PK_ActivityLogs PRIMARY KEY (LogID),
    CONSTRAINT FK_ActivityLogs_Users FOREIGN KEY (UserID) REFERENCES dbo.Users(UserID)
);
GO

CREATE INDEX IX_Students_Name ON dbo.Students(LastName, FirstName);
CREATE INDEX IX_Students_MajorID ON dbo.Students(MajorID);
CREATE INDEX IX_Students_Status ON dbo.Students(Status);
CREATE INDEX IX_Teachers_Name ON dbo.Teachers(LastName, FirstName);
CREATE INDEX IX_Teachers_Department ON dbo.Teachers(Department);
CREATE INDEX IX_Teachers_Status ON dbo.Teachers(Status);
CREATE INDEX IX_StudentScores_StudentID ON dbo.StudentScores(StudentID);
CREATE INDEX IX_StudentScores_SubjectID ON dbo.StudentScores(SubjectID);
CREATE INDEX IX_StudentScores_DateRecorded ON dbo.StudentScores(DateRecorded DESC);
CREATE INDEX IX_ActivityLogs_CreatedAt ON dbo.ActivityLogs(CreatedAt DESC);
GO

INSERT INTO dbo.Users (Username, PasswordHash, FullName, RoleName)
SELECT 'admin',
       CONVERT(CHAR(64), HASHBYTES('SHA2_256', 'admin123'), 2),
       'System Administrator',
       'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Username = 'admin');
GO

INSERT INTO dbo.Majors (MajorCode, MajorName, Faculty)
SELECT MajorCode, MajorName, Faculty
FROM
(
    VALUES
        ('CS', 'Computer Science', 'Faculty of Science and Technology'),
        ('SE', 'Software Engineering', 'Faculty of Science and Technology'),
        ('IT', 'Information Technology', 'Faculty of Science and Technology'),
        ('BA', 'Business Administration', 'Faculty of Business'),
        ('ENG', 'English', 'Faculty of Arts'),
        ('ACC', 'Accounting', 'Faculty of Business')
) AS SeedMajors(MajorCode, MajorName, Faculty)
WHERE NOT EXISTS
(
    SELECT 1
    FROM dbo.Majors
    WHERE dbo.Majors.MajorCode = SeedMajors.MajorCode
);
GO

INSERT INTO dbo.Subjects (SubjectCode, SubjectName, MajorID)
SELECT SubjectCode, SubjectName, MajorID
FROM
(
    VALUES
        ('CS101', 'Data Structures', 1),
        ('CS102', 'Algorithms', 1),
        ('CS103', 'Database Systems', 1),
        ('CS104', 'Operating Systems', 1),
        ('SE101', 'Software Engineering', 2),
        ('SE102', 'Web Development', 2),
        ('SE103', 'Software Testing', 2),
        ('SE104', 'Mobile Application Development', 2),
        ('IT101', 'Computer Networks', 3),
        ('IT102', 'Systems Analysis', 3),
        ('IT103', 'Cybersecurity', 3),
        ('IT104', 'Cloud Computing', 3),
        ('BA101', 'Accounting Principles', 4),
        ('BA102', 'Marketing', 4),
        ('BA103', 'Management', 4),
        ('ENG101', 'English Literature', 5),
        ('ENG102', 'Writing Skills', 5),
        ('ENG103', 'Communication', 5),
        ('ACC101', 'Financial Accounting', 6),
        ('ACC102', 'Auditing', 6),
        ('ACC103', 'Taxation', 6)
) AS SeedSubjects(SubjectCode, SubjectName, MajorID)
WHERE NOT EXISTS
(
    SELECT 1
    FROM dbo.Subjects
    WHERE dbo.Subjects.SubjectCode = SeedSubjects.SubjectCode
);
GO

INSERT INTO dbo.ActivityLogs (UserID, Username, ActionName, EntityName, EntityID, Description)
SELECT UserID,
       Username,
       'DATABASE_SETUP',
       'Database',
       DB_NAME(),
       'Student Management System database schema initialized.'
FROM dbo.Users
WHERE Username = 'yumiena';
GO

SELECT 'UniversityStudentManagementDB setup completed successfully.' AS ResultMessage;
GO

SELECT TeacherID, TeacherCode, FirstName, LastName
FROM Teachers;


