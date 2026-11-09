//package com.mtms.controller;
//
//import com.mtms.domain.Track;
//import com.mtms.service.ReplayService;
//import com.mtms.service.TrackService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//public class TrackController {
//    @Autowired
//    private TrackService trackService;
//    @Autowired
//    private ReplayService replayService;
//
//    //localhost:8080/api/tracks/list
//    @RequestMapping("/api/tracks/list")
//    public static List<Track> getAllTracks(){
//        return TrackService.findAllTracks();
//    }
//
//    //localhost:8080/api/tracks/search?trackNumber=TRK1002
//    @RequestMapping(value ="/api/tracks/search", params = "trackNumber")
//    public List<Track> getTrackByNum(@RequestParam("trackNumber") String trackNumber){
//        return TrackService.findTrackByNum(trackNumber);
//    }
//
//    //localhost:8080/api/tracks/history?sourceId=LINK-C&trackNumber=TRK1002
//    @RequestMapping(value = "/api/tracks/history", params = {"sourceId", "trackNumber"})
//    public List<Track> getFullHistory(@RequestParam("sourceId") String sourceId,
//                                      @RequestParam("trackNumber") String trackNumber){
//        return TrackService.getFullHistory(sourceId, trackNumber);
//    }
//
//    //localhost:8080/api/tracks/filter?identity=FRIEND&status=ACTIVE&minSpeed=450
//    @RequestMapping(value="/api/tracks/filter", params = {"identity", "status", "minSpeed"})
//    public List<Track> getFilteredTracks(@RequestParam("identity") String identity,
//                                         @RequestParam("status") String status,
//                                         @RequestParam("minSpeed") String minSpeed){
//        if (!minSpeed.isEmpty())
//            return TrackService.getFilteredTracks(identity, status, Double.parseDouble(minSpeed));
//        else
//            return TrackService.getFilteredTracks(identity, status, null);
//    }
//
//    //
//    @RequestMapping(method = RequestMethod.DELETE, value = "/api/tracks/maintenance", params = "trackNumber")
//    public String deleteTrackByNum(@RequestParam("trackNumber") String trackNumber){
//        if(TrackService.deleteTrackByNum(trackNumber))
//            return "Track " + trackNumber + " has been deleted";
//        return "Track " + trackNumber + " could not be deleted";
//    }
//
//
//}
