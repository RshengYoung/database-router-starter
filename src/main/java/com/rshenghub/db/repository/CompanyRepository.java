package com.rshenghub.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rshenghub.db.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, String> {

}
