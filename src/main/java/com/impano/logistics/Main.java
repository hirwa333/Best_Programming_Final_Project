package com.impano.logistics;

import com.impano.logistics.api.ApiServer;
import com.impano.logistics.ui.ConsoleApp;

/**
 * Entry point for Smart Logistics & Transportation Management System
 * Starts the REST API server on port 8080, then launches the console UI.
 *
 * Usage:
 *   --api      : start API server only (for web frontend)
 *   --console  : start console UI only
 *   (no args)  : start API server + console UI
 */
public class Main {
    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "--api";

        if ("--console".equals(mode)) {
            new ConsoleApp().run();
        } else if ("--api".equals(mode)) {
            new ApiServer().start();
            // Keep running
            Thread.currentThread().join();
        } else {
            // Start API in background thread, then run console
            new Thread(() -> {
                try { new ApiServer().start(); }
                catch (Exception e) { System.out.println("API server error: " + e.getMessage()); }
            }).start();
            new ConsoleApp().run();
        }
    }
}
