package org.example.springbatchdemo.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JobMetadata {
    private String jobName;
    private int lastProcessedRow;
}
