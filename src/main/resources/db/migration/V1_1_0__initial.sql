CREATE TABLE qh_info
(
    id              INT(100) NOT NULL AUTO_INCREMENT,
    column_A        VARCHAR(100),
    accountant_date  VARCHAR(100),
    column_C        VARCHAR(100),
    column_D        VARCHAR(100),
    column_E        VARCHAR(100),
    column_F        VARCHAR(100),
    column_G        VARCHAR(100),
    column_H        VARCHAR(100),
    column_I        VARCHAR(100),
    column_J        VARCHAR(100),
    accounting_type VARCHAR(100),
    column_L        VARCHAR(100),
    column_M        VARCHAR(100),
    column_N        VARCHAR(100),
    column_O        VARCHAR(100),
    operation_sign  VARCHAR(100),
    debit_value     VARCHAR(100),
    credit_value    VARCHAR(100),
    column_S        VARCHAR(100),
    column_T        VARCHAR(100),
    column_U        VARCHAR(100),
    column_V        VARCHAR(100),
    column_W        VARCHAR(100),
    column_X        VARCHAR(100),
    column_Y        VARCHAR(100),
    column_Z        VARCHAR(100),
    column_AA       VARCHAR(100),
    column_AB       VARCHAR(100),
    column_AC       VARCHAR(100),
    column_AD       VARCHAR(100),
    column_AE       VARCHAR(100),
    column_AF       VARCHAR(100),
    column_AG       VARCHAR(100),
    column_AH       VARCHAR(100),
    account_number       VARCHAR(100),
    column_AJ       VARCHAR(100),
    column_AK       VARCHAR(100),
    column_AL       VARCHAR(100),
    contract_number VARCHAR(100),
    column_AN       VARCHAR(100),
    column_AO       VARCHAR(100),
    column_AP       VARCHAR(100),
    column_AQ       VARCHAR(100),
    column_AR       VARCHAR(100),
    column_AS       VARCHAR(100),
    column_AT       VARCHAR(100),
    column_AU       VARCHAR(100),
    column_AV       VARCHAR(100),
    column_AW       VARCHAR(100),
    PRIMARY KEY (id)
);

CREATE TABLE no_qh_info
(
    id              INT(100) NOT NULL AUTO_INCREMENT,
    entidad        VARCHAR(100),
    PRIMARY KEY (id)
);



CREATE TABLE polizario_file
(
    id              INT(100) NOT NULL AUTO_INCREMENT,
    accountant_date        VARCHAR(500),
    sequence_number        VARCHAR(500),
    account         VARCHAR(500),
    operate_center           VARCHAR(500),
    destiny_center           VARCHAR(500),
    accounting_type VARCHAR(500),
    debits VARCHAR(500),
    credits VARCHAR(500),
    correct_indicator VARCHAR(500),
    accountant_operation VARCHAR(500),
    cross_reference VARCHAR(500),
    description VARCHAR(500),
    application     VARCHAR(500),
    pd     VARCHAR(500),
    currency     VARCHAR(500),
    PRIMARY KEY (id)
);