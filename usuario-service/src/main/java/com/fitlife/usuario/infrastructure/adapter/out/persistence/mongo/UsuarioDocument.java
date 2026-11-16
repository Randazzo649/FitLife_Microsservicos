package com.fitlife.usuario.infrastructure.adapter.out.persistence.mongo;

import com.fitlife.usuario.domain.model.Usuario;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class UsuarioDocument {
    @Id private Long id;
    private String nome;
    private String dataNasc;
    @Indexed(unique = true) private String cpf;
    private String genero;
    private String telefone;
    private String endereco;
    @Indexed(unique = true) private String email;
    private String senhaHash;
    private BigDecimal peso;
    private BigDecimal altura;
    private String sexo;
    private String frequencia;
    private String atividade;
    private String cardiopatia;
    private String fumo;
    private String alcool;
    private List<Object> saude;
    private String restricao;
    private String objetivo;
    private String objetivoMedico;
    private String fotoUrl;
    private Instant criadoEm;
    private Instant atualizadoEm;

    public UsuarioDocument() { }

    public static UsuarioDocument de(Usuario u) {
        UsuarioDocument d = new UsuarioDocument();
        d.id=u.id(); d.nome=u.nome(); d.dataNasc=u.dataNasc(); d.cpf=u.cpf(); d.genero=u.genero();
        d.telefone=u.telefone(); d.endereco=u.endereco(); d.email=u.email(); d.senhaHash=u.senhaHash();
        d.peso=u.peso(); d.altura=u.altura(); d.sexo=u.sexo(); d.frequencia=u.frequencia();
        d.atividade=u.atividade(); d.cardiopatia=u.cardiopatia(); d.fumo=u.fumo(); d.alcool=u.alcool();
        d.saude=u.saude(); d.restricao=u.restricao(); d.objetivo=u.objetivo(); d.objetivoMedico=u.objetivoMedico();
        d.fotoUrl=u.fotoUrl(); d.criadoEm=u.criadoEm(); d.atualizadoEm=u.atualizadoEm();
        return d;
    }

    public Usuario paraDominio() {
        return new Usuario(id,nome,dataNasc,cpf,genero,telefone,endereco,email,senhaHash,peso,altura,
                sexo,frequencia,atividade,cardiopatia,fumo,alcool,saude,restricao,objetivo,
                objetivoMedico,fotoUrl,criadoEm,atualizadoEm);
    }
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getNome(){return nome;} public void setNome(String v){nome=v;}
    public String getDataNasc(){return dataNasc;} public void setDataNasc(String v){dataNasc=v;}
    public String getCpf(){return cpf;} public void setCpf(String v){cpf=v;}
    public String getGenero(){return genero;} public void setGenero(String v){genero=v;}
    public String getTelefone(){return telefone;} public void setTelefone(String v){telefone=v;}
    public String getEndereco(){return endereco;} public void setEndereco(String v){endereco=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getSenhaHash(){return senhaHash;} public void setSenhaHash(String v){senhaHash=v;}
    public BigDecimal getPeso(){return peso;} public void setPeso(BigDecimal v){peso=v;}
    public BigDecimal getAltura(){return altura;} public void setAltura(BigDecimal v){altura=v;}
    public String getSexo(){return sexo;} public void setSexo(String v){sexo=v;}
    public String getFrequencia(){return frequencia;} public void setFrequencia(String v){frequencia=v;}
    public String getAtividade(){return atividade;} public void setAtividade(String v){atividade=v;}
    public String getCardiopatia(){return cardiopatia;} public void setCardiopatia(String v){cardiopatia=v;}
    public String getFumo(){return fumo;} public void setFumo(String v){fumo=v;}
    public String getAlcool(){return alcool;} public void setAlcool(String v){alcool=v;}
    public List<Object> getSaude(){return saude;} public void setSaude(List<Object> v){saude=v;}
    public String getRestricao(){return restricao;} public void setRestricao(String v){restricao=v;}
    public String getObjetivo(){return objetivo;} public void setObjetivo(String v){objetivo=v;}
    public String getObjetivoMedico(){return objetivoMedico;} public void setObjetivoMedico(String v){objetivoMedico=v;}
    public String getFotoUrl(){return fotoUrl;} public void setFotoUrl(String v){fotoUrl=v;}
    public Instant getCriadoEm(){return criadoEm;} public void setCriadoEm(Instant v){criadoEm=v;}
    public Instant getAtualizadoEm(){return atualizadoEm;} public void setAtualizadoEm(Instant v){atualizadoEm=v;}
}
