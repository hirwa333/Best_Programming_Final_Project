package com.impano.logistics.repository;

import com.impano.logistics.model.*;

import java.io.*;
import java.util.*;

/**
 * DataStore handles saving and loading all application data to a file.
 * This ensures data persists between application restarts.
 * Data is stored in a simple text file: sltms_data.txt
 */
public class DataStore {

    private static final String FILE = "sltms_data.txt";

    // ---- SAVE ALL DATA ----
    public static void save(ClientRepository clientRepo,
                            DriverRepository driverRepo,
                            VehicleRepository vehicleRepo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {

            // Save clients
            for (Client c : clientRepo.findAll()) {
                pw.println("CLIENT|" + c.getUserId() + "|" + c.getName() + "|" +
                           c.getEmail() + "|" + c.getPassword() + "|" +
                           c.getPhone() + "|" + c.getAddress());
            }

            // Save drivers
            for (Driver d : driverRepo.findAll()) {
                pw.println("DRIVER|" + d.getUserId() + "|" + d.getName() + "|" +
                           d.getEmail() + "|" + d.getPassword() + "|" +
                           d.getLicenseNumber() + "|" + d.isAvailable());
            }

            // Save vehicles
            for (Vehicle v : vehicleRepo.findAll()) {
                pw.println("VEHICLE|" + v.getVehicleId() + "|" + v.getPlateNumber() + "|" +
                           v.getType() + "|" + v.getCapacityKg() + "|" + v.getStatus());
            }

        } catch (IOException e) {
            System.out.println("Warning: Could not save data — " + e.getMessage());
        }
    }

    // ---- LOAD ALL DATA ----
    public static void load(ClientRepository clientRepo,
                            DriverRepository driverRepo,
                            VehicleRepository vehicleRepo) {
        File file = new File(FILE);
        if (!file.exists()) return; // First run — no data yet

        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;

                switch (parts[0]) {
                    case "CLIENT" -> {
                        // CLIENT|id|name|email|password|phone|address
                        Client c = new Client(parts[1], parts[2], parts[3],
                                              parts[4], parts[5], parts[6]);
                        clientRepo.save(c);
                    }
                    case "DRIVER" -> {
                        // DRIVER|id|name|email|password|license|available
                        Driver d = new Driver(parts[1], parts[2], parts[3],
                                              parts[4], parts[5]);
                        d.setAvailable(Boolean.parseBoolean(parts[6]));
                        driverRepo.save(d);
                    }
                    case "VEHICLE" -> {
                        // VEHICLE|id|plate|type|capacity|status
                        Vehicle v = new Vehicle(parts[1], parts[2],
                                                VehicleType.valueOf(parts[3]),
                                                Double.parseDouble(parts[4]));
                        v.setStatus(VehicleStatus.valueOf(parts[5]));
                        vehicleRepo.save(v);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load data — " + e.getMessage());
        }
    }
}
