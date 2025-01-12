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
@Table(name = "`claim_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaimRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`staff_id`")
    Staff staff; // ID nhân viên (Foreign Key liên kết với bảng Staff)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`project_id`")
    Project project; // ID dự án (Foreign Key liên kết với bảng Project)

    String status; // Trạng thái yêu cầu: Draft, Pending Approval, Approved, Paid, Rejected, Cancelled

    String description; // Mô tả yêu cầu

    @Column(name = "`claim_date`")
    LocalDate claimDate; // Ngày tạo yêu cầu

    @Column(name = "`approved_date`")
    LocalDate approvedDate; // Ngày phê duyệt yêu cầu (nếu có)

    @Column(name = "`total_working_hours`")
    Double totalWorkingHours; // Tổng số giờ làm việc yêu cầu thanh toán

    @Column(name = "`total_claim_amount`")
    Double totalClaimAmount; // Tổng số tiền yêu cầu thanh toán
}
