package com.fitlife.usuario.infrastructure.adapter.out.storage;

import com.fitlife.usuario.application.port.out.ProfilePhotoStoragePort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalProfilePhotoStorageAdapter implements ProfilePhotoStoragePort {
    private final Path directory;
    public LocalProfilePhotoStorageAdapter(@Value("${fitlife.uploads.directory:./uploads}") String directory) {
        this.directory=Paths.get(directory).toAbsolutePath().normalize();
    }
    @Override
    public String salvar(long usuarioId, byte[] conteudo, String contentType) throws IOException {
        Files.createDirectories(directory);
        String extensao=switch(contentType){case "image/jpeg"->"jpg";case "image/png"->"png";case "image/webp"->"webp";default->throw new IllegalArgumentException("Tipo de imagem não permitido.");};
        String nome="usuario-"+usuarioId+"-"+UUID.randomUUID()+"."+extensao;
        Files.write(directory.resolve(nome),conteudo);
        return "/uploads/"+nome;
    }
}
