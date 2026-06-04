package com.athlunakms.user.controller;

import com.athlunakms.user.dto.CountryDto;
import com.athlunakms.user.entity.Province;
import com.athlunakms.user.service.CountryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/countries"})
public class CountryController {
    private static final Logger log = LoggerFactory.getLogger(CountryController.class);
    private final CountryService countryService;

    @GetMapping(value={"/enabled"})
    public ResponseEntity<Map<String, Object>> getEnabledCountries() {
        List<CountryDto> countries = this.countryService.getEnabledCountries();
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("data", countries);
        result.put("total", countries.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCountries() {
        List<CountryDto> countries = this.countryService.getAllCountries();
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("data", countries);
        result.put("total", countries.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping(value={"/{id}"})
    public ResponseEntity<Map<String, Object>> getCountryById(@PathVariable(value="id") Integer id) {
        CountryDto country = this.countryService.getCountryById(id);
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (country != null) {
            result.put("success", true);
            result.put("data", country);
        } else {
            result.put("success", false);
            result.put("message", "\u56fd\u5bb6\u4e0d\u5b58\u5728");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(value={"/{countryCode}/provinces"})
    public ResponseEntity<List<Province>> getProvinces(@PathVariable(value="countryCode") String countryCode) {
        return ResponseEntity.ok(this.countryService.getProvincesByCountry(countryCode));
    }

    @GetMapping(value={"/provinces/all"})
    public ResponseEntity<List<Province>> getAllProvinces() {
        return ResponseEntity.ok(this.countryService.getAllProvinces());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCountry(@RequestBody CountryDto dto) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            CountryDto created = this.countryService.createCountry(dto);
            result.put("success", true);
            result.put("data", created);
            result.put("message", "\u521b\u5efa\u6210\u529f");
        }
        catch (Exception e) {
            log.error("Failed to create country", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping(value={"/{id}"})
    public ResponseEntity<Map<String, Object>> updateCountry(@PathVariable(value="id") Integer id, @RequestBody CountryDto dto) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            CountryDto updated = this.countryService.updateCountry(id, dto);
            result.put("success", true);
            result.put("data", updated);
            result.put("message", "\u66f4\u65b0\u6210\u529f");
        }
        catch (Exception e) {
            log.error("Failed to update country", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<Map<String, Object>> deleteCountry(@PathVariable(value="id") Integer id) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            this.countryService.deleteCountry(id);
            result.put("success", true);
            result.put("message", "\u5220\u9664\u6210\u529f");
        }
        catch (Exception e) {
            log.error("Failed to delete country", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(value={"/{id}/toggle"})
    public ResponseEntity<Map<String, Object>> toggleEnabled(@PathVariable(value="id") Integer id) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            CountryDto updated = this.countryService.toggleEnabled(id);
            result.put("success", true);
            result.put("data", updated);
            result.put("message", updated.getEnabled() != false ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528");
        }
        catch (Exception e) {
            log.error("Failed to toggle country", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }
}

