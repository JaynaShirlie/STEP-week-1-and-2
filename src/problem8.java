import java.util.*;

public class problem8 {

    private static final int SIZE = 500;

    enum Status {
        EMPTY,
        OCCUPIED,
        DELETED
    }

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            this.status = Status.EMPTY;
        }
    }

    private ParkingSpot[] table;
    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int totalParks = 0;
    private int[] hourlyCount = new int[24];

    public problem8() {
        table = new ParkingSpot[SIZE];
        for (int i = 0; i < SIZE; i++) {
            table[i] = new ParkingSpot();
        }
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % SIZE;
    }

    public String parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;

        for (int i = 0; i < SIZE; i++) {
            int current = (index + i) % SIZE;

            if (table[current].status == Status.EMPTY ||
                    table[current].status == Status.DELETED) {

                table[current].licensePlate = plate;
                table[current].entryTime = System.currentTimeMillis();
                table[current].status = Status.OCCUPIED;

                occupiedCount++;
                totalProbes += probes;
                totalParks++;

                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                hourlyCount[hour]++;

                return "Assigned spot #" + current + " (" + probes + " probes)";
            }

            probes++;
        }

        return "Parking Full";
    }

    public String exitVehicle(String plate) {
        int index = hash(plate);

        for (int i = 0; i < SIZE; i++) {
            int current = (index + i) % SIZE;

            if (table[current].status == Status.EMPTY) {
                return "Vehicle not found";
            }

            if (table[current].status == Status.OCCUPIED &&
                    table[current].licensePlate.equals(plate)) {

                long exitTime = System.currentTimeMillis();
                long durationMillis = exitTime - table[current].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);
                double fee = hours * 5.5;

                table[current].status = Status.DELETED;
                occupiedCount--;

                return "Spot #" + current + " freed, Duration: "
                        + String.format("%.2f", hours)
                        + "h, Fee: $" + String.format("%.2f", fee);
            }
        }

        return "Vehicle not found";
    }

    public String getStatistics() {
        double occupancy = (occupiedCount * 100.0) / SIZE;
        double avgProbes = totalParks == 0 ? 0 :
                (double) totalProbes / totalParks;

        int peakHour = 0;
        int maxCount = 0;

        for (int i = 0; i < 24; i++) {
            if (hourlyCount[i] > maxCount) {
                maxCount = hourlyCount[i];
                peakHour = i;
            }
        }

        return "Occupancy: " + String.format("%.2f", occupancy) +
                "%, Avg Probes: " + String.format("%.2f", avgProbes) +
                ", Peak Hour: " + peakHour + "-" + (peakHour + 1);
    }

    public static void main(String[] args) throws InterruptedException {

        problem8 parking = new problem8();

        System.out.println(parking.parkVehicle("ABC-1234"));
        System.out.println(parking.parkVehicle("ABC-1235"));
        System.out.println(parking.parkVehicle("XYZ-9999"));

        Thread.sleep(2000);

        System.out.println(parking.exitVehicle("ABC-1234"));
        System.out.println(parking.getStatistics());
    }
}