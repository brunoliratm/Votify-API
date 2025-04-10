package com.votify.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
