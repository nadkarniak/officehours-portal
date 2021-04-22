package com.example.aman.officehoursportal.entity;

import javax.persistence.*;

@Entity
@Table(name = "exchanges")
public class ExchangeRequest extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_status")
    private ExchangeStatus status;

    @OneToOne
    @JoinColumn(name = "id_meeting_requestor")
    private Meeting requestor;

    @OneToOne
    @JoinColumn(name = "id_meeting_requested")
    private Meeting requested;


    public ExchangeRequest() {

    }

    public ExchangeRequest(Meeting requestor, Meeting requested, ExchangeStatus status) {
        this.status = status;
        this.requestor = requestor;
        this.requested = requested;
    }

    public ExchangeStatus getStatus() {
        return status;
    }

    public void setStatus(ExchangeStatus status) {
        this.status = status;
    }

    public Meeting getRequestor() {
        return requestor;
    }

    public void setRequestor(Meeting requestor) {
        this.requestor = requestor;
    }

    public Meeting getRequested() {
        return requested;
    }

    public void setRequested(Meeting requested) {
        this.requested = requested;
    }
}
