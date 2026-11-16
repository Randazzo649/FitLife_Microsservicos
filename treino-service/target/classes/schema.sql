IF OBJECT_ID(N'dbo.treino', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.treino (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        titulo NVARCHAR(150) NOT NULL,
        cor NVARCHAR(20) NOT NULL CONSTRAINT DF_treino_cor DEFAULT '#ff6300',
        categorias_json NVARCHAR(MAX) NOT NULL,
        criado_em DATETIME2 NOT NULL CONSTRAINT DF_treino_criado_em DEFAULT SYSUTCDATETIME()
    );
    CREATE INDEX IX_treino_usuario ON dbo.treino(usuario_id);
END;
