package com.example.stlviewer.model;

public interface ConsoleMessages
{
    /**
     * Generic exception message for exceptions caught in the exception handler.
     */
    String EXCEPTION_HANDLER_LOOP = "Trying again. (%d/%d)%n\n";
    /**
     * Exception message for too many exceptions caught in the exception handler.
     */
    String EXCEPTION_HANDLER_EXIT = "Exiting program.";
    String NETWORK_EXCEPTION_UNKNOWN_HOST = "[ERROR] Don't know about host: %s\n";
    String NETWORK_EXCEPTION_IO = "[ERROR] Couldn't get I/O for the connection to: %s\n";
    String NETWORK_EXCEPTION_CONNECTION_SECURITY = "[ERROR] Security exception for the connection to: %s\n";
    String NETWORK_EXCEPTION_ILLEGAL_ARGUMENT = "[ERROR] Illegal argument exception for the connection to: %s\n";
    String THREAD_EXCEPTION_INTERRUPTED = "[ERROR] Thread interrupted: %s\n";
    String ERROR_NO_DATA_RECEIVED = "[ERROR] No data received from the drone.\n";
    String CONNECTION_EXCEPTION = "[Error] Could not establish connection: %s\n";
}
