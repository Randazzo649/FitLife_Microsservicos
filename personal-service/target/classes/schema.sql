IF OBJECT_ID(N'dbo.personal', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.personal (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        nome NVARCHAR(140) NOT NULL,
        titulo NVARCHAR(180) NULL,
        modalidades NVARCHAR(300) NULL,
        disponibilidade NVARCHAR(180) NULL,
        cidade NVARCHAR(100) NULL,
        estado NVARCHAR(2) NULL,
        email NVARCHAR(180) NULL,
        whatsapp NVARCHAR(30) NULL
    );
END;
IF OBJECT_ID(N'dbo.contato_personal', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.contato_personal (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        personal_id BIGINT NOT NULL,
        criado_em DATETIME2 NOT NULL CONSTRAINT DF_contato_personal_criado DEFAULT SYSUTCDATETIME(),
        CONSTRAINT UQ_contato_usuario_personal UNIQUE(usuario_id,personal_id),
        CONSTRAINT FK_contato_personal_personal FOREIGN KEY(personal_id) REFERENCES dbo.personal(id)
    );
END;
IF NOT EXISTS (SELECT 1 FROM dbo.personal WHERE titulo=N'Musculação e condicionamento físico')
    INSERT INTO dbo.personal(nome,titulo,modalidades,disponibilidade,cidade,estado) VALUES(N'Personal de musculação',N'Musculação e condicionamento físico',N'musculação, condicionamento físico e funcional',N'segunda a sexta-feira',N'Curitiba',N'PR');
IF NOT EXISTS (SELECT 1 FROM dbo.personal WHERE titulo=N'Treinamento funcional')
    INSERT INTO dbo.personal(nome,titulo,modalidades,disponibilidade,cidade,estado) VALUES(N'Personal de treinamento funcional',N'Treinamento funcional',N'funcional, mobilidade e bem-estar',N'terça, quinta e sábado',N'Curitiba',N'PR');
IF NOT EXISTS (SELECT 1 FROM dbo.personal WHERE titulo=N'Corrida e resistência')
    INSERT INTO dbo.personal(nome,titulo,modalidades,disponibilidade,cidade,estado) VALUES(N'Personal de corrida',N'Corrida e resistência',N'corrida, resistência e preparação física',N'segunda, quarta e sexta-feira',N'Curitiba',N'PR');
