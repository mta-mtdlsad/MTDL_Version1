package com.domain;

import java.time.LocalDateTime;

public class Source {
    String source_id;
    String source_status;
    LocalDateTime first_seen;
    LocalDateTime last_seen;
    int highest_sequence;
    int received_count;
    int accepted_count;
    int rejected_count;
    int duplicate_count;
    int processing_failure_count;

    public Source(String source_id, String source_status, LocalDateTime first_seen, LocalDateTime last_seen,
                  int highest_sequence, int received_count, int accepted_count, int rejected_count, int duplicate_count,
                  int processing_failure_count) {
        this.source_id = source_id;
        this.source_status = source_status;
        this.first_seen = first_seen;
        this.last_seen = last_seen;
        this.highest_sequence = highest_sequence;
        this.received_count = received_count;
        this.accepted_count = accepted_count;
        this.rejected_count = rejected_count;
        this.duplicate_count = duplicate_count;
        this.processing_failure_count = processing_failure_count;
    }

    public String getSource_id() {
        return source_id;
    }

    public String getSource_status() {
        return source_status;
    }

    public LocalDateTime getFirst_seen() {
        return first_seen;
    }

    public LocalDateTime getLast_seen() {
        return last_seen;
    }

    public int getHighest_sequence() {
        return highest_sequence;
    }

    public int getReceived_count() {
        return received_count;
    }

    public int getAccepted_count() {
        return accepted_count;
    }

    public int getRejected_count() {
        return rejected_count;
    }

    public int getDuplicate_count() {
        return duplicate_count;
    }

    public int getProcessing_failure_count() {
        return processing_failure_count;
    }

    @Override
    public String toString() {
        return "Source{" +
                "source_id='" + source_id + '\'' +
                ", source_status='" + source_status + '\'' +
                ", first_seen=" + first_seen +
                ", last_seen=" + last_seen +
                ", highest_sequence=" + highest_sequence +
                ", received_count=" + received_count +
                ", accepted_count=" + accepted_count +
                ", rejected_count=" + rejected_count +
                ", duplicate_count=" + duplicate_count +
                ", processing_failure_count=" + processing_failure_count +
                '}';
    }
}
