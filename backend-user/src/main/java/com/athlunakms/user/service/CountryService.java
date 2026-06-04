package com.athlunakms.user.service;

import com.athlunakms.user.dto.CountryDto;
import com.athlunakms.user.entity.Country;
import com.athlunakms.user.entity.Province;
import com.athlunakms.user.repository.CountryRepository;
import com.athlunakms.user.repository.ProvinceRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountryService {
    private static final Logger log = LoggerFactory.getLogger(CountryService.class);
    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;

    public List<Province> getProvincesByCountry(String countryCode) {
        return this.provinceRepository.findByCountryCodeAndEnabledTrueOrderByProvinceNameAsc(countryCode);
    }

    public List<Province> getAllProvinces() {
        return this.provinceRepository.findAll();
    }

    public List<CountryDto> getEnabledCountries() {
        return this.countryRepository.findAllEnabled().stream().map(arg_0 -> this.toDto(arg_0)).collect(Collectors.toList());
    }

    public List<CountryDto> getAllCountries() {
        return this.countryRepository.findAllOrderBySortOrder().stream().map(arg_0 -> this.toDto(arg_0)).collect(Collectors.toList());
    }

    public CountryDto getCountryById(Integer id) {
        return this.countryRepository.findById(id).map(this::toDto).orElse(null);
    }

    public CountryDto getCountryByCode(String code) {
        return this.countryRepository.findByCode(code).map(arg_0 -> this.toDto(arg_0)).orElse(null);
    }

    @Transactional
    public CountryDto createCountry(CountryDto dto) {
        if (this.countryRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("\u56fd\u5bb6\u4ee3\u7801\u5df2\u5b58\u5728: " + dto.getCode());
        }
        Country country = new Country();
        BeanUtils.copyProperties(dto, country, "id", "createdAt", "updatedAt");
        country = (Country)this.countryRepository.save(country);
        log.info("Created country: {} - {}", country.getCode(), country.getNameCn());
        return this.toDto(country);
    }

    @Transactional
    public CountryDto updateCountry(Integer id, CountryDto dto) {
        Country country = (Country)this.countryRepository.findById(id).orElseThrow(() -> new RuntimeException("\u56fd\u5bb6\u4e0d\u5b58\u5728: " + id));
        if (!country.getCode().equals(dto.getCode()) && this.countryRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("\u56fd\u5bb6\u4ee3\u7801\u5df2\u5b58\u5728: " + dto.getCode());
        }
        country.setCode(dto.getCode());
        country.setNameCn(dto.getNameCn());
        country.setNameEn(dto.getNameEn());
        country.setPhonePrefix(dto.getPhonePrefix());
        country.setTimezone(dto.getTimezone());
        country.setSortOrder(dto.getSortOrder());
        country.setEnabled(dto.getEnabled());
        country = (Country)this.countryRepository.save(country);
        log.info("Updated country: {} - {}", country.getCode(), country.getNameCn());
        return this.toDto(country);
    }

    @Transactional
    public void deleteCountry(Integer id) {
        Country country = (Country)this.countryRepository.findById(id).orElseThrow(() -> new RuntimeException("\u56fd\u5bb6\u4e0d\u5b58\u5728: " + id));
        this.countryRepository.delete(country);
        log.info("Deleted country: {} - {}", country.getCode(), country.getNameCn());
    }

    @Transactional
    public CountryDto toggleEnabled(Integer id) {
        Country country = (Country)this.countryRepository.findById(id).orElseThrow(() -> new RuntimeException("\u56fd\u5bb6\u4e0d\u5b58\u5728: " + id));
        country.setEnabled(!country.getEnabled());
        country = (Country)this.countryRepository.save(country);
        log.info("Toggled country {} enabled: {}", country.getCode(), country.getEnabled());
        return this.toDto(country);
    }

    private CountryDto toDto(Country entity) {
        CountryDto dto = new CountryDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public CountryService(CountryRepository countryRepository, ProvinceRepository provinceRepository) {
        this.countryRepository = countryRepository;
        this.provinceRepository = provinceRepository;
    }
}

