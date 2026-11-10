package com.mtms.controller;

import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class TrackViewController {

    @Autowired
    private TrackService tracks;

    @GetMapping("/tracks")
    public String showTracks(){
        return "tracks";
    }
}
