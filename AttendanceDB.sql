USE [master];
GO

-- Create the database
CREATE DATABASE [AttendanceDB];
GO

-- Set compatibility level (optional, based on target SQL Server version)
ALTER DATABASE [AttendanceDB] SET COMPATIBILITY_LEVEL = 160;
GO

IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [AttendanceDB].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [AttendanceDB] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [AttendanceDB] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [AttendanceDB] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [AttendanceDB] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [AttendanceDB] SET ARITHABORT OFF 
GO
ALTER DATABASE [AttendanceDB] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [AttendanceDB] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [AttendanceDB] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [AttendanceDB] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [AttendanceDB] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [AttendanceDB] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [AttendanceDB] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [AttendanceDB] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [AttendanceDB] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [AttendanceDB] SET  ENABLE_BROKER 
GO
ALTER DATABASE [AttendanceDB] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [AttendanceDB] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [AttendanceDB] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [AttendanceDB] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [AttendanceDB] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [AttendanceDB] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [AttendanceDB] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [AttendanceDB] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [AttendanceDB] SET  MULTI_USER 
GO
ALTER DATABASE [AttendanceDB] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [AttendanceDB] SET DB_CHAINING OFF 
GO
ALTER DATABASE [AttendanceDB] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [AttendanceDB] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [AttendanceDB] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [AttendanceDB] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [AttendanceDB] SET QUERY_STORE = ON
GO
ALTER DATABASE [AttendanceDB] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [AttendanceDB]
GO
/****** Object:  Table [dbo].[Admins]    Script Date: 25/05/2025 6:13:41 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Admins](
	[admin_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[first_name] [nvarchar](50) NULL,
	[last_name] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[admin_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Attendance]    Script Date: 25/05/2025 6:13:42 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Attendance](
	[attendance_id] [int] IDENTITY(1,1) NOT NULL,
	[enrollment_id] [int] NOT NULL,
	[date] [date] NOT NULL,
	[status] [nvarchar](20) NOT NULL,
	[reason] [nvarchar](255) NULL,
	[approval_status] [nvarchar](20) NULL,
	[approved_by] [int] NULL,
	[approval_date] [date] NULL,
PRIMARY KEY CLUSTERED 
(
	[attendance_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Classes]    Script Date: 25/05/2025 6:13:42 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Classes](
	[class_id] [int] IDENTITY(1,1) NOT NULL,
	[teacher_id] [int] NOT NULL,
	[course_name] [nvarchar](100) NULL,
	[section] [nvarchar](50) NULL,
	[program] [nvarchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[class_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Enrollments]    Script Date: 25/05/2025 6:13:42 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Enrollments](
	[enrollment_id] [int] IDENTITY(1,1) NOT NULL,
	[student_id] [int] NOT NULL,
	[class_id] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[enrollment_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Notifications]    Script Date: 25/05/2025 6:13:42 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Notifications](
	[notification_id] [int] IDENTITY(1,1) NOT NULL,
	[sender_user_id] [int] NULL,
	[recipient_user_id] [int] NULL,
	[message] [nvarchar](max) NULL,
	[notification_type] [nvarchar](100) NULL,
	[date_sent] [datetime] NULL,
	[is_read] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[notification_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Students]    Script Date: 25/05/2025 6:13:42 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Students](
	[student_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[last_name] [nvarchar](50) NULL,
	[first_name] [nvarchar](50) NULL,
	[middle_name] [nvarchar](50) NULL,
	[year_level] [nvarchar](50) NULL,
	[section] [nvarchar](50) NULL,
	[program] [nvarchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[student_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Teachers]    Script Date: 25/05/2025 6:13:42 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Teachers](
	[teacher_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[first_name] [nvarchar](50) NULL,
	[last_name] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[teacher_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 25/05/2025 6:13:42 pm ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[user_id] [int] IDENTITY(1,1) NOT NULL,
	[username] [nvarchar](50) NOT NULL,
	[email] [nvarchar](100) NOT NULL,
	[password_hash] [nvarchar](255) NOT NULL,
	[role] [nvarchar](20) NOT NULL,
	[created_by_admin_id] [int] NULL,
	[date_created] [datetime] NULL,
	[profile_path] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Attendance] ADD  DEFAULT ('Pending') FOR [approval_status]
GO
ALTER TABLE [dbo].[Notifications] ADD  DEFAULT (getdate()) FOR [date_sent]
GO
ALTER TABLE [dbo].[Notifications] ADD  DEFAULT ((0)) FOR [is_read]
GO
ALTER TABLE [dbo].[Users] ADD  DEFAULT (getdate()) FOR [date_created]
GO
ALTER TABLE [dbo].[Admins]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Attendance]  WITH CHECK ADD FOREIGN KEY([approved_by])
REFERENCES [dbo].[Users] ([user_id])
GO
ALTER TABLE [dbo].[Attendance]  WITH CHECK ADD FOREIGN KEY([enrollment_id])
REFERENCES [dbo].[Enrollments] ([enrollment_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Classes]  WITH CHECK ADD FOREIGN KEY([teacher_id])
REFERENCES [dbo].[Teachers] ([teacher_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD FOREIGN KEY([class_id])
REFERENCES [dbo].[Classes] ([class_id])
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD FOREIGN KEY([student_id])
REFERENCES [dbo].[Students] ([student_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Notifications]  WITH CHECK ADD  CONSTRAINT [FK_Notifications_Recipient] FOREIGN KEY([recipient_user_id])
REFERENCES [dbo].[Users] ([user_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Notifications] CHECK CONSTRAINT [FK_Notifications_Recipient]
GO
ALTER TABLE [dbo].[Notifications]  WITH CHECK ADD  CONSTRAINT [FK_Notifications_Sender] FOREIGN KEY([sender_user_id])
REFERENCES [dbo].[Users] ([user_id])
GO
ALTER TABLE [dbo].[Notifications] CHECK CONSTRAINT [FK_Notifications_Sender]
GO
ALTER TABLE [dbo].[Students]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Teachers]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Users]  WITH CHECK ADD  CONSTRAINT [FK_Users_CreatedByAdmin] FOREIGN KEY([created_by_admin_id])
REFERENCES [dbo].[Admins] ([admin_id])
GO
ALTER TABLE [dbo].[Users] CHECK CONSTRAINT [FK_Users_CreatedByAdmin]
GO
ALTER TABLE [dbo].[Attendance]  WITH CHECK ADD CHECK  (([approval_status]='Rejected' OR [approval_status]='Approved' OR [approval_status]='Pending'))
GO
ALTER TABLE [dbo].[Attendance]  WITH CHECK ADD  CONSTRAINT [CK_Attendance_Status] CHECK  (([status]='Pending' OR [status]='Late' OR [status]='Excused' OR [status]='Absent' OR [status]='Present'))
GO
ALTER TABLE [dbo].[Attendance] CHECK CONSTRAINT [CK_Attendance_Status]
GO
USE [master]
GO
ALTER DATABASE [AttendanceDB] SET  READ_WRITE 
GO
