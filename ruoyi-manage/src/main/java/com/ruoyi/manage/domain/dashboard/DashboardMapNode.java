package com.ruoyi.manage.domain.dashboard;

import java.util.ArrayList;
import java.util.List;

public class DashboardMapNode
{
    private String nodeType;
    private String nodeId;
    private Long deptId;
    private Long parentDeptId;
    private Long firePointId;
    private String name;
    private String deptName;
    private String firePointName;
    private String longitude;
    private String latitude;
    private String province;
    private String city;
    private String area;
    private String location;
    private String building;
    private String floor;
    private String leader;
    private String phone;
    private String contactPerson;
    private String contactPhone;
    private Long externalCompanyId;
    private String externalCompanyName;
    private String status;
    private int childUnitCount;
    private int firePointCount;
    private int directFirePointCount;
    private int gatewayCount;
    private int sensorCount;
    private int extinguisherCount;
    private int normalExtinguisherCount;
    private int warningExtinguisherCount;
    private int expiredExtinguisherCount;
    private List<DashboardMapNode> children = new ArrayList<DashboardMapNode>();
    private List<DashboardMapNode> firePoints = new ArrayList<DashboardMapNode>();

    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getParentDeptId() { return parentDeptId; }
    public void setParentDeptId(Long parentDeptId) { this.parentDeptId = parentDeptId; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getChildUnitCount() { return childUnitCount; }
    public void setChildUnitCount(int childUnitCount) { this.childUnitCount = childUnitCount; }
    public int getFirePointCount() { return firePointCount; }
    public void setFirePointCount(int firePointCount) { this.firePointCount = firePointCount; }
    public int getDirectFirePointCount() { return directFirePointCount; }
    public void setDirectFirePointCount(int directFirePointCount) { this.directFirePointCount = directFirePointCount; }
    public int getGatewayCount() { return gatewayCount; }
    public void setGatewayCount(int gatewayCount) { this.gatewayCount = gatewayCount; }
    public int getSensorCount() { return sensorCount; }
    public void setSensorCount(int sensorCount) { this.sensorCount = sensorCount; }
    public int getExtinguisherCount() { return extinguisherCount; }
    public void setExtinguisherCount(int extinguisherCount) { this.extinguisherCount = extinguisherCount; }
    public int getNormalExtinguisherCount() { return normalExtinguisherCount; }
    public void setNormalExtinguisherCount(int normalExtinguisherCount) { this.normalExtinguisherCount = normalExtinguisherCount; }
    public int getWarningExtinguisherCount() { return warningExtinguisherCount; }
    public void setWarningExtinguisherCount(int warningExtinguisherCount) { this.warningExtinguisherCount = warningExtinguisherCount; }
    public int getExpiredExtinguisherCount() { return expiredExtinguisherCount; }
    public void setExpiredExtinguisherCount(int expiredExtinguisherCount) { this.expiredExtinguisherCount = expiredExtinguisherCount; }
    public List<DashboardMapNode> getChildren() { return children; }
    public void setChildren(List<DashboardMapNode> children) { this.children = children; }
    public List<DashboardMapNode> getFirePoints() { return firePoints; }
    public void setFirePoints(List<DashboardMapNode> firePoints) { this.firePoints = firePoints; }
}
