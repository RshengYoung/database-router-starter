package com.rshenghub.db.entity;

import com.rshenghub.db.entity.embedded.Author;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "company_site", schema = "bu")
public class CompanySite {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Column(name = "name")
    private String name;

    @Column(name = "cname")
    private String cname;

    @Column(name = "ename")
    private String ename;

    @Column(name = "address")
    private String address;

    @Column(name = "owner")
    private String owner;

    @Embedded
    private Author author = new Author();

}
