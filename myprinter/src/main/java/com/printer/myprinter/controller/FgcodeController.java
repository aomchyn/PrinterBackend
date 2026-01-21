package com.printer.myprinter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.printer.myprinter.entity.FgcodeEntity;
import com.printer.myprinter.repository.FgcodeRepositoiry;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/fgcode")
public class FgcodeController {
    private final FgcodeRepositoiry fgcodeRepositoiry;

    public FgcodeController (FgcodeRepositoiry fgcodeRepositoiry){
        this.fgcodeRepositoiry = fgcodeRepositoiry;
    }

    @GetMapping
    public List<FgcodeEntity> getAllFgcode(){
        return fgcodeRepositoiry.findAll();
    }
    

    @PostMapping
    public FgcodeEntity createFgcode(@RequestBody FgcodeEntity fgcode){
        return fgcodeRepositoiry.save(fgcode);
    }

    @GetMapping("/id")
    public FgcodeEntity getById (@PathVariable String id){
        return fgcodeRepositoiry.findByid(id).orElse(null);
    }

    @PutMapping("/{id}")
    public FgcodeEntity updateFgcode(@PathVariable String id, @RequestBody FgcodeEntity fgcode){
        FgcodeEntity fgcodeUpdate = fgcodeRepositoiry.findByid(id);

        if (fgcodeUpdate == null){
            throw new IllegalArgumentException("Not found");
        }

        fgcodeUpdate.setName(fgcode.getName());
        fgcode.setExp(fgcode.getExp());

        return fgcodeRepositoiry.save(fgcode);
    }

    @DeleteMapping("/{id}")
    public void dropFgcode (@PathVariable String id){
        
        fgcodeRepositoiry.deleteById(id);
    }
    
    
}
