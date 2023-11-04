package com.rshenghub.rest.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rshenghub.db.entity.Company;
import com.rshenghub.db.entity.CompanySite;
import com.rshenghub.db.entity.embedded.Author;
import com.rshenghub.db.repository.CompanyRepository;
import com.rshenghub.rest.model.AuthorDto;
import com.rshenghub.rest.model.CompanyDto;
import com.rshenghub.rest.model.CompanySiteDto;

@RestController
@RequestMapping("/api/v1")
public class CompanyAPIController {

    @Autowired
    private CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    @GetMapping("/company")
    public List<CompanyDto> getCompany() {
        var companies = companyRepository.findAll();
        return companies.stream().map(this::convert).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @GetMapping("/company/{companyId}")
    public CompanyDto getCompany(@PathVariable String companyId) {
        var company = companyRepository.findById(companyId).orElseThrow();
        return convert(company);
    }

    @Transactional
    @PostMapping("/company")
    public CompanyDto createCompany(@RequestBody CompanyDto dto) {
        return convert(companyRepository.save(this.convert(dto)));
    }

    @Transactional
    @DeleteMapping("/company/{companyId}")
    public void deleteCompnay(@PathVariable String companyId) {
        companyRepository.deleteById(companyId);
    }

    private Company convert(CompanyDto dto) {
        var company = new Company();
        company.setId(Objects.requireNonNullElse(dto.getCompanyId(), UUID.randomUUID().toString().replaceAll("-", "")));
        company.setName(dto.getCompanyName());
        company.setCname(dto.getCompanyCname());
        company.setEname(dto.getCompanyEname());
        company.setSince(dto.getSince());
        company.setSites(dto.getSites().stream().map(this::convert).map(site -> {
            site.setCompany(company);
            return site;
        }).collect(Collectors.toList()));

        return company;
    }

    private CompanySite convert(CompanySiteDto dto) {
        var site = new CompanySite();
        site.setId(Objects.requireNonNullElse(dto.getSiteId(), UUID.randomUUID().toString().replaceAll("-", "")));
        site.setName(dto.getSiteName());
        site.setCname(dto.getSiteCname());
        site.setEname(dto.getSiteEname());
        return site;
    }

    private CompanyDto convert(Company company) {
        var dto = new CompanyDto();
        dto.setCompanyId(company.getId());
        dto.setCompanyName(company.getName());
        dto.setCompanyCname(company.getCname());
        dto.setCompanyEname(company.getEname());
        dto.setAuthor(convert(company.getAuthor()));
        dto.setSince(company.getSince());
        dto.setSites(company.getSites().stream().map(this::convert).collect(Collectors.toList()));
        return dto;
    }

    private CompanySiteDto convert(CompanySite site) {
        var dto = new CompanySiteDto();
        dto.setSiteId(site.getId());
        dto.setSiteName(site.getName());
        dto.setSiteCname(site.getCname());
        dto.setSiteEname(site.getEname());
        dto.setAddress(site.getAddress());
        dto.setAuthor(convert(site.getAuthor()));
        return dto;
    }

    private AuthorDto convert(Author author) {
        var dto = new AuthorDto();
        if (Objects.isNull(author)) {
            return dto;
        }

        dto.setCreateUserId(author.getCreateUserId());
        dto.setCreateTime(author.getCreateTime());
        dto.setModifyUserId(author.getModifyUserId());
        dto.setModifyTime(author.getModifyTime());
        return dto;
    }

}
