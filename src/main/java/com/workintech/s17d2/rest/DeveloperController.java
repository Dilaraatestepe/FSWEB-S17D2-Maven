package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workintech.s17d2.model.Experience.*;

@RestController
@RequestMapping("/developers")

public class DeveloperController {

    public Map<Integer, Developer> developers;
    private Taxable taxable; // Dependency Injection

    // Constructor Dependency Injection,

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    public Map<Integer, Developer> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Map<Integer, Developer> developers) {
        this.developers = developers;
    }

    public Taxable getTaxable() {
        return taxable;
    }

    public void setTaxable(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    // 🔹 [GET] Tüm developer'ları List olarak döndür
    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    // 🔹 [GET] Belirtilen ID'ye sahip developer'ı döndür
    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    // 🔹 [POST] Yeni bir developer ekle (Vergi hesaplaması yaparak)
    @PostMapping
    public Developer addDeveloper(@RequestBody Developer developer) {
        int id = developers.size() + 1; // Yeni ID ataması
        double salary = developer.getSalary();

        // Vergi hesaplaması
        switch (developer.getExperience()) {
            case JUNIOR:
                salary -= salary * taxable.getSimpleTaxRate();
                break;
            case MID:
                salary -= salary * taxable.getMiddleTaxRate();
                break;
            case SENIOR:
                salary -= salary * taxable.getUpperTaxRate();
                break;
        }

        Developer newDeveloper = new Developer(id, developer.getName(), salary, developer.getExperience());
        developers.put(id, newDeveloper);
        return newDeveloper;
    }

    // 🔹 [PUT] Belirtilen ID'deki developer'ı güncelle
    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer updatedDeveloper) {
        if (developers.containsKey(id)) {
            updatedDeveloper.setId(id);
            developers.put(id, updatedDeveloper);
            return updatedDeveloper;
        }
        return null;
    }

    // 🔹 [DELETE] Belirtilen ID'yi developers Map'inden sil
    @DeleteMapping("/{id}")
    public String deleteDeveloper(@PathVariable int id) {
        if (developers.remove(id) != null) {
            return "Developer with ID " + id + " has been deleted.";
        }
        return "Developer not found.";
    }

}
