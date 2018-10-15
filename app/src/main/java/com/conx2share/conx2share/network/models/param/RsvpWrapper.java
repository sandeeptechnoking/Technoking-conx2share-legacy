package com.conx2share.conx2share.network.models.param;

public class RsvpWrapper {
    private Rsvp rsvp;

    public RsvpWrapper(int status) {
        this.rsvp = new Rsvp(status);
    }

    public Rsvp getRsvp() {
        return rsvp;
    }

    public void setRsvp(Rsvp rsvp) {
        this.rsvp = rsvp;
    }

    private class Rsvp {
        private String status;

        public Rsvp(int status) {
            this.status = String.valueOf(status);
        }

        public int getStatus() {
            return Integer.parseInt(status);
        }

        public void setStatus(int status) {
            this.status = String.valueOf(status);
        }
    }
}
