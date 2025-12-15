package com.MediConnect.websocket;

import com.MediConnect.MediConnect.dto.QueueEntryDTO;
import com.MediConnect.services.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QueueSocketHandler {

    private final QueueService queueService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Subscribe to queue updates for a specific dispensary
     * Client sends: /app/queue/subscribe/{dispensaryId}
     * Client receives: /topic/queue/{dispensaryId}
     */
    @MessageMapping("/queue/subscribe/{dispensaryId}")
    @SendTo("/topic/queue/{dispensaryId}")
    public List<QueueEntryDTO> subscribeToDispensaryQueue(@DestinationVariable String dispensaryId) {
        log.info("Client subscribed to dispensary queue: {}", dispensaryId);
        return queueService.getQueueByDispensary(dispensaryId);
    }

    /**
     * Subscribe to queue updates for a specific doctor
     * Client sends: /app/queue/doctor/{doctorId}
     * Client receives: /topic/queue/doctor/{doctorId}
     */
    @MessageMapping("/queue/doctor/{doctorId}")
    @SendTo("/topic/queue/doctor/{doctorId}")
    public List<QueueEntryDTO> subscribeToDoctorQueue(@DestinationVariable String doctorId) {
        log.info("Client subscribed to doctor queue: {}", doctorId);
        return queueService.getQueueByDoctor(doctorId);
    }

    /**
     * Subscribe to personal queue updates
     * Client sends: /app/queue/patient/{patientId}
     * Client receives: /queue/patient/{patientId}
     */
    @MessageMapping("/queue/patient/{patientId}")
    public void subscribeToPersonalQueue(@DestinationVariable String patientId) {
        log.info("Patient subscribed to personal queue updates: {}", patientId);
        List<QueueEntryDTO> history = queueService.getPatientQueueHistory(patientId);
        messagingTemplate.convertAndSendToUser(
                patientId,
                "/queue/updates",
                history
        );
    }

    /**
     * Broadcast queue update to all subscribers
     */
    public void broadcastQueueUpdate(String dispensaryId, List<QueueEntryDTO> queue) {
        messagingTemplate.convertAndSend("/topic/queue/" + dispensaryId, queue);
    }

    /**
     * Send notification to specific patient
     */
    public void notifyPatient(String patientId, QueueEntryDTO entry) {
        messagingTemplate.convertAndSendToUser(
                patientId,
                "/queue/notification",
                entry
        );
    }
}