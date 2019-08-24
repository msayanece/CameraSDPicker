package com.sayan.sdk.mediacollector.exceptions;

public final class CameraProviderSetupException extends RuntimeException {
    public CameraProviderSetupException() {
        super("Data required for image capturing");
    }

    public CameraProviderSetupException(String message) {
        super(message);
    }
}
