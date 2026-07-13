package com.ruoyi.manage.domain.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单位设备报告预览数据。
 */
public class UnitDeviceReportPreview
{
    private String reportTitle;
    private String scopeName;
    private String generatedTime;
    private UnitInfo unitInfo = new UnitInfo();
    private Overview overview = new Overview();
    private List<FirePointRow> firePoints = new ArrayList<>();
    private List<GatewayRow> gateways = new ArrayList<>();
    private List<SensorRow> sensors = new ArrayList<>();
    private List<ExtinguisherRow> extinguishers = new ArrayList<>();

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getScopeName() { return scopeName; }
    public void setScopeName(String scopeName) { this.scopeName = scopeName; }
    public String getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(String generatedTime) { this.generatedTime = generatedTime; }
    public UnitInfo getUnitInfo() { return unitInfo; }
    public void setUnitInfo(UnitInfo unitInfo) { this.unitInfo = unitInfo; }
    public Overview getOverview() { return overview; }
    public void setOverview(Overview overview) { this.overview = overview; }
    public List<FirePointRow> getFirePoints() { return firePoints; }
    public void setFirePoints(List<FirePointRow> firePoints) { this.firePoints = firePoints; }
    public List<GatewayRow> getGateways() { return gateways; }
    public void setGateways(List<GatewayRow> gateways) { this.gateways = gateways; }
    public List<SensorRow> getSensors() { return sensors; }
    public void setSensors(List<SensorRow> sensors) { this.sensors = sensors; }
    public List<ExtinguisherRow> getExtinguishers() { return extinguishers; }
    public void setExtinguishers(List<ExtinguisherRow> extinguishers) { this.extinguishers = extinguishers; }

    public static class UnitInfo
    {
        private Long deptId;
        private String deptName;
        private String parentDeptName;
        private String province;
        private String city;
        private String area;
        private String leader;
        private String phone;
        private String email;
        private String status;

        public Long getDeptId() { return deptId; }
        public void setDeptId(Long deptId) { this.deptId = deptId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getParentDeptName() { return parentDeptName; }
        public void setParentDeptName(String parentDeptName) { this.parentDeptName = parentDeptName; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
        public String getLeader() { return leader; }
        public void setLeader(String leader) { this.leader = leader; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class Overview
    {
        private Long childDeptCount;
        private Long firePointCount;
        private Long gatewayCount;
        private Long sensorCount;
        private Long extinguisherCount;
        private Long gatewayUnboundFirePointCount;
        private Long sensorUnboundGatewayCount;
        private Long extinguisherUnboundSensorCount;
        private Long missingDeptCount;

        public Long getChildDeptCount() { return childDeptCount; }
        public void setChildDeptCount(Long childDeptCount) { this.childDeptCount = childDeptCount; }
        public Long getFirePointCount() { return firePointCount; }
        public void setFirePointCount(Long firePointCount) { this.firePointCount = firePointCount; }
        public Long getGatewayCount() { return gatewayCount; }
        public void setGatewayCount(Long gatewayCount) { this.gatewayCount = gatewayCount; }
        public Long getSensorCount() { return sensorCount; }
        public void setSensorCount(Long sensorCount) { this.sensorCount = sensorCount; }
        public Long getExtinguisherCount() { return extinguisherCount; }
        public void setExtinguisherCount(Long extinguisherCount) { this.extinguisherCount = extinguisherCount; }
        public Long getGatewayUnboundFirePointCount() { return gatewayUnboundFirePointCount; }
        public void setGatewayUnboundFirePointCount(Long value) { this.gatewayUnboundFirePointCount = value; }
        public Long getSensorUnboundGatewayCount() { return sensorUnboundGatewayCount; }
        public void setSensorUnboundGatewayCount(Long value) { this.sensorUnboundGatewayCount = value; }
        public Long getExtinguisherUnboundSensorCount() { return extinguisherUnboundSensorCount; }
        public void setExtinguisherUnboundSensorCount(Long value) { this.extinguisherUnboundSensorCount = value; }
        public Long getMissingDeptCount() { return missingDeptCount; }
        public void setMissingDeptCount(Long missingDeptCount) { this.missingDeptCount = missingDeptCount; }
    }

    public static class FirePointRow
    {
        private Long firePointId;
        private String deptName;
        private String firePointName;
        private String firePointCode;
        private String stationType;
        private String location;
        private String building;
        private String floor;
        private Integer expectedExtinguisherCount;
        private Integer actualExtinguisherCount;
        private Integer actualSensorCount;
        private String status;

        public Long getFirePointId() { return firePointId; }
        public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getFirePointName() { return firePointName; }
        public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
        public String getFirePointCode() { return firePointCode; }
        public void setFirePointCode(String firePointCode) { this.firePointCode = firePointCode; }
        public String getStationType() { return stationType; }
        public void setStationType(String stationType) { this.stationType = stationType; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getBuilding() { return building; }
        public void setBuilding(String building) { this.building = building; }
        public String getFloor() { return floor; }
        public void setFloor(String floor) { this.floor = floor; }
        public Integer getExpectedExtinguisherCount() { return expectedExtinguisherCount; }
        public void setExpectedExtinguisherCount(Integer value) { this.expectedExtinguisherCount = value; }
        public Integer getActualExtinguisherCount() { return actualExtinguisherCount; }
        public void setActualExtinguisherCount(Integer value) { this.actualExtinguisherCount = value; }
        public Integer getActualSensorCount() { return actualSensorCount; }
        public void setActualSensorCount(Integer actualSensorCount) { this.actualSensorCount = actualSensorCount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class GatewayRow
    {
        private Long gatewayId;
        private Long externalTboxId;
        private String deptName;
        private String firePointName;
        private String imei;
        private String sim;
        private String status;
        private Date lastOnlineTime;
        private Date lastSyncTime;

        public Long getGatewayId() { return gatewayId; }
        public void setGatewayId(Long gatewayId) { this.gatewayId = gatewayId; }
        public Long getExternalTboxId() { return externalTboxId; }
        public void setExternalTboxId(Long externalTboxId) { this.externalTboxId = externalTboxId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getFirePointName() { return firePointName; }
        public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
        public String getImei() { return imei; }
        public void setImei(String imei) { this.imei = imei; }
        public String getSim() { return sim; }
        public void setSim(String sim) { this.sim = sim; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Date getLastOnlineTime() { return lastOnlineTime; }
        public void setLastOnlineTime(Date lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
        public Date getLastSyncTime() { return lastSyncTime; }
        public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    }

    public static class SensorRow
    {
        private Long sensorId;
        private String deptName;
        private String firePointName;
        private String sensorCode;
        private String gatewayCode;
        private BigDecimal pressure;
        private BigDecimal temperature;
        private Integer batteryLevel;
        private String status;
        private Date lastOnlineTime;
        private Date lastSyncTime;

        public Long getSensorId() { return sensorId; }
        public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getFirePointName() { return firePointName; }
        public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
        public String getSensorCode() { return sensorCode; }
        public void setSensorCode(String sensorCode) { this.sensorCode = sensorCode; }
        public String getGatewayCode() { return gatewayCode; }
        public void setGatewayCode(String gatewayCode) { this.gatewayCode = gatewayCode; }
        public BigDecimal getPressure() { return pressure; }
        public void setPressure(BigDecimal pressure) { this.pressure = pressure; }
        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public Integer getBatteryLevel() { return batteryLevel; }
        public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Date getLastOnlineTime() { return lastOnlineTime; }
        public void setLastOnlineTime(Date lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
        public Date getLastSyncTime() { return lastSyncTime; }
        public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    }

    public static class ExtinguisherRow
    {
        private Long extinguisherId;
        private String deptName;
        private String firePointName;
        private String labelCode;
        private String productName;
        private String specification;
        private String extinguisherType;
        private String extinguisherForm;
        private String sensorCode;
        private Date productionDate;
        private Date expiryDate;
        private String status;
        private Date lastSyncTime;

        public Long getExtinguisherId() { return extinguisherId; }
        public void setExtinguisherId(Long extinguisherId) { this.extinguisherId = extinguisherId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getFirePointName() { return firePointName; }
        public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
        public String getLabelCode() { return labelCode; }
        public void setLabelCode(String labelCode) { this.labelCode = labelCode; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getSpecification() { return specification; }
        public void setSpecification(String specification) { this.specification = specification; }
        public String getExtinguisherType() { return extinguisherType; }
        public void setExtinguisherType(String extinguisherType) { this.extinguisherType = extinguisherType; }
        public String getExtinguisherForm() { return extinguisherForm; }
        public void setExtinguisherForm(String extinguisherForm) { this.extinguisherForm = extinguisherForm; }
        public String getSensorCode() { return sensorCode; }
        public void setSensorCode(String sensorCode) { this.sensorCode = sensorCode; }
        public Date getProductionDate() { return productionDate; }
        public void setProductionDate(Date productionDate) { this.productionDate = productionDate; }
        public Date getExpiryDate() { return expiryDate; }
        public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Date getLastSyncTime() { return lastSyncTime; }
        public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    }
}
