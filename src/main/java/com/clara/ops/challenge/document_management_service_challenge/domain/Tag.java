package com.clara.ops.challenge.document_management_service_challenge.domain;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tags", schema = "document_schema")
public class Tag {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @Column(name = "tag")
    private String tag;

    public Tag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString(){
        return tag;
    }
}
