/* Execute conectado à database master em uma instância SQL Server local. */
IF DB_ID(N'FitLife_Treinos') IS NULL
    CREATE DATABASE [FitLife_Treinos];
GO

IF DB_ID(N'FitLife_Personais') IS NULL
    CREATE DATABASE [FitLife_Personais];
GO

IF DB_ID(N'FitLife_Comunidade') IS NULL
    CREATE DATABASE [FitLife_Comunidade];
GO
