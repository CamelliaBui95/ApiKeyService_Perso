package fr.btn.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="CLIENT")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_CLIENT")
    private Integer id;

    @Column(name="NOM_CLIENT")
    private String name;

    @Column(name="EMAIL", unique = true)
    private String email;

    @Column(name = "CLE", unique = true)
    private String apiKey;

    @Column(name="QUOTA_MENSUEL")
    private Integer quota;

    @Column(name="DATE_CREE")
    private LocalDate createdDate;

    @Column(name = "STATUT")
    private String status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender")
    private List<MailEntity> mails;

}
