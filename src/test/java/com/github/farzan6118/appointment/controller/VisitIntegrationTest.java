package com.github.farzan6118.appointment.controller;

import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.appointment.repository.VisitRepository;
import com.github.farzan6118.clinic.model.Room;
import com.github.farzan6118.clinic.repository.RoomRepository;
import com.github.farzan6118.config.ClinicProperties;
import com.github.farzan6118.infrastructure.email.VisitNotificationService;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.pet.repository.PetRepository;
import com.github.farzan6118.vet.model.Vet;
import com.github.farzan6118.vet.model.VetAvailability;
import com.github.farzan6118.vet.repository.VetAvailabilityRepository;
import com.github.farzan6118.vet.repository.VetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VisitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private VetRepository vetRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private VetAvailabilityRepository vetAvailabilityRepository;
    @Autowired
    private ClinicProperties clinicProperties;

    @MockitoBean
    private VisitNotificationService visitNotificationService;

    @Test
    void createAndRescheduleVisit_overHttpAndPersistRoundedTimeRanges() throws Exception {
        Pet pet = petRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("Seeder must provide a Pet for integration tests"));
        Vet vet = vetRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("Seeder must provide a Vet for integration tests"));
        Room room = roomRepository.findAll().stream()
                .filter(candidate -> candidate.isActive()
                        && candidate.getClinic() != null && candidate.getClinic().isActive())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Seeder must provide an active Room for integration tests"));

        LocalDate bookingDate = nextOpenDay(LocalDate.now().plusDays(30));
        LocalDate rescheduleDate = nextOpenDay(bookingDate.plusDays(1));
        LocalTime visitStart = clinicProperties.workingHours().start();
        LocalTime availabilityEnd = clinicProperties.workingHours().end();
        vetAvailabilityRepository.saveAll(java.util.List.of(
                VetAvailability.create(vet, bookingDate.atTime(visitStart), bookingDate.atTime(availabilityEnd)),
                VetAvailability.create(vet, rescheduleDate.atTime(visitStart), rescheduleDate.atTime(availabilityEnd))));

        String createRequest = """
                {
                  "petUuid": "%s",
                  "vetUuid": "%s",
                  "roomUuid": "%s",
                  "visitDate": "%s",
                  "visitTime": "%s",
                  "visitType": "ONSITE",
                  "durationMinutes": 6,
                  "description": "Integration booking"
                }
                """.formatted(pet.getUuid(), vet.getUuid(), room.getUuid(),
                bookingDate, visitStart.format(DateTimeFormatter.ofPattern("HH:mm")));

        mockMvc.perform(post("/api/visits")
                        .contentType(APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated());

        Visit createdVisit = visitRepository.findAll().stream()
                .filter(visit -> visit.getPet().getUuid().equals(pet.getUuid())
                        && visit.getStartTime().equals(bookingDate.atTime(visitStart)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Booking endpoint did not persist the Visit"));
        assertEquals(bookingDate.atTime(visitStart.plusMinutes(10)), createdVisit.getEndTime());

        LocalTime rescheduleTime = visitStart.plusMinutes(30);
        String rescheduleRequest = """
                {
                  "visitDate": "%s",
                  "visitTime": "%s",
                  "visitType": "ONSITE",
                  "roomUuid": "%s",
                  "description": "Integration reschedule"
                }
                """.formatted(rescheduleDate,
                rescheduleTime.format(DateTimeFormatter.ofPattern("HH:mm")), room.getUuid());

        mockMvc.perform(put("/api/visits/{uuid}", createdVisit.getUuid())
                        .contentType(APPLICATION_JSON)
                        .content(rescheduleRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/visits/{uuid}", createdVisit.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitDateFrom").value(rescheduleDate + "T" + rescheduleTime + ":00"))
                .andExpect(jsonPath("$.visitDateTo").value(rescheduleDate + "T" + visitStart.plusMinutes(40) + ":00"))
                .andExpect(jsonPath("$.description").value("Integration reschedule"))
                .andExpect(jsonPath("$.roomUuid").value(room.getUuid().toString()));
    }

    private LocalDate nextOpenDay(LocalDate date) {
        LocalDate candidate = date;
        while (clinicProperties.closeDays().contains(candidate.getDayOfWeek())) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }
}
