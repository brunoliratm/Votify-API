package com.votify.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.votify.enums.AgendaStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "agendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendaModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonBackReference
    private SessionModel session;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL)
    private List<VoteModel> votes;

    @Column(name = "start_voting_at")
    private LocalDateTime startVotingAt;

    @Column(name = "end_voting_at")
    private LocalDateTime endVotingAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AgendaStatus status;
}
