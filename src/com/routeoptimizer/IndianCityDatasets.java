package com.routeoptimizer;

import java.util.*;

public class IndianCityDatasets {

    public static class CityInfo {
        private final String id;
        private final String name;
        private final String state;
        private final String country = "India";
        private final double centerLat;
        private final double centerLng;
        private final int zoom;
        private final String description;

        public CityInfo(String id, String name, String state, double centerLat, double centerLng, int zoom, String description) {
            this.id = id;
            this.name = name;
            this.state = state;
            this.centerLat = centerLat;
            this.centerLng = centerLng;
            this.zoom = zoom;
            this.description = description;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getState() { return state; }
        public String getCountry() { return country; }
        public double getCenterLat() { return centerLat; }
        public double getCenterLng() { return centerLng; }
        public int getZoom() { return zoom; }
        public String getDescription() { return description; }
    }

    public static class CityDataset {
        private final CityInfo info;
        private final List<DepotDto> depots;
        private final List<VehicleDto> vehicles;
        private final List<CustomerDto> customers;

        public CityDataset(CityInfo info, List<DepotDto> depots, List<VehicleDto> vehicles, List<CustomerDto> customers) {
            this.info = info;
            this.depots = depots;
            this.vehicles = vehicles;
            this.customers = customers;
        }

        public CityInfo getInfo() { return info; }
        public List<DepotDto> getDepots() { return depots; }
        public List<VehicleDto> getVehicles() { return vehicles; }
        public List<CustomerDto> getCustomers() { return customers; }
    }

    private static final Map<String, CityDataset> CITY_DATASETS = new LinkedHashMap<>();

