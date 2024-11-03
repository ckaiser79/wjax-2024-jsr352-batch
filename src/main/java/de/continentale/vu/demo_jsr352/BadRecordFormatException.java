package de.continentale.vu.demo_jsr352;

import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;

public class BadRecordFormatException extends Exception {

    private PersonTextRecord record;

    public BadRecordFormatException(final String reason, final PersonTextRecord record) {
        super(reason);
        this.record = record;
    }

    public PersonTextRecord getRecord() {
        return record;
    }
}
