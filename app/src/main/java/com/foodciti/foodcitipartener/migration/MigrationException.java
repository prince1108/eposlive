package com.foodciti.foodcitipartener.migration;

public class MigrationException extends Exception {
    public MigrationException(String e) {
        super(e);
    }
    public MigrationException(Throwable t) {
        super(t);
    }
}
