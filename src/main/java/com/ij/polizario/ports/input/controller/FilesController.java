package com.ij.polizario.ports.input.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/files")
public class FilesController {

    @GetMapping("/accountingInterface")
    public String accountingInterface(){
        return "accountingInterface generated";
    }
}
