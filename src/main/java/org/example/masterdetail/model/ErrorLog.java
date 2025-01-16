package org.example.masterdetail.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator= "error_log_error_log_id_seq")
    @SequenceGenerator(name = "error_log_error_log_id_seq", sequenceName = "error_log_error_log_id_seq", allocationSize = 1)
    @Column(name = "error_id")
    private Long id;

    @Column(name = "error_time", nullable = false)
    private LocalDateTime errorTime;

    @Column(name = "error_type", nullable = false)
    private String errorType;

    @Column(name = "message", nullable = false)
    private String message;

}

