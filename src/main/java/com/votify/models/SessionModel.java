package com.votify.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionModel extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "start_date")
    private LocalDateTime startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserModel organizer;

    @OneToMany(mappedBy = "session", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonManagedReference
    private List<AgendaModel> agendas;
}