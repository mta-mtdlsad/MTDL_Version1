package com.mtms.controller;

import com.mtms.domain.Source;
import com.mtms.service.SourceService;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SourceController {

    @Autowired
    private SourceService sourceService;
    @Autowired
    private TrackService trackService;


    //getAllSources
    @ResponseBody
    @GetMapping("/api/source/monitor")
    public String getAllSources(Model model) {
        model.addAttribute("sources", "Sources List");
        List<Source> listOfSources = sourceService.findAllSources();

        StringBuilder result = new StringBuilder();

        result.append("========== LIST OF ALL SOURCES ==========<pre>");

        if (listOfSources == null || listOfSources.isEmpty()) {

            result.append("No sources available.");

        } else {

            buildingString(listOfSources, result);
            List<String> counter = trackService.processingCounter();
            result.append("Processing Counters:- <pre>");
            for (String count : counter)
                result.append(count).append("<pre>");
        }
        return result.toString();
    }




    public void buildingString(List<Source> listOfSources, StringBuilder result){
        for (Source source : listOfSources) {
            result.append("Source ID: ").append(source.getSourceId()).append("\n");
            result.append("firstSeenAt: ").append(source.getFirstSeenAt()).append("\n");
            result.append("lastReceivedAt: ").append(source.getLastReceivedAt()).append("\n");
            result.append("latestSequence: ").append(source.getLatestSequence()).append("\n");
            result.append("sourceStatus: ").append(source.getSourceStatus()).append("\n");
            result.append("------------------------------<pre>");
        }
    }



}


//    //getSourceById
//    @ResponseBody
//    @GetMapping("/api/sources/search")
//    public String findSourceById(@RequestParam("sourceId") String sourceId,
//                               Model model) {
//        model.addAttribute("sources", "Source Find");
//        List<Source> listOfSources = sourceService.findSourceById(sourceId);
//
//        StringBuilder result = new StringBuilder();
//
//        if (listOfSources != null && !listOfSources.isEmpty()) {
//
//            result.append("========== SOURCE SEARCH RESULTS ==========<pre>");
//            result.append("Displaying source with source id: ")
//                    .append(sourceId).append("\n");
//
//            buildingString(listOfSources, result);
//
//        } else {
//
//            result.append("Source with source id ")
//                    .append(sourceId)
//                    .append(" could not be found.");
//        }
//
//        return result.toString();
//    }


