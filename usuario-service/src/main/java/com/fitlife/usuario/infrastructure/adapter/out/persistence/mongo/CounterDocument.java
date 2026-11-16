package com.fitlife.usuario.infrastructure.adapter.out.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counters")
public class CounterDocument {
    @Id private String id;
    private long sequence;
    public CounterDocument() { }
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public long getSequence(){return sequence;} public void setSequence(long sequence){this.sequence=sequence;}
}
