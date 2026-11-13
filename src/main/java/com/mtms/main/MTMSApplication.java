
package com.mtms.main;

import com.mtms.networking.Server;
import com.mtms.networking.SourceAClient;
import com.mtms.networking.SourceBClient;
import com.mtms.networking.SourceCClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import static java.lang.Thread.sleep;

@SpringBootApplication(scanBasePackages = "com.mtms")
@EntityScan(basePackages = "com.mtms.domain")
@EnableJpaRepositories(basePackages = "com.mtms.repository")
public class MTMSApplication {
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(MTMSApplication.class, args);

//
//        Server server = new Server();
//        SourceAClient clientA = new SourceAClient();
//        SourceBClient clientB = new SourceBClient();
//        SourceCClient clientC = new SourceCClient();

        Server server = context.getBean(Server.class);
        SourceAClient clientA = context.getBean(SourceAClient.class);
        SourceBClient clientB = context.getBean(SourceBClient.class);
        SourceCClient clientC = context.getBean(SourceCClient.class);

        new Thread(server::recevingDataFromClient).start();
        new Thread(clientA::sendingDataToServer).start();
        new Thread(clientB::sendingDataToServer).start();
        new Thread(clientC::sendingDataToServer).start();

//        Thread serverThread = new Thread(server::recevingDataFromClient);
//        serverThread.start();
//
//        Thread clientAThread = new Thread(clientA::sendingDataToServer);
//        clientAThread.start();
//
//        Thread clientBThread = new Thread(clientB::sendingDataToServer);
//        clientBThread.start();
//
//        Thread clientCThread = new Thread(clientC::sendingDataToServer);
//        clientCThread.start();

//        // waiting for tracks to be processed and saved
//        sleep(1000);
//
//        // generating processing counters
//        TrackService.processingCounter();
//
//        // generate matching tracks
//        TrackService.getCorrelatedTracks();


//        //APPLICATION STARTS HERE
//        System.out.println("===============================");
//        System.out.println("MTMS APPLICATION STARTS HERE");
//        System.out.println("===============================");

//        // Simulating tracks
//        TrackSimulator simulator = new TrackSimulator();
//        List<Track> listOfTracks = simulator.generateTracks();

//        // Processing tracks and saving to database
//        System.out.println("\nProcessing Tracks: ");
//        for(Track track : listOfTracks)
//            TrackService.processTrack(track);
//
//        System.out.println("\n--------------------------------\n");  //formatting
//
//        // Displaying tracks with delay
//        System.out.println("Displaying Tracks (DOUBLE speed): ");
//        ReplayService.replayTracks(ReplaySpeed.DOUBLE);
//
//        // Displaying tracks with delay and time frame
//        System.out.println("\nDisplaying Tracks (HALF speed) with a time frame: ");
//        ReplayService.replayTracks(ReplaySpeed.HALF,
//                LocalDateTime.of(2026, 10, 28, 7, 41, 25, 123_000_000),
//                LocalDateTime.of(2026, 10, 28, 7, 55, 25, 123_000_000));
//
//        System.out.println("\n--------------------------------\n");  //formatting
//
//        // Generate track reports
//        System.out.println("Displaying Track Analytics: ");
//        TrackAnalyticsService.showReportStat();
//
//        System.out.println("\n--------------------------------");  //formatting
//
//        // Displaying all alerts
//        AlertService.getAllAlerts();
//
//        System.out.println("\n--------------------------------\n");  //formatting
//
//        // Display severity summary
//        System.out.println("Displaying severity summary of the alerts: ");
//        AlertAnalyticsService analyticsService = new AlertAnalyticsService();
//        analyticsService.getSeveritySummary();
//

//        //APPLICATION ENDS HERE
//        System.out.println("\n===============================");
//        System.out.println("MTMS APPLICATION ENDS HERE ");
//        System.out.println("===============================");

    }
}