package com.ij.polizario.config.mapper;

import com.ij.polizario.persistence.entities.QhInfoEntity;
import org.springframework.batch.item.file.LineMapper;

public class QHFieldSetMapper implements LineMapper<QhInfoEntity> {

    private final LineMapper<QhInfoEntity> delegate;

    public QHFieldSetMapper(LineMapper<QhInfoEntity> delegate) {
        this.delegate = delegate;
    }

    @Override
    public QhInfoEntity mapLine(String line, int lineNumber) throws Exception {
        if(line.trim().isEmpty()){
            return null;
        }
        String modifiedLine = modifyLine(line);

        return delegate.mapLine(modifiedLine, lineNumber);
    }

    private String modifyLine(String line) {


        String[] lineTokens = line.split("/");
        var newLineTokens = new StringBuilder();
        for (int i = 0; i < lineTokens.length; i++) {
            if (i == 3 && lineTokens[i].length() == 8) {// check program name
                newLineTokens.append(lineTokens[i], 0, 4).append("/");
                newLineTokens.append(lineTokens[i], 3, 8).append("/");
               i = i + 1;
            }

            if ((i == 35 || i == 36) && lineTokens[i].trim().length() == 18) {// check contract number

                newLineTokens.append(lineTokens[i], 0, 4).append("/");
                newLineTokens.append(lineTokens[i], 4, 8).append("/");
                newLineTokens.append(lineTokens[i], 8, 18).append("/");
            }
            else {
                newLineTokens.append(lineTokens[i]).append("/");
            }
        }

        return newLineTokens.toString();
    }
}
