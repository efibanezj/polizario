package com.ij.polizario.controller.response;

import com.ij.polizario.core.enums.QhStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountantOperationQHResumeResponse {

    private String accountantDate;
    private String accountNumber;
    private String totalDebit;
    private String totalCredit;
    private String difference;
    //ZERO DIFFERENCE ACCOUNT
    private boolean zda;
    private String status;

    public void calculateStatus() {

        if ("0".equalsIgnoreCase(difference)) {
            status = QhStatusEnum.OK.name();
        } else if (isZda()) {
            status = QhStatusEnum.ERROR.name();
        } else {
            status = QhStatusEnum.WARNING.name();
        }
    }

    public String getResumeLine() {
        return String.join("/", accountantDate, accountNumber, totalDebit, totalCredit, difference, String.valueOf(zda), status);
    }
}
