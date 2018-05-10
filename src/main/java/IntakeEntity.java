public class IntakeEntity {
    private String createdByEmail;
    private String division;
    private String businessImperative;
    private String executionPlan;
    private String project;
    private String accountable;
    private String plant;
    private String metric;
    private String metricDataType;

    //these fields could be either numeric or text
    private Object budget;
    private Object stretch;

    private String status;
    private String department1;
    private String department2;
    private String department3;
    private String department4;
    private String department5;

    public IntakeEntity() {
    }

    public void replaceNullsWithEmptyStrings(IntakeEntity entity) {
        if (entity.getDepartment2() == null) {
            entity.setDepartment2("");
        }

        if (entity.getDepartment3() == null) {
            entity.setDepartment3("");
        }

        if (entity.getDepartment4() == null) {
            entity.setDepartment4("");
        }

        if (entity.getDepartment5() == null) {
            entity.setDepartment5("");
        }
    }

    public String getMetricDataType() {
        return metricDataType;
    }

    public void setMetricDataType(String metricDataType) {
        this.metricDataType = metricDataType;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getBusinessImperative() {
        return businessImperative;
    }

    public void setBusinessImperative(String businessImperative) {
        this.businessImperative = businessImperative;
    }

    public String getExecutionPlan() {
        return executionPlan;
    }

    public void setExecutionPlan(String executionPlan) {
        this.executionPlan = executionPlan;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getAccountable() {
        return accountable;
    }

    public void setAccountable(String accountable) {
        this.accountable = accountable;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Object getBudget() {
        return budget;
    }

    public void setBudget(Object budget) {
        this.budget = budget;
    }

    public Object getStretch() {
        return stretch;
    }

    public void setStretch(Object stretch) {
        this.stretch = stretch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment1() {
        return department1;
    }

    public void setDepartment1(String department1) {
        this.department1 = department1;
    }

    public String getDepartment2() {
        return department2;
    }

    public void setDepartment2(String department2) {
        this.department2 = department2;
    }

    public String getDepartment3() {
        return department3;
    }

    public void setDepartment3(String department3) {
        this.department3 = department3;
    }

    public String getDepartment4() {
        return department4;
    }

    public void setDepartment4(String department4) {
        this.department4 = department4;
    }

    public String getDepartment5() {
        return department5;
    }

    public void setDepartment5(String department5) {
        this.department5 = department5;
    }
}
