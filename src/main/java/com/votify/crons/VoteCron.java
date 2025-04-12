package com.votify.crons;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import com.votify.enums.AgendaStatus;
import com.votify.models.AgendaModel;
import com.votify.repositories.AgendaRepository;
import jakarta.transaction.Transactional;

public class VoteCron {
    private final AgendaRepository agendaRepository;

    public VoteCron(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }
    @Scheduled(fixedRate = 60000) 
    @Transactional
    public void closeExpiredVotingSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<AgendaModel> openAgendas = agendaRepository.findByStatusAndEndVotingAtBefore(
                AgendaStatus.OPEN, now);
        
        for (AgendaModel agenda : openAgendas) {
            agenda.setStatus(AgendaStatus.CLOSED);
            agendaRepository.save(agenda);
        }
    }

}
