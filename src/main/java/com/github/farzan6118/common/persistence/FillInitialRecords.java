package com.github.farzan6118.common.persistence;

import com.github.farzan6118.appointment.model.DurationTemplate;
import com.github.farzan6118.appointment.repository.DurationTemplateRepository;
import com.github.farzan6118.clinic.model.Building;
import com.github.farzan6118.clinic.model.Room;
import com.github.farzan6118.clinic.model.RoomType;
import com.github.farzan6118.clinic.repository.ClinicRepository;
import com.github.farzan6118.clinic.repository.RoomRepository;
import com.github.farzan6118.clinic.repository.RoomTypeRepository;
import com.github.farzan6118.clinic.service.BuildingService;
import com.github.farzan6118.common.enums.Sex;
import com.github.farzan6118.common.valueobject.DateTimeRange;
import com.github.farzan6118.owner.model.Owner;
import com.github.farzan6118.owner.repository.OwnerRepository;
import com.github.farzan6118.person.model.Address;
import com.github.farzan6118.person.model.Contact;
import com.github.farzan6118.person.model.Person;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.pet.model.Species;
import com.github.farzan6118.pet.repository.PetRepository;
import com.github.farzan6118.pet.repository.SpeciesRepository;
import com.github.farzan6118.vet.model.Vet;
import com.github.farzan6118.vet.model.VetAvailability;
import com.github.farzan6118.vet.repository.VetAvailabilityRepository;
import com.github.farzan6118.vet.repository.VetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final BuildingService buildingService;

    @Override
    @Transactional
    public void run(String... args) {
        seedDurationTemplates();
        seedSpecies();
        seedBuilding();
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

    private void seedBuilding() {
        if (clinicRepository.count() != 0) return;
        Building building = new Building();
        building.setCode(1);
        building.setName("Main Building");
        building.setAddress(address("Main building", "Berlin", "Berlin", "Afrikaner Str.",
                1, "12A", 52.52D, 13.4D));
        building.setActive(true);
        clinicRepository.save(building);
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
                        "09120000001", "mina.rahimi@example.com", "Berlin", "Vali Street"),
                owner("Mr.", "Arman", "Karimi", "200000002", LocalDate.of(1987, 9, 25),
                        "09120000002", "arman.karimi@example.com", "Hamburg", "Zara Street"),
                owner("Ms.", "Niloofar", "Ahmadi", "200000003", LocalDate.of(1995, 1, 8),
                        "09120000003", "niloofar.ahmadi@example.com", "Dusseldorf", "Shahrivar Street")
        ));
    }

    private void seedVets() {
        if (vetRepository.count() != 0) return;
        Building building = buildingService.getFirstByActive();
        vetRepository.saveAll(List.of(
                vet("Sara", "Moradi", "100000001",
                        LocalDate.of(1985, 3, 18),
                        "09210000001", "sara.moradi@example.com",
                        "Berlin", "Your Boulevard", building),
                vet("Reza", "Hosseini", "100000002",
                        LocalDate.of(1982, 11, 2),
                        "09210000002", "reza.hosseini@example.com",
                        "Hamburg", "Malibad Street", building),
                vet("Parisa", "Etemadi", "100000003",
                        LocalDate.of(1990, 6, 27),
                        "09210000003", "parisa.etemadi@example.com",
                        "Dusseldorf", "Ferdowsi Street", building)
        ));
    }

    private void seedPets() {
        if (petRepository.count() != 0) return;

        List<Owner> owners = ownerRepository.findAll();
        List<Species> species = speciesRepository.findAll();

        if (owners.isEmpty() || species.isEmpty()) {
            throw new IllegalStateException("Cannot seed pets: owners or species are missing");
        }

        Map<String, Owner> ownersByNationalId = owners.stream()
                .collect(Collectors.toMap(owner -> owner.getPerson().getNationalId(), Function.identity()));
        Map<String, Species> speciesByCode = species.stream()
                .collect(Collectors.toMap(Species::getCode, Function.identity()));

        requireSeedReference(ownersByNationalId, "200000001", "owner");
        requireSeedReference(ownersByNationalId, "200000002", "owner");
        requireSeedReference(ownersByNationalId, "200000003", "owner");
        requireSeedReference(speciesByCode, "DOG", "species");
        requireSeedReference(speciesByCode, "CAT", "species");
        requireSeedReference(speciesByCode, "RABBIT", "species");
        requireSeedReference(speciesByCode, "HAMSTER", "species");

        petRepository.saveAll(List.of(
                pet("Luna", "White", "Small black mark", Sex.FEMALE,
                        speciesByCode.get("DOG"), ownersByNationalId.get("200000001"), LocalDate.of(2021, 5, 12)),

                pet("Milo", "Orange", "White paws", Sex.MALE,
                        speciesByCode.get("CAT"), ownersByNationalId.get("200000001"), LocalDate.of(2022, 2, 8)),

                pet("Coco", "Brown", "Long ears", Sex.FEMALE,
                        speciesByCode.get("RABBIT"), ownersByNationalId.get("200000002"), LocalDate.of(2023, 7, 21)),

                pet("Cookie", "Red", "Long ears", Sex.MALE,
                        speciesByCode.get("RABBIT"), ownersByNationalId.get("200000003"), LocalDate.of(2020, 2, 10)),

                pet("Cookie", "Red", "Long ears", Sex.FEMALE,
                        speciesByCode.get("HAMSTER"), ownersByNationalId.get("200000003"), LocalDate.of(2021, 4, 16))
        ));
    }

    private <T> void requireSeedReference(Map<String, T> records, String key, String type) {
        if (!records.containsKey(key)) {
            throw new IllegalStateException("Cannot seed pets: required " + type + " is missing: " + key);
        }
    }

    private void seedRooms() {
        if (roomRepository.count() != 0) return;
        List<Building> buildings = clinicRepository.findAll().stream()
                .filter(Building::isActive)
                .toList();
        List<RoomType> types = roomTypeRepository.findAll();
        if (buildings.isEmpty()) {
            throw new IllegalStateException("Cannot seed rooms: no active building exists");
        }
        if (types.isEmpty()) {
            throw new IllegalStateException("Cannot seed rooms: no room types exist");
        }
        Building building = buildings.getFirst();
        RoomType examination = findRoomType(types, "examination");
        RoomType treatment = findRoomType(types, "treatment");
        RoomType surgery = findRoomType(types, "surgery");
        roomRepository.saveAll(List.of(
                room("Examination Room 1", "EXAM-01", examination, building),
                room("Examination Room 2", "EXAM-02", examination, building),
                room("Treatment Room 1", "TREAT-01", treatment, building),
                room("Surgery Room 1", "SURG-01", surgery, building)
        ));
    }

    private void seedVetAvailability() {
        if (vetAvailabilityRepository.count() != 0) return;

        List<Vet> vets = vetRepository.findAll();
        if (vets.isEmpty()) return;

        LocalDate startDate = LocalDate.now().minusDays(1);

        int[][] schedules = {
                {9, 15, 16, 0},
                {9, 0, 17, 0},
                {10, 0, 18, 0}
        };

        for (int vetIndex = 0; vetIndex < vets.size(); vetIndex++) {
            Vet vet = vets.get(vetIndex);

            int[] schedule = schedules[vetIndex % schedules.length];

            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);

                // Sunday is a day off
                if (date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                    continue;
                }

                vetAvailabilityRepository.save(
                        availability(
                                vet,
                                date.atTime(schedule[0], schedule[1]),
                                date.atTime(schedule[2], schedule[3])
                        )
                );
            }
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
                contact(email, mobile), address("Home", city, city, street,
                        2, "13B", 32.54D, 23.45D));
        person.setBirthDate(birthDate);
        Owner owner = new Owner();
        owner.setPerson(person);
        return owner;
    }

    private Vet vet(String firstName, String lastName, String nationalId,
                    LocalDate birthDate, String mobile, String email, String city, String street, Building building) {
        Vet vet = new Vet();
        vet.setPerson(person("Dr.", firstName, lastName, nationalId,
                contact(email, mobile), address("Home", city, city, street,
                        3, "13B", 32.54D, 23.45D)));
        vet.getPerson().setBirthDate(birthDate);
        vet.setBuilding(building);
        return vet;
    }

    private Person person(String title, String firstName, String lastName, String nationalId,
                          Contact contact, Address address) {
        Person person = new Person();
        person.setTitle(title);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setNationalId(nationalId);
        person.setContact(contact);
        person.setAddress(address);
        return person;
    }

    private Contact contact(String email, String mobile) {
        Contact contact = new Contact();
        contact.setEmail(email);
        contact.setMobileNumber(mobile);
        return contact;
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

    private Room room(String name, String number, RoomType roomType, Building building) {
        Room room = new Room();
        room.setName(name);
        room.setRoomNumber(number);
        room.setRoomType(roomType);
        room.setBuilding(building);
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
