package com.github.farzan6118.petclinic.common.persistence;

import com.github.farzan6118.petclinic.appointment.model.DurationTemplate;
import com.github.farzan6118.petclinic.appointment.repository.DurationTemplateRepository;
import com.github.farzan6118.petclinic.clinic.model.Clinic;
import com.github.farzan6118.petclinic.clinic.model.Room;
import com.github.farzan6118.petclinic.clinic.model.RoomType;
import com.github.farzan6118.petclinic.clinic.repository.ClinicRepository;
import com.github.farzan6118.petclinic.clinic.repository.RoomRepository;
import com.github.farzan6118.petclinic.clinic.repository.RoomTypeRepository;
import com.github.farzan6118.petclinic.clinic.service.ClinicService;
import com.github.farzan6118.petclinic.common.enums.Sex;
import com.github.farzan6118.petclinic.common.valueobject.DateTimeRange;
import com.github.farzan6118.petclinic.owner.model.Owner;
import com.github.farzan6118.petclinic.owner.repository.OwnerRepository;
import com.github.farzan6118.petclinic.person.model.Address;
import com.github.farzan6118.petclinic.person.model.Person;
import com.github.farzan6118.petclinic.person.model.Profile;
import com.github.farzan6118.petclinic.pet.model.Pet;
import com.github.farzan6118.petclinic.pet.model.Species;
import com.github.farzan6118.petclinic.pet.repository.PetRepository;
import com.github.farzan6118.petclinic.pet.repository.SpeciesRepository;
import com.github.farzan6118.petclinic.vet.model.Vet;
import com.github.farzan6118.petclinic.vet.model.VetAvailability;
import com.github.farzan6118.petclinic.vet.repository.VetAvailabilityRepository;
import com.github.farzan6118.petclinic.vet.repository.VetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FillInitialRecords implements CommandLineRunner {

    private final DurationTemplateRepository durationTemplateRepository;
    private final VetAvailabilityRepository vetAvailabilityRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final SpeciesRepository speciesRepository;
    private final ClinicRepository clinicRepository;
    private final OwnerRepository ownerRepository;
    private final RoomRepository roomRepository;
    private final PetRepository petRepository;
    private final VetRepository vetRepository;
    private final ClinicService clinicService;

    @Override
    @Transactional
    public void run(String... args) {
        seedDurationTemplates();
        seedSpecies();
        seedClinic();
        seedRoomTypes();
        seedOwners();
        seedVets();
        seedPets();
        seedRooms();
        seedVetAvailability();
    }

    private void seedDurationTemplates() {
        if (durationTemplateRepository.count() != 0) return;
        durationTemplateRepository.saveAll(List.of(
                durationTemplate("QUICK", 15, "Quick visit"),
                durationTemplate("SHORT", 20, "Short visit"),
                durationTemplate("STANDARD", 30, "Standard visit"),
                durationTemplate("EXTENDED", 45, "Extended visit"),
                durationTemplate("LONG", 60, "Long visit"),
                durationTemplate("VERY_LONG", 120, "Very long visit")
        ));
    }

    private void seedSpecies() {
        if (speciesRepository.count() != 0) return;
        speciesRepository.saveAll(List.of(
                species("Dog", "DOG", "Canis lupus familiaris", "Common companion animal."),
                species("Cat", "CAT", "Felis catus", "Common companion animal."),
                species("Rabbit", "RABBIT", "Oryctolagus cuniculus", "Common companion animal."),
                species("Hamster", "HAMSTER", "Cricetinae", "Small companion animal.")
        ));
    }

    private void seedClinic() {
        if (clinicRepository.count() != 0) return;
        Clinic clinic = new Clinic();
        clinic.setCode(1);
        clinic.setName("Main Clinic");
        clinic.setAddress(address("Main clinic", "Berlin", "Berlin", "Afrikanische Str.",
                1, "12A", 52.52D, 13.4D));
        clinic.setActive(true);
        clinicRepository.save(clinic);
    }

    private void seedRoomTypes() {
        if (roomTypeRepository.count() != 0) return;
        roomTypeRepository.saveAll(List.of(
                roomType("examination", "Routine examinations, consultations, and follow-up visits."),
                roomType("treatment", "Non-surgical procedures and patient care."),
                roomType("surgery", "Surgical procedures and invasive interventions."),
                roomType("dental", "Dental examinations and oral procedures."),
                roomType("imaging", "X-ray, ultrasound, and diagnostic imaging."),
                roomType("physiotherapy", "Rehabilitation and mobility exercises."),
                roomType("individual", "Private room for individual consultations."),
                roomType("isolation", "Infection-control isolation room.")
        ));
    }

    private void seedOwners() {
        if (ownerRepository.count() != 0) return;
        ownerRepository.saveAll(List.of(
                owner("Ms.", "Mina", "Rahimi", "200000001", LocalDate.of(1990, 4, 12),
                        "09120000001", "mina.rahimi@example.com", "Berlin", "Valiasr Street"),
                owner("Mr.", "Arman", "Karimi", "200000002", LocalDate.of(1987, 9, 25),
                        "09120000002", "arman.karimi@example.com", "Hamburg", "Zand Street"),
                owner("Ms.", "Niloofar", "Ahmadi", "200000003", LocalDate.of(1995, 1, 8),
                        "09120000003", "niloofar.ahmadi@example.com", "Dusseldorf", "Shahrivar Street")
        ));
    }

    private void seedVets() {
        if (vetRepository.count() != 0) return;
        Clinic clinic = clinicService.getFirstByActive();
        vetRepository.saveAll(List.of(
                vet("Sara", "Moradi", "100000001",
                        LocalDate.of(1985, 3, 18),
                        "09210000001", "sara.moradi@example.com",
                        "Berlin", "Mirdamad Boulevard", clinic),
                vet("Reza", "Hosseini", "100000002",
                        LocalDate.of(1982, 11, 2),
                        "09210000002", "reza.hosseini@example.com",
                        "Hamburg", "Maaliabad Street", clinic),
                vet("Parisa", "Etemadi", "100000003",
                        LocalDate.of(1990, 6, 27),
                        "09210000003", "parisa.etemadi@example.com",
                        "Dusseldorf", "Ferdowsi Street", clinic)
        ));
    }

    private void seedPets() {
        if (petRepository.count() != 0) return;
        List<Owner> owners = ownerRepository.findAll();
        List<Species> species = speciesRepository.findAll();
        if (owners.size() < 3 || species.size() < 3) return;
        petRepository.saveAll(List.of(
                pet("Luna", "White", "Small black mark", Sex.FEMALE, species.get(0), owners.get(0), LocalDate.of(2021, 5, 12)),
                pet("Milo", "Orange", "White paws", Sex.MALE, species.get(1), owners.get(1), LocalDate.of(2022, 2, 8)),
                pet("Coco", "Brown", "Long ears", Sex.FEMALE, species.get(2), owners.get(2), LocalDate.of(2023, 7, 21))
        ));
    }

    private void seedRooms() {
        if (roomRepository.count() != 0) return;
        List<Clinic> clinics = clinicRepository.findAll();
        List<RoomType> types = roomTypeRepository.findAll();
        if (clinics.isEmpty() || types.isEmpty()) return;
        Clinic clinic = clinics.getFirst();
        RoomType examination = findRoomType(types, "examination");
        RoomType treatment = findRoomType(types, "treatment");
        RoomType surgery = findRoomType(types, "surgery");
        roomRepository.saveAll(List.of(
                room("Examination Room 1", "EXAM-01", examination, clinic),
                room("Examination Room 2", "EXAM-02", examination, clinic),
                room("Treatment Room 1", "TREAT-01", treatment, clinic),
                room("Surgery Room 1", "SURG-01", surgery, clinic)
        ));
    }

    private void seedVetAvailability() {
        if (vetAvailabilityRepository.count() != 0) return;
        List<Vet> vets = vetRepository.findAll();
        if (vets.isEmpty()) return;
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfter = tomorrow.plusDays(1);
        vetAvailabilityRepository.saveAll(List.of(
                availability(vets.get(0), tomorrow.atTime(9, 0), tomorrow.atTime(13, 0)),
                availability(vets.get(0), dayAfter.atTime(9, 0), dayAfter.atTime(12, 20))
        ));
        if (vets.size() > 1) {
            vetAvailabilityRepository.saveAll(List.of(
                    availability(vets.get(1), tomorrow.atTime(10, 0), tomorrow.atTime(14, 0)),
                    availability(vets.get(1), dayAfter.atTime(9, 30), dayAfter.atTime(12, 30))
            ));
        }
        if (vets.size() > 2) {
            vetAvailabilityRepository.saveAll(List.of(
                    availability(vets.get(2), tomorrow.atTime(8, 0), tomorrow.atTime(12, 0)),
                    availability(vets.get(2), dayAfter.atTime(9, 30), dayAfter.atTime(14, 0))
            ));
        }
    }

    private DurationTemplate durationTemplate(String name, int minutes, String description) {
        DurationTemplate template = new DurationTemplate();
        template.setName(name);
        template.setDurationMinutes(minutes);
        template.setDescription(description);
        return template;
    }

    private Species species(String name, String code, String origin, String description) {
        Species species = new Species();
        species.setName(name);
        species.setCode(code);
        species.setOrigin(origin);
        species.setDescription(description);
        return species;
    }

    private Owner owner(String title, String firstName, String lastName, String nationalId,
                        LocalDate birthDate, String mobile, String email, String city, String street) {
        Person person = person(title, firstName, lastName, nationalId,
                profile(email, mobile, birthDate), address("Home", city, city, street,
                        2, "13B", 32.54D, 23.45D));
        Owner owner = new Owner();
        owner.setPerson(person);
        return owner;
    }

    private Vet vet(String firstName, String lastName, String nationalId,
                    LocalDate birthDate, String mobile, String email, String city, String street, Clinic clinic) {
        Vet vet = new Vet();
        vet.setPerson(person("Dr.", firstName, lastName, nationalId,
                profile(email, mobile, birthDate), address("Home", city, city, street,
                        3, "13B", 32.54D, 23.45D)));
        vet.setClinic(clinic);
        return vet;
    }

    private Person person(String title, String firstName, String lastName, String nationalId,
                          Profile profile, Address address) {
        Person person = new Person();
        person.setTitle(title);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setNationalId(nationalId);
        person.setProfile(profile);
        person.setAddress(address);
        return person;
    }

    private Profile profile(String email, String mobile, LocalDate birthDate) {
        Profile profile = new Profile();
        profile.setEmail(email);
        profile.setMobileNumber(mobile);
        profile.setBirthDate(birthDate);
        return profile;
    }

    private Address address(String title, String province, String city,
                            String street, Integer floor, String unitNumber,
                            double latitude, double longitude) {
        Address address = new Address();
        address.setTitle(title);
        address.setCountryName("Germany");
        address.setProvinceName(province);
        address.setCityName(city);
        address.setBuildingNumber("1");
        address.setAddress(street);
        address.setFloor(floor);
        address.setUnitNumber(unitNumber);
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        address.setPostalCode("2478299468");
        address.setDefaultAddress(true);
        return address;
    }

    private Pet pet(String name, String color, String marks, Sex sex, Species species,
                    Owner owner, LocalDate birthDate) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setColor(color);
        pet.setMarks(marks);
        pet.setSex(sex);
        pet.setSpecies(species);
        pet.setOwner(owner);
        pet.setBirthDate(birthDate);
        return pet;
    }

    private RoomType roomType(String name, String description) {
        RoomType roomType = new RoomType();
        roomType.setName(name);
        roomType.setDescription(description);
        return roomType;
    }

    private Room room(String name, String number, RoomType roomType, Clinic clinic) {
        Room room = new Room();
        room.setName(name);
        room.setRoomNumber(number);
        room.setRoomType(roomType);
        room.setClinic(clinic);
        room.setActive(true);
        return room;
    }

    private RoomType findRoomType(List<RoomType> types, String name) {
        return types.stream()
                .filter(type -> type.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Required room type is missing: " + name));
    }

    private VetAvailability availability(Vet vet, LocalDateTime start, LocalDateTime end) {
        VetAvailability availability = new VetAvailability();
        availability.setVet(vet);
        availability.setTimeRange(new DateTimeRange(start, end));
        availability.setActive(true);
        return availability;
    }
}
