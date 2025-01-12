package com.quynh.clmrs.models.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`staff_project`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`staff_id`")
    Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`project_id`")
    Project project;

    String role; // Vai trò của nhân viên trong dự án (PM, QA, Developer, etc.)

    @Column(name = "`joined_date`")
    LocalDate joinedDate; // Ngày tham gia dự án

    @Column(name = "`left_date`")
    LocalDate leftDate; // Ngày rời khỏi dự án (nếu có)
}
