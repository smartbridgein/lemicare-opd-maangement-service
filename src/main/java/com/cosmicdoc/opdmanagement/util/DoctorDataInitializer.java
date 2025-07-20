package com.cosmicdoc.opdmanagement.util;

import com.cosmicdoc.opdmanagement.model.Doctor;
import com.cosmicdoc.opdmanagement.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility to initialize doctor data in Firestore
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DoctorDataInitializer {

    private final DoctorRepository doctorRepository;

    /**
     * Creates a command line runner that initializes doctor data when the application starts
     * Only runs when the "init-doctors" profile is active
     */
    @Bean
    @Profile("init-doctors")
    public CommandLineRunner initializeDoctors() {
        return args -> {
            log.info("Initializing doctor data...");
            
            List<Doctor> doctorsToAdd = createDoctorsList();
            
            for (Doctor doctor : doctorsToAdd) {
                try {
                    // Check if doctor already exists by email to avoid duplicates
                    boolean exists = doctorRepository.findByEmail(doctor.getEmail()).isPresent();
                    
                    if (!exists) {
                        Doctor savedDoctor = doctorRepository.save(doctor);
                        log.info("Added doctor: {}", savedDoctor.getName());
                    } else {
                        log.info("Doctor already exists: {}", doctor.getName());
                    }
                } catch (Exception e) {
                    log.error("Error adding doctor: {}", doctor.getName(), e);
                }
            }
            
            log.info("Doctor data initialization completed");
        };
    }
    
    /**
     * Creates the list of doctors to be added
     */
    private List<Doctor> createDoctorsList() {
        List<Doctor> doctors = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Dr. R. Amin Hanan
        Doctor drHanan = new Doctor();
        drHanan.setId(UUID.randomUUID().toString());
        drHanan.setName("Dr. R. Amin Hanan");
        drHanan.setEmail("dr.amin.hanan@lemicare.com");
        drHanan.setPhoneNumber("+91-9876543201");
        drHanan.setSpecialization("Dermatology");
        drHanan.setQualification("MBBS., MD.(DVL), FFAM.");
        drHanan.setLicenseNumber("TN-MED-" + UUID.randomUUID().toString().substring(0, 8));
        drHanan.setHospital("Lemicare Clinic");
        drHanan.setAddress("123 Anna Nagar");
        drHanan.setCity("Chennai");
        drHanan.setState("Tamil Nadu");
        drHanan.setCountry("India");
        drHanan.setZipCode("600040");
        drHanan.setAvailable(true);
        drHanan.setProfileImage("https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_hanan.jpg");
        drHanan.setCreatedAt(now);
        drHanan.setUpdatedAt(now);
        doctors.add(drHanan);
        
        // Dr. Mohamed Basith
        Doctor drBasith = new Doctor();
        drBasith.setId(UUID.randomUUID().toString());
        drBasith.setName("Dr. Mohamed Basith");
        drBasith.setEmail("dr.mohamed.basith@lemicare.com");
        drBasith.setPhoneNumber("+91-9876543202");
        drBasith.setSpecialization("Anesthesiology");
        drBasith.setQualification("MBBS., MD., FHT.");
        drBasith.setLicenseNumber("TN-MED-" + UUID.randomUUID().toString().substring(0, 8));
        drBasith.setHospital("Lemicare Clinic");
        drBasith.setAddress("123 Anna Nagar");
        drBasith.setCity("Chennai");
        drBasith.setState("Tamil Nadu");
        drBasith.setCountry("India");
        drBasith.setZipCode("600040");
        drBasith.setAvailable(true);
        drBasith.setProfileImage("https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_basith.jpg");
        drBasith.setCreatedAt(now);
        drBasith.setUpdatedAt(now);
        doctors.add(drBasith);
        
        // Dr. U. SANDEEP
        Doctor drSandeep = new Doctor();
        drSandeep.setId(UUID.randomUUID().toString());
        drSandeep.setName("Dr. U. SANDEEP");
        drSandeep.setEmail("dr.sandeep@lemicare.com");
        drSandeep.setPhoneNumber("+91-9876543203");
        drSandeep.setSpecialization("Plastic Surgery");
        drSandeep.setQualification("MS., DNB., M.Ch., DNB.");
        drSandeep.setLicenseNumber("TN-MED-" + UUID.randomUUID().toString().substring(0, 8));
        drSandeep.setHospital("Lemicare Clinic");
        drSandeep.setAddress("123 Anna Nagar");
        drSandeep.setCity("Chennai");
        drSandeep.setState("Tamil Nadu");
        drSandeep.setCountry("India");
        drSandeep.setZipCode("600040");
        drSandeep.setAvailable(true);
        drSandeep.setProfileImage("https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_sandeep.jpg");
        drSandeep.setCreatedAt(now);
        drSandeep.setUpdatedAt(now);
        doctors.add(drSandeep);
        
        // Dr. Shruthi Janardhanan
        Doctor drShruthi = new Doctor();
        drShruthi.setId(UUID.randomUUID().toString());
        drShruthi.setName("Dr. Shruthi Janardhanan");
        drShruthi.setEmail("dr.shruthi@lemicare.com");
        drShruthi.setPhoneNumber("+91-9876543204");
        drShruthi.setSpecialization("Dermatology");
        drShruthi.setQualification("MD., DNB., MRCP (UK, Dermatology)");
        drShruthi.setLicenseNumber("TN-MED-" + UUID.randomUUID().toString().substring(0, 8));
        drShruthi.setHospital("Lemicare Clinic");
        drShruthi.setAddress("123 Anna Nagar");
        drShruthi.setCity("Chennai");
        drShruthi.setState("Tamil Nadu");
        drShruthi.setCountry("India");
        drShruthi.setZipCode("600040");
        drShruthi.setAvailable(true);
        drShruthi.setProfileImage("https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_shruthi.jpg");
        drShruthi.setCreatedAt(now);
        drShruthi.setUpdatedAt(now);
        doctors.add(drShruthi);
        
        return doctors;
    }
}
