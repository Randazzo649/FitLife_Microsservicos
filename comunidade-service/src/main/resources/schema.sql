IF OBJECT_ID(N'dbo.evento', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.evento (
        id NVARCHAR(36) NOT NULL PRIMARY KEY,
        nome NVARCHAR(120) NOT NULL,
        data_evento NVARCHAR(30) NOT NULL,
        local NVARCHAR(180) NOT NULL,
        descricao NVARCHAR(500) NULL,
        imagem NVARCHAR(MAX) NULL,
        criado_por BIGINT NULL,
        criado_em DATETIME2 NOT NULL CONSTRAINT DF_evento_criado_em DEFAULT SYSUTCDATETIME()
    );
END;
IF OBJECT_ID(N'dbo.evento_participante', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.evento_participante (
        evento_id NVARCHAR(36) NOT NULL,
        usuario_id BIGINT NOT NULL,
        criado_em DATETIME2 NOT NULL CONSTRAINT DF_evento_participante_criado_em DEFAULT SYSUTCDATETIME(),
        CONSTRAINT PK_evento_participante PRIMARY KEY(evento_id,usuario_id),
        CONSTRAINT FK_evento_participante_evento FOREIGN KEY(evento_id) REFERENCES dbo.evento(id) ON DELETE CASCADE
    );
END;
IF OBJECT_ID(N'dbo.atividade_treino', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.atividade_treino (
        event_id NVARCHAR(36) NOT NULL PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        tipo NVARCHAR(80) NOT NULL,
        total_treinos INT NOT NULL,
        ocorrido_em DATETIME2 NOT NULL
    );
    CREATE INDEX IX_atividade_treino_usuario ON dbo.atividade_treino(usuario_id);
END;
IF NOT EXISTS (SELECT 1 FROM dbo.evento WHERE id=N'corrida')
    INSERT INTO dbo.evento(id,nome,data_evento,local,descricao,imagem,criado_por) VALUES(N'corrida',N'CORRIDA DE RUA',N'2026-12-05T08:00',N'Parque da cidade',N'Encontro para correr em grupo, com percursos para diferentes ritmos.',N'https://images.unsplash.com/photo-1552674605-db6ffd4facb5?w=900',NULL);
IF NOT EXISTS (SELECT 1 FROM dbo.evento WHERE id=N'bike')
    INSERT INTO dbo.evento(id,nome,data_evento,local,descricao,imagem,criado_por) VALUES(N'bike',N'BIKE ON ROOF',N'2026-12-12T09:00',N'Espaço fitness',N'Aula coletiva de bike com música e muita energia.',N'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=900',NULL);
IF NOT EXISTS (SELECT 1 FROM dbo.evento WHERE id=N'defesa')
    INSERT INTO dbo.evento(id,nome,data_evento,local,descricao,imagem,criado_por) VALUES(N'defesa',N'WORKSHOP DEFESA PESSOAL',N'2026-12-19T14:00',N'Centro esportivo',N'Introdução às técnicas de defesa pessoal em grupo.',N'https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=900',NULL);
IF NOT EXISTS (SELECT 1 FROM dbo.evento WHERE id=N'trilha')
    INSERT INTO dbo.evento(id,nome,data_evento,local,descricao,imagem,criado_por) VALUES(N'trilha',N'TRILHA NO PARQUE',N'2026-12-20T08:00',N'Parque municipal',N'Caminhada ao ar livre com orientação.',N'https://images.unsplash.com/photo-1551632811-561732d1e306?w=900',NULL);
IF NOT EXISTS (SELECT 1 FROM dbo.evento WHERE id=N'yoga')
    INSERT INTO dbo.evento(id,nome,data_evento,local,descricao,imagem,criado_por) VALUES(N'yoga',N'YOGA NO JARDIM BOTÂNICO',N'2026-12-21T09:00',N'Jardim Botânico',N'Prática de yoga ao ar livre para todos os níveis.',N'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=900',NULL);
