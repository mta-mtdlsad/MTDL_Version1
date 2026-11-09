package com.mtms.simulator;

import com.mtms.domain.Track;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackSimulator {
    static List<String> listOfTracks = null;
    public static List<String> generateTracksA(){
        listOfTracks = new ArrayList<>(
                Arrays.asList(
                        "LINK-A|1|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:10:00",
                        "LINK-A|2|TRK1001|15.971600|90.594600|500|FRIEND|ACTIVE|2026-11-02T10:20:00",
                        "LINK-A|2|TRK1001|15.971600|90.594600|500|FRIEND|ACTIVE|2026-11-02T10:20:00",
                        "LINK-A|3|TRK1001|30.971600|120.594600|650|FRIEND|ACTIVE|2026-11-02T11:30:00",
                        "LINK-A|4|TRK1002|0.971600|30.594600|900|UNKNOWN|ACTIVE|2026-11-02T09:15:00",
                        "LINK-A|5|TRK1002|2.971600|50.594600|1000|HOSTILE|ACTIVE|2026-11-02T10:30:00"
                        )
        );
        return listOfTracks;
    }

    public static List<String> generateTracksB(){
        listOfTracks = new ArrayList<>(
                Arrays.asList(
                        "LINK-B|1|TRK1002|20.971600|70.594600|200|UNKNOWN|ACTIVE|2026-11-01T09:10:00",
                        "LINK-B|3|TRK1002|25.971600|75.594600|300|FRIEND|ACTIVE|2026-11-01T10:20:00",
                        "LINK-B|2|TRK1002|30.971600|80.594600|400|FRIEND|ACTIVE|2026-11-01T11:30:00",
                        "LINK-B|4|TRK1002|30.971600|80.594600|400|FRIEND|INACTIVE|2026-11-01T12:00:00",
                        "LINK-B|5|TRK1001|12.987650|77.594600|500|FRIEND|ACTIVE|2026-11-02T09:10:10"
                )
        );
        return listOfTracks;
    }

    public static List<String> generateTracksC(){
        listOfTracks = new ArrayList<>(
                Arrays.asList(
                        "LINK-C|1|TRK1003|80.971600|100.594600|700|UNKNOWN|ACTIVE|2026-11-02T09:15:00",
                        "LINK-C|2|TRK1003|70.971600|110.594600|100|HOSTILE|ACTIVE|2026-11-02T10:25:00",
                        "LINK-C|3|TRK1003|60.971600|120.594600|900|HOSTILE|ACTIVE|2026-11-02T11:35:00",
                        "LINK-C|4|TRK1002|30.971600|80.594600|470|FRIEND|INACTIVE|2026-11-01T12:00:20",
                        "LINK-C|5|TRK1004|60.971600|170.594600|450|UNKNOWN|ACTIVE|2026-11-02T07:10:00"
                )
        );
        return listOfTracks;
    }
}



