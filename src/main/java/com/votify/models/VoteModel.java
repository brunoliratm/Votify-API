package com.votify.models;

import com.votify.enums.VoteOption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_option")
    private VoteOption voteOption;

    @ManyToOne
    @JoinColumn(name = "agenda_id", nullable = false)
    private AgendaModel agenda;

    @ManyToOne
    @JoinColumn(name = "associate_id", nullable = false)
    private UserModel associate;

    @Column(name = "voted_time", nullable = false, updatable = false)
    private LocalDateTime votedTime;
}
