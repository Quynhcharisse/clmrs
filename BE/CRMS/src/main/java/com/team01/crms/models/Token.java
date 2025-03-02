package com.team01.crms.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`token`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`account_id`")
    Account account;

    String value;

    String type;

    String status;

}
