package com.tarento.formservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CSVDataDto {

    private String applicationId;
    private Long formId;
    private String title;
    private String section;
    private String question;
    private String instituteAnswer;
    private String inspectionAnswer;
    private String updatedBy;
    private String updatedDate;
    private String createdDate;
    private Integer version;
    private String status;
    private String inspectionCompletionDate;

}
