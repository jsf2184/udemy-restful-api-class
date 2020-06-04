package com.jefff.app.restful.web.ui.model.response;

public class OperationStatusModel {
    private String operationName;
    private String operationStatus;

    public OperationStatusModel(RequestOperation operation,
                                RequestOperationStatus operationStatus)
    {
        this(operation.name(), operationStatus.name());
    }

    public OperationStatusModel(String operationName, String operationStatus) {
        this.operationStatus = operationStatus;
        this.operationName = operationName;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
}
