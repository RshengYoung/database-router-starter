package com.rshenghub.db.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.rshenghub.db.entity.embedded.Author;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "company", schema = "bu")
public class Company {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "cname")
    private String cname;

    @Column(name = "ename")
    private String ename;

    @Column(name = "since")
    private LocalDateTime since;

    @Embedded
    private Author author = new Author();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanySite> sites;

}
