CREATE TABLE IF NOT EXISTS evento(id VARCHAR(36) PRIMARY KEY,nome VARCHAR(120) NOT NULL,data_evento VARCHAR(30) NOT NULL,local VARCHAR(180) NOT NULL,descricao VARCHAR(500),imagem LONGTEXT,criado_por BIGINT,criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS evento_participante(evento_id VARCHAR(36) NOT NULL,usuario_id BIGINT NOT NULL,criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,PRIMARY KEY(evento_id,usuario_id),FOREIGN KEY(evento_id) REFERENCES evento(id) ON DELETE CASCADE);
INSERT IGNORE INTO evento(id,nome,data_evento,local,descricao,imagem,criado_por) VALUES
('corrida','CORRIDA DE RUA','2026-12-05T08:00','Parque da cidade','Encontro para correr em grupo, com percursos para diferentes ritmos.','https://images.unsplash.com/photo-1552674605-db6ffd4facb5?w=900',NULL),
('bike','BIKE ON ROOF','2026-12-12T09:00','Espaço fitness','Aula coletiva de bike com música e muita energia.','https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=900',NULL),
('defesa','WORKSHOP DEFESA PESSOAL','2026-12-19T14:00','Centro esportivo','Introdução às técnicas de defesa pessoal em grupo.','https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=900',NULL),
('trilha','TRILHA NO PARQUE','2026-12-20T08:00','Parque municipal','Caminhada ao ar livre com orientação.','https://images.unsplash.com/photo-1551632811-561732d1e306?w=900',NULL),
('yoga','YOGA NO JARDIM BOTÂNICO','2026-12-21T09:00','Jardim Botânico','Prática de yoga ao ar livre para todos os níveis.','https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=900',NULL);