    static {
        // 1. Bengaluru, Karnataka (Default)
        CityInfo blrInfo = new CityInfo("bengaluru", "Bengaluru", "Karnataka", 12.9716, 77.5946, 11, "Silicon Valley of India Logistics Corridor");
        List<DepotDto> blrDepots = Arrays.asList(
                new DepotDto("W1", "Peenya Industrial Area Depot", 12.9978, 77.5587),
                new DepotDto("W2", "Hosur Road Logistics Hub", 12.8912, 77.6412),
                new DepotDto("W3", "Whitefield Logistics Hub", 12.9719, 77.7499)
        );
        List<VehicleDto> blrVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "W1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "W1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "W2", 0.12, 10.0),
                new VehicleDto("V4", 80.0, "W2", 0.12, 10.0)
        );
        List<CustomerDto> blrCustomers = Arrays.asList(
                new CustomerDto("C1", "Manyata Tech Park, Nagawara", 13.0475, 77.6200, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Phoenix Marketcity, Whitefield", 12.9959, 77.6964, 25.0, "MEDIUM", 15.0, 540.0, 720.0),
                new CustomerDto("C3", "Rajajinagar Industrial Area", 12.9915, 77.5524, 30.0, "HIGH", 10.0, 500.0, 680.0),
                new CustomerDto("C4", "Koramangala Commercial Hub", 12.9352, 77.6245, 15.0, "MEDIUM", 10.0, 560.0, 750.0),
                new CustomerDto("C5", "Electronic City Phase 1", 12.8452, 77.6602, 35.0, "LOW", 15.0, 520.0, 700.0),
                new CustomerDto("C6", "Indiranagar 100ft Road", 12.9784, 77.6408, 15.0, "LOW", 10.0, 480.0, 640.0),
                new CustomerDto("C7", "Jayanagar 4th Block", 12.9308, 77.5838, 20.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C8", "Marathahalli Junction Hub", 12.9591, 77.6974, 20.0, "MEDIUM", 10.0, 500.0, 690.0),
                new CustomerDto("C9", "Yeshwanthpur APMC Yard", 13.0238, 77.5489, 25.0, "MEDIUM", 10.0, 480.0, 660.0),
                new CustomerDto("C10", "Hebbal Flyover Logistics", 13.0358, 77.5970, 15.0, "LOW", 10.0, 510.0, 700.0)
        );
        CITY_DATASETS.put("bengaluru", new CityDataset(blrInfo, blrDepots, blrVehicles, blrCustomers));

        // 2. Hyderabad, Telangana
        CityInfo hydInfo = new CityInfo("hyderabad", "Hyderabad", "Telangana", 17.3850, 78.4867, 11, "Cyberabad Tech & Logistics Zone");
        List<DepotDto> hydDepots = Arrays.asList(
                new DepotDto("D1", "Kukatpally Logistics Hub", 17.4849, 78.4138),
                new DepotDto("D2", "Shamshabad Cargo Airport Depot", 17.2505, 78.4312)
        );
        List<VehicleDto> hydVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> hydCustomers = Arrays.asList(
                new CustomerDto("C1", "HITEC City Cyber Towers", 17.4474, 78.3762, 25.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Gachibowli Financial District", 17.4401, 78.3489, 20.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Madhapur IT Hub", 17.4483, 78.3915, 15.0, "HIGH", 10.0, 490.0, 650.0),
                new CustomerDto("C4", "Banjara Hills Road 12", 17.4156, 78.4350, 20.0, "LOW", 10.0, 520.0, 700.0),
                new CustomerDto("C5", "Secunderabad Station Hub", 17.4399, 78.4983, 30.0, "HIGH", 15.0, 480.0, 660.0),
                new CustomerDto("C6", "Kondapur Botanical Garden", 17.4667, 78.3667, 15.0, "MEDIUM", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Jubilee Hills Check Post", 17.4319, 78.4073, 20.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "LB Nagar Ring Road", 17.3457, 78.5522, 25.0, "MEDIUM", 15.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("hyderabad", new CityDataset(hydInfo, hydDepots, hydVehicles, hydCustomers));

        // 3. Mumbai, Maharashtra
        CityInfo mumInfo = new CityInfo("mumbai", "Mumbai", "Maharashtra", 19.0760, 72.8777, 11, "Financial Capital & Port Logistics Hub");
        List<DepotDto> mumDepots = Arrays.asList(
                new DepotDto("D1", "Bhiwandi Major Warehousing Hub", 19.2967, 73.0631),
                new DepotDto("D2", "Andheri MIDC Logistics Depot", 19.1136, 72.8697)
        );
        List<VehicleDto> mumVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> mumCustomers = Arrays.asList(
                new CustomerDto("C1", "Bandra Kurla Complex (BKC)", 19.0657, 72.8687, 30.0, "HIGH", 15.0, 480.0, 660.0),
                new CustomerDto("C2", "Powai Hiranandani Business Park", 19.1176, 72.9060, 20.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Andheri East SEEPZ Hub", 19.1155, 72.8752, 25.0, "HIGH", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Thane West Commercial Center", 19.2183, 72.9781, 20.0, "LOW", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Navi Mumbai Vashi APMC", 19.0771, 72.9986, 35.0, "HIGH", 15.0, 480.0, 650.0),
                new CustomerDto("C6", "Lower Parel Mill Compound", 18.9953, 72.8306, 15.0, "MEDIUM", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Dadar Commercial Junction", 19.0178, 72.8478, 20.0, "HIGH", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Worli Sea Face Hub", 19.0166, 72.8167, 15.0, "LOW", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("mumbai", new CityDataset(mumInfo, mumDepots, mumVehicles, mumCustomers));

        // 4. Delhi NCR
        CityInfo delInfo = new CityInfo("delhi", "Delhi NCR", "Delhi", 28.6139, 77.2090, 11, "National Capital Region Freight Corridor");
        List<DepotDto> delDepots = Arrays.asList(
                new DepotDto("D1", "Okhla Industrial Area Phase 3", 28.5308, 77.2711),
                new DepotDto("D2", "Gurugram Udyog Vihar Hub", 28.4595, 77.0266)
        );
        List<VehicleDto> delVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> delCustomers = Arrays.asList(
                new CustomerDto("C1", "Connaught Place Inner Circle", 28.6315, 77.2167, 25.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Noida Sector 62 Electronic City", 28.6280, 77.3649, 30.0, "HIGH", 15.0, 500.0, 680.0),
                new CustomerDto("C3", "Gurugram Cyber City Phase 2", 28.4950, 77.0895, 20.0, "MEDIUM", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Dwarka Sector 10 Hub", 28.5823, 77.0500, 15.0, "LOW", 10.0, 520.0, 700.0),
                new CustomerDto("C5", "Rohini Sector 7 Commercial", 28.7495, 77.0565, 25.0, "MEDIUM", 15.0, 480.0, 650.0),
                new CustomerDto("C6", "Saket District Centre", 28.5244, 77.2177, 20.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Vasant Kunj Promenade", 28.5293, 77.1524, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Karol Bagh Market", 28.6514, 77.1907, 25.0, "HIGH", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("delhi", new CityDataset(delInfo, delDepots, delVehicles, delCustomers));

        // 5. Chennai, Tamil Nadu
        CityInfo chnInfo = new CityInfo("chennai", "Chennai", "Tamil Nadu", 13.0827, 80.2707, 11, "Automobile & Port Logistics Gateway");
        List<DepotDto> chnDepots = Arrays.asList(
                new DepotDto("D1", "Ambattur Industrial Estate Depot", 13.1143, 80.1548),
                new DepotDto("D2", "Guindy Industrial Logistics Hub", 13.0067, 80.2025)
        );
        List<VehicleDto> chnVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> chnCustomers = Arrays.asList(
                new CustomerDto("C1", "T Nagar Commercial District", 13.0418, 80.2341, 25.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "OMR Sholinganallur Tech Corridor", 12.9010, 80.2279, 30.0, "HIGH", 15.0, 500.0, 680.0),
                new CustomerDto("C3", "Anna Nagar West Commercial", 13.0850, 80.2101, 20.0, "MEDIUM", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Adyar Gandhi Nagar", 13.0012, 80.2565, 15.0, "LOW", 10.0, 520.0, 700.0),
                new CustomerDto("C5", "Velachery Main Road Hub", 12.9759, 80.2212, 20.0, "HIGH", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Tambaram Freight Junction", 12.9249, 80.1000, 25.0, "MEDIUM", 15.0, 540.0, 720.0),
                new CustomerDto("C7", "Porur Junction Business Hub", 13.0382, 80.1565, 20.0, "MEDIUM", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Alwarpet TTK Road", 13.0334, 80.2520, 15.0, "LOW", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("chennai", new CityDataset(chnInfo, chnDepots, chnVehicles, chnCustomers));

        // 6. Pune, Maharashtra
        CityInfo punInfo = new CityInfo("pune", "Pune", "Maharashtra", 18.5204, 73.8567, 11, "Auto Manufacturing & Tech Logistics Hub");
        List<DepotDto> punDepots = Arrays.asList(
                new DepotDto("D1", "Chakan MIDC Logistics Park", 18.7606, 73.8617),
                new DepotDto("D2", "Hinjewadi Phase 2 Depot", 18.5913, 73.7389)
        );
        List<VehicleDto> punVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> punCustomers = Arrays.asList(
                new CustomerDto("C1", "Hinjewadi Infotech Park Phase 1", 18.5971, 73.7188, 30.0, "HIGH", 15.0, 480.0, 660.0),
                new CustomerDto("C2", "Baner Commercial High Street", 18.5590, 73.7868, 20.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Kharadi EON Free Zone", 18.5516, 73.9348, 25.0, "HIGH", 15.0, 490.0, 670.0),
                new CustomerDto("C4", "Viman Nagar Business Hub", 18.5679, 73.9143, 15.0, "LOW", 10.0, 520.0, 700.0),
                new CustomerDto("C5", "Hadapsar Magarpatta City", 18.5158, 73.9272, 25.0, "HIGH", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Pimpri Industrial Cluster", 18.6298, 73.7997, 30.0, "MEDIUM", 15.0, 540.0, 720.0),
                new CustomerDto("C7", "Wakad Express Highway Junction", 18.5987, 73.7686, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Kothrud Paud Road", 18.5074, 73.8077, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("pune", new CityDataset(punInfo, punDepots, punVehicles, punCustomers));

        // 7. Kolkata, West Bengal
        CityInfo kolInfo = new CityInfo("kolkata", "Kolkata", "West Bengal", 22.5726, 88.3639, 11, "Eastern India Commercial Hub");
        List<DepotDto> kolDepots = Arrays.asList(
                new DepotDto("D1", "Dankuni Logistics Park", 22.6841, 88.2917),
                new DepotDto("D2", "Taratala Industrial Area Hub", 22.5070, 88.3140)
        );
        List<VehicleDto> kolVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> kolCustomers = Arrays.asList(
                new CustomerDto("C1", "Salt Lake Sector V IT Hub", 22.5804, 88.4378, 30.0, "HIGH", 15.0, 480.0, 660.0),
                new CustomerDto("C2", "New Town Rajarhat Business Zone", 22.5867, 88.4754, 25.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Park Street Commercial District", 22.5519, 88.3524, 20.0, "HIGH", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Howrah Freight Terminal", 22.5958, 88.2636, 35.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Ballygunge Circular Road", 22.5280, 88.3659, 15.0, "LOW", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Gariahat Crossing Hub", 22.5186, 88.3683, 20.0, "MEDIUM", 10.0, 540.0, 720.0)
        );
        CITY_DATASETS.put("kolkata", new CityDataset(kolInfo, kolDepots, kolVehicles, kolCustomers));

        // 8. Ahmedabad, Gujarat
        CityInfo ahmInfo = new CityInfo("ahmedabad", "Ahmedabad", "Gujarat", 23.0225, 72.5714, 11, "Textile & Industrial Freight Center");
        List<DepotDto> ahmDepots = Arrays.asList(
                new DepotDto("D1", "Sanand GIDC Logistics Park", 22.9868, 72.3813),
                new DepotDto("D2", "Changodar Industrial Area Hub", 22.9234, 72.4412)
        );
        List<VehicleDto> ahmVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> ahmCustomers = Arrays.asList(
                new CustomerDto("C1", "SG Highway Corporate Zone", 23.0387, 72.5119, 30.0, "HIGH", 15.0, 480.0, 660.0),
                new CustomerDto("C2", "Prahlad Nagar Commercial Hub", 23.0125, 72.5108, 25.0, "HIGH", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Navrangpura Commercial Complex", 23.0365, 72.5611, 20.0, "MEDIUM", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Maninagar Market", 22.9967, 72.6019, 20.0, "LOW", 10.0, 520.0, 700.0),
                new CustomerDto("C5", "Bopal Ring Road Junction", 23.0336, 72.4634, 15.0, "LOW", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Satellite Commercial Corridor", 23.0289, 72.5298, 25.0, "MEDIUM", 10.0, 540.0, 720.0)
        );
        CITY_DATASETS.put("ahmedabad", new CityDataset(ahmInfo, ahmDepots, ahmVehicles, ahmCustomers));

        // 9. Jaipur, Rajasthan
        CityInfo jaiInfo = new CityInfo("jaipur", "Jaipur", "Rajasthan", 26.9124, 75.7873, 11, "Pink City Industrial Logistics Gateway");
        List<DepotDto> jaiDepots = Arrays.asList(
                new DepotDto("D1", "VKIA Vishwakarma Industrial Area", 26.9855, 75.7689),
                new DepotDto("D2", "Sitapura Industrial Area Hub", 26.7820, 75.8456)
        );
        List<VehicleDto> jaiVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> jaiCustomers = Arrays.asList(
                new CustomerDto("C1", "Malviya Nagar World Trade Park", 26.8530, 75.8050, 25.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Vaishali Nagar Commercial Belt", 26.9067, 75.7389, 20.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Mansarovar Commercial Zone", 26.8580, 75.7640, 25.0, "HIGH", 15.0, 490.0, 670.0),
                new CustomerDto("C4", "C-Scheme Central Business Hub", 26.9114, 75.8023, 15.0, "HIGH", 10.0, 520.0, 700.0),
                new CustomerDto("C5", "Raja Park Commercial Market", 26.8972, 75.8286, 20.0, "LOW", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Tonk Road Freight Corridor", 26.8647, 75.8000, 25.0, "MEDIUM", 10.0, 540.0, 720.0)
        );
        CITY_DATASETS.put("jaipur", new CityDataset(jaiInfo, jaiDepots, jaiVehicles, jaiCustomers));

        // 10. Kochi, Kerala
        CityInfo kocInfo = new CityInfo("kochi", "Kochi", "Kerala", 9.9312, 76.2673, 11, "Port City & Coastal IT Logistics Corridor");
        List<DepotDto> kocDepots = Arrays.asList(
                new DepotDto("D1", "Kalamassery Industrial Hub", 10.0543, 76.3218),
                new DepotDto("D2", "Willingdon Island Port Depot", 9.9487, 76.2736)
        );
        List<VehicleDto> kocVehicles = Arrays.asList(
                new VehicleDto("V1", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V2", 80.0, "D1", 0.12, 10.0),
                new VehicleDto("V3", 90.0, "D2", 0.12, 10.0)
        );
        List<CustomerDto> kocCustomers = Arrays.asList(
                new CustomerDto("C1", "Kakkanad InfoPark SmartCity", 10.0104, 76.3638, 30.0, "HIGH", 15.0, 480.0, 660.0),
                new CustomerDto("C2", "MG Road Commercial Corridor", 9.9723, 76.2831, 20.0, "HIGH", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Edappally LuLu Commercial Zone", 10.0261, 76.3082, 25.0, "MEDIUM", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Marine Drive High Street", 9.9816, 76.2763, 15.0, "LOW", 10.0, 520.0, 700.0),
                new CustomerDto("C5", "Aluva Industrial Area", 10.1076, 76.3516, 25.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Vytilla Mobility Hub", 9.9676, 76.3195, 20.0, "HIGH", 10.0, 540.0, 720.0)
        );
        CITY_DATASETS.put("kochi", new CityDataset(kocInfo, kocDepots, kocVehicles, kocCustomers));

        // 11. Visakhapatnam, Andhra Pradesh
        CityInfo vskInfo = new CityInfo("visakhapatnam", "Visakhapatnam", "Andhra Pradesh", 17.6868, 83.2185, 11, "Port City Logistics Hub");
        List<DepotDto> vskDepots = Arrays.asList(new DepotDto("D1", "Gajuwaka Industrial Hub", 17.6256, 83.1833));
        List<VehicleDto> vskVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> vskCustomers = Arrays.asList(
                new CustomerDto("C1", "Maddilapalem Area", 17.7333, 83.3167, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "MVP Colony", 17.7408, 83.3364, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Seethammadhara", 17.7441, 83.3116, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "NAD Junction", 17.7483, 83.2389, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Gopalapatnam", 17.7554, 83.2197, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Siripuram", 17.7233, 83.3166, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Kancharapalem", 17.7380, 83.2721, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Kurmannapalem", 17.6534, 83.1517, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("visakhapatnam", new CityDataset(vskInfo, vskDepots, vskVehicles, vskCustomers));

        // 12. Surat, Gujarat
        CityInfo srtInfo = new CityInfo("surat", "Surat", "Gujarat", 21.1702, 72.8311, 11, "Diamond City Transport Hub");
        List<DepotDto> srtDepots = Arrays.asList(new DepotDto("D1", "Udhna Industrial Hub", 21.1578, 72.8407));
        List<VehicleDto> srtVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> srtCustomers = Arrays.asList(
                new CustomerDto("C1", "Adajan Area", 21.1963, 72.7933, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Piplod", 21.1601, 72.7663, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Vesu", 21.1394, 72.7766, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Varachha", 21.2163, 72.8596, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Katargam", 21.2227, 72.8251, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Ring Road Hub", 21.1894, 72.8291, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Athwa Lines", 21.1818, 72.8058, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Palsana", 21.0858, 72.9754, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("surat", new CityDataset(srtInfo, srtDepots, srtVehicles, srtCustomers));

        // 13. Lucknow, Uttar Pradesh
        CityInfo lkoInfo = new CityInfo("lucknow", "Lucknow", "Uttar Pradesh", 26.8467, 80.9462, 11, "Nawab City Trade Route");
        List<DepotDto> lkoDepots = Arrays.asList(new DepotDto("D1", "Transport Nagar Depot", 26.7725, 80.8718));
        List<VehicleDto> lkoVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> lkoCustomers = Arrays.asList(
                new CustomerDto("C1", "Gomti Nagar", 26.8488, 80.9996, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Alambagh", 26.8043, 80.9009, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Hazratganj", 26.8505, 80.9439, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Indira Nagar", 26.8776, 80.9950, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Chowk Area", 26.8660, 80.9095, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Aashiana", 26.7828, 80.9157, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Mahanagar", 26.8775, 80.9575, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Kapoorthala", 26.8835, 80.9416, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("lucknow", new CityDataset(lkoInfo, lkoDepots, lkoVehicles, lkoCustomers));

        // 14. Indore, Madhya Pradesh
        CityInfo indInfo = new CityInfo("indore", "Indore", "Madhya Pradesh", 22.7196, 75.8577, 11, "Cleanest City Distribution Hub");
        List<DepotDto> indDepots = Arrays.asList(new DepotDto("D1", "Pithampur Logistics Park", 22.6214, 75.6791));
        List<VehicleDto> indVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> indCustomers = Arrays.asList(
                new CustomerDto("C1", "Vijay Nagar", 22.7533, 75.8937, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Palasia", 22.7237, 75.8893, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Rajwada Market", 22.7185, 75.8550, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Bhawarkuan", 22.6953, 75.8711, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Sudama Nagar", 22.6908, 75.8361, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Annapurna", 22.6976, 75.8335, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Saket Nagar", 22.7258, 75.8973, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Mahalaxmi Nagar", 22.7663, 75.9080, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("indore", new CityDataset(indInfo, indDepots, indVehicles, indCustomers));

        // 15. Nagpur, Maharashtra
        CityInfo nagInfo = new CityInfo("nagpur", "Nagpur", "Maharashtra", 21.1458, 79.0882, 11, "Zero Mile Freight Center");
        List<DepotDto> nagDepots = Arrays.asList(new DepotDto("D1", "MIHAN SEZ Hub", 21.0267, 79.0287));
        List<VehicleDto> nagVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> nagCustomers = Arrays.asList(
                new CustomerDto("C1", "Dharampeth", 21.1396, 79.0601, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Sitabuldi", 21.1424, 79.0827, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Sadar", 21.1611, 79.0805, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Wardhaman Nagar", 21.1436, 79.1354, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Manish Nagar", 21.0924, 79.0664, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Hingna Road", 21.1090, 78.9959, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Mahal", 21.1448, 79.1066, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Pratap Nagar", 21.1190, 79.0531, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("nagpur", new CityDataset(nagInfo, nagDepots, nagVehicles, nagCustomers));

        // 16. Coimbatore, Tamil Nadu
        CityInfo cbeInfo = new CityInfo("coimbatore", "Coimbatore", "Tamil Nadu", 11.0168, 76.9558, 11, "Manchester of South India");
        List<DepotDto> cbeDepots = Arrays.asList(new DepotDto("D1", "Peelamedu Industrial Depot", 11.0267, 77.0090));
        List<VehicleDto> cbeVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> cbeCustomers = Arrays.asList(
                new CustomerDto("C1", "RS Puram", 11.0084, 76.9482, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Gandhipuram", 11.0180, 76.9664, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Saibaba Colony", 11.0276, 76.9427, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Singanallur", 10.9997, 77.0270, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Saravanampatti", 11.0797, 76.9986, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Vadavalli", 11.0336, 76.9103, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Town Hall", 10.9983, 76.9602, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Kovaipudur", 10.9238, 76.9406, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("coimbatore", new CityDataset(cbeInfo, cbeDepots, cbeVehicles, cbeCustomers));

        // 17. Bhubaneswar, Odisha
        CityInfo bbsrInfo = new CityInfo("bhubaneswar", "Bhubaneswar", "Odisha", 20.2961, 85.8245, 11, "Temple City Regional Hub");
        List<DepotDto> bbsrDepots = Arrays.asList(new DepotDto("D1", "Chandaka Industrial Area", 20.3533, 85.8021));
        List<VehicleDto> bbsrVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> bbsrCustomers = Arrays.asList(
                new CustomerDto("C1", "Jayadev Vihar", 20.3010, 85.8197, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Saheed Nagar", 20.2917, 85.8451, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Patia", 20.3470, 85.8164, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Khandagiri", 20.2598, 85.7876, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Nayapalli", 20.2878, 85.8082, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Old Town", 20.2404, 85.8335, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Unit 9", 20.2905, 85.8339, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Rasulgarh", 20.2989, 85.8569, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("bhubaneswar", new CityDataset(bbsrInfo, bbsrDepots, bbsrVehicles, bbsrCustomers));

        // 18. Chandigarh
        CityInfo chdInfo = new CityInfo("chandigarh", "Chandigarh", "Chandigarh", 30.7333, 76.7794, 11, "Planned City Transport Hub");
        List<DepotDto> chdDepots = Arrays.asList(new DepotDto("D1", "Industrial Area Phase 1", 30.7061, 76.8016));
        List<VehicleDto> chdVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> chdCustomers = Arrays.asList(
                new CustomerDto("C1", "Sector 17 Plaza", 30.7398, 76.7827, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Sector 22 Market", 30.7347, 76.7645, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Sector 35", 30.7259, 76.7554, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Mani Majra", 30.7183, 76.8410, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Sector 43 ISBT", 30.7214, 76.7454, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Sector 9", 30.7503, 76.7909, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Sector 8", 30.7441, 76.7941, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Zirakpur Hub", 30.6393, 76.8219, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("chandigarh", new CityDataset(chdInfo, chdDepots, chdVehicles, chdCustomers));

        // 19. Mysuru, Karnataka
        CityInfo mysInfo = new CityInfo("mysuru", "Mysuru", "Karnataka", 12.2958, 76.6394, 11, "Heritage City Delivery Belt");
        List<DepotDto> mysDepots = Arrays.asList(new DepotDto("D1", "Hebbal Industrial Estate", 12.3571, 76.6115));
        List<VehicleDto> mysVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> mysCustomers = Arrays.asList(
                new CustomerDto("C1", "Gokulam", 12.3323, 76.6268, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Kuvempunagar", 12.2741, 76.6277, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Jayalakshmipuram", 12.3213, 76.6341, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Siddhartha Layout", 12.3168, 76.6800, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "Vidyaranyapuram", 12.2828, 76.6457, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Vijayanagar", 12.3275, 76.6083, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Bannimantap", 12.3392, 76.6579, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Nazarbad", 12.3146, 76.6669, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("mysuru", new CityDataset(mysInfo, mysDepots, mysVehicles, mysCustomers));

        // 20. Vijayawada, Andhra Pradesh
        CityInfo vijInfo = new CityInfo("vijayawada", "Vijayawada", "Andhra Pradesh", 16.5062, 80.6480, 11, "Business Capital Hub");
        List<DepotDto> vijDepots = Arrays.asList(new DepotDto("D1", "Autonagar Logistics Depot", 16.4862, 80.6725));
        List<VehicleDto> vijVehicles = Arrays.asList(new VehicleDto("V1", 80.0, "D1", 0.12, 10.0), new VehicleDto("V2", 90.0, "D1", 0.12, 10.0));
        List<CustomerDto> vijCustomers = Arrays.asList(
                new CustomerDto("C1", "Benz Circle", 16.4975, 80.6558, 20.0, "HIGH", 10.0, 480.0, 660.0),
                new CustomerDto("C2", "Bhavanipuram", 16.5298, 80.5985, 15.0, "MEDIUM", 10.0, 500.0, 680.0),
                new CustomerDto("C3", "Patamata", 16.4913, 80.6698, 25.0, "LOW", 10.0, 490.0, 670.0),
                new CustomerDto("C4", "Governor Peta", 16.5097, 80.6276, 20.0, "HIGH", 15.0, 520.0, 700.0),
                new CustomerDto("C5", "One Town", 16.5146, 80.6138, 10.0, "MEDIUM", 10.0, 480.0, 650.0),
                new CustomerDto("C6", "Gunadala", 16.5204, 80.6664, 30.0, "HIGH", 10.0, 540.0, 720.0),
                new CustomerDto("C7", "Machavaram", 16.5140, 80.6487, 15.0, "LOW", 10.0, 510.0, 690.0),
                new CustomerDto("C8", "Kankipadu", 16.4253, 80.7850, 20.0, "MEDIUM", 10.0, 530.0, 710.0)
        );
        CITY_DATASETS.put("vijayawada", new CityDataset(vijInfo, vijDepots, vijVehicles, vijCustomers));
    }

    public static List<CityInfo> getAllCities() {
        List<CityInfo> list = new ArrayList<>();
        for (CityDataset d : CITY_DATASETS.values()) {
            list.add(d.getInfo());
        }
        return list;
    }

    public static CityDataset getCityDataset(String cityId) {
        if (cityId == null) return CITY_DATASETS.get("bengaluru");
        CityDataset d = CITY_DATASETS.get(cityId.toLowerCase().trim());
        return d != null ? d : CITY_DATASETS.get("bengaluru");
    }

    public static CityDataset getDefaultDataset() {
        return CITY_DATASETS.get("bengaluru");
    }
}
