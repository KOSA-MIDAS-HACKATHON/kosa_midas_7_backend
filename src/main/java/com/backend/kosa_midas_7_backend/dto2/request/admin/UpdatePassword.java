package com.backend.kosa_midas_7_backend.dto2.request.admin;

import lombok.Getter;

@Getter
public class UpdatePassword {

    private String accountId;

    private String newPassword;
}
