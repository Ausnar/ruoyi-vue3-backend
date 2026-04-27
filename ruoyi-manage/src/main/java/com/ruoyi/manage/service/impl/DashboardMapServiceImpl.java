package com.ruoyi.manage.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.dashboard.DashboardMapFirePoint;
import com.ruoyi.manage.domain.dashboard.DashboardMapNode;
import com.ruoyi.manage.domain.dashboard.DashboardMapResponse;
import com.ruoyi.manage.mapper.DashboardMapMapper;
import com.ruoyi.manage.service.IDashboardMapService;
import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.system.service.ISysDeptApiConfigService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class DashboardMapServiceImpl implements IDashboardMapService
{
    private static final Long ADMIN_ROOT_DEPT_ID = 100L;
    private static final String NODE_PARENT_DEPT = "parentDept";
    private static final String NODE_CHILD_DEPT = "childDept";
    private static final String NODE_DIRECT_GROUP = "directGroup";
    private static final String NODE_FIRE_POINT = "firePoint";
    private static final String DIRECT_GROUP_NAME = "直属/未分配消防点";

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysDeptApiConfigService contractService;

    @Autowired
    private DashboardMapMapper dashboardMapMapper;

    @Override
    public DashboardMapResponse getMapHierarchy()
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Map<Long, SysDept> deptMap = toDeptMap(depts);
        List<Long> permittedDeptIds = getPermittedDeptIds();
        List<DashboardMapFirePoint> firePoints = dashboardMapMapper.selectFirePointsForMap(permittedDeptIds);
        List<SysDeptApiConfig> contracts = contractService.selectSysDeptApiConfigList(new SysDeptApiConfig());

        Map<Long, List<DashboardMapFirePoint>> directPointsByRoot = new HashMap<Long, List<DashboardMapFirePoint>>();
        Map<Long, List<DashboardMapFirePoint>> pointsByDirectChild = new HashMap<Long, List<DashboardMapFirePoint>>();
        Set<Long> rootIds = new HashSet<Long>();

        for (SysDeptApiConfig contract : contracts)
        {
            Long rootId = findBusinessRootId(contract.getDeptId(), deptMap);
            if (rootId != null)
            {
                rootIds.add(rootId);
            }
        }

        for (DashboardMapFirePoint firePoint : firePoints)
        {
            Long resolvedDeptId = firePoint.getResolvedDeptId();
            Long rootId = findBusinessRootId(resolvedDeptId, deptMap);
            if (rootId == null)
            {
                continue;
            }
            rootIds.add(rootId);
            Long directChildId = findDirectChildId(rootId, resolvedDeptId, deptMap);
            if (directChildId == null || directChildId.equals(rootId))
            {
                addToMapList(directPointsByRoot, rootId, firePoint);
            }
            else
            {
                addToMapList(pointsByDirectChild, directChildId, firePoint);
            }
        }

        DashboardMapResponse response = new DashboardMapResponse();
        response.setRootDeptId(ADMIN_ROOT_DEPT_ID);
        response.setRoots(buildRootNodes(depts, deptMap, rootIds, directPointsByRoot, pointsByDirectChild, contracts));
        return response;
    }

    private List<DashboardMapNode> buildRootNodes(List<SysDept> depts, Map<Long, SysDept> deptMap, Set<Long> rootIds,
            Map<Long, List<DashboardMapFirePoint>> directPointsByRoot,
            Map<Long, List<DashboardMapFirePoint>> pointsByDirectChild, List<SysDeptApiConfig> contracts)
    {
        List<DashboardMapNode> roots = new ArrayList<DashboardMapNode>();
        for (SysDept dept : depts)
        {
            if (!rootIds.contains(dept.getDeptId()))
            {
                continue;
            }
            DashboardMapNode root = buildDeptNode(dept, NODE_PARENT_DEPT);
            List<DashboardMapNode> children = buildChildNodes(dept, depts, deptMap, pointsByDirectChild, contracts);
            List<DashboardMapNode> directFirePoints = buildFirePointNodes(directPointsByRoot.get(dept.getDeptId()));
            if (!directFirePoints.isEmpty())
            {
                children.add(buildDirectGroupNode(dept, directFirePoints));
            }

            root.setChildren(children);
            root.setChildUnitCount(countChildDeptNodes(children));
            root.setDirectFirePointCount(directFirePoints.size());
            root.setFirePointCount(countFirePoints(children));
            applyFallbackCoordinate(root, collectCoordinateNodes(children));
            if (hasCoordinate(root) || root.getFirePointCount() > 0 || root.getChildUnitCount() > 0)
            {
                roots.add(root);
            }
        }
        return roots;
    }

    private List<DashboardMapNode> buildChildNodes(SysDept root, List<SysDept> depts, Map<Long, SysDept> deptMap,
            Map<Long, List<DashboardMapFirePoint>> pointsByDirectChild, List<SysDeptApiConfig> contracts)
    {
        Set<Long> contractChildIds = new HashSet<Long>();
        for (SysDeptApiConfig contract : contracts)
        {
            Long childId = findDirectChildId(root.getDeptId(), contract.getDeptId(), deptMap);
            if (childId != null && !childId.equals(root.getDeptId()))
            {
                contractChildIds.add(childId);
            }
        }

        List<DashboardMapNode> children = new ArrayList<DashboardMapNode>();
        for (SysDept dept : depts)
        {
            if (!root.getDeptId().equals(dept.getParentId()))
            {
                continue;
            }
            List<DashboardMapNode> firePointNodes = buildFirePointNodes(pointsByDirectChild.get(dept.getDeptId()));
            boolean hasBusiness = !firePointNodes.isEmpty() || contractChildIds.contains(dept.getDeptId()) || hasCoordinate(dept);
            if (!hasBusiness)
            {
                continue;
            }
            DashboardMapNode child = buildDeptNode(dept, NODE_CHILD_DEPT);
            child.setFirePoints(firePointNodes);
            child.setFirePointCount(firePointNodes.size());
            applyFallbackCoordinate(child, firePointNodes);
            children.add(child);
        }
        return children;
    }

    private DashboardMapNode buildDeptNode(SysDept dept, String nodeType)
    {
        DashboardMapNode node = new DashboardMapNode();
        node.setNodeType(nodeType);
        node.setNodeId(nodeType + "-" + dept.getDeptId());
        node.setDeptId(dept.getDeptId());
        node.setParentDeptId(dept.getParentId());
        node.setName(dept.getDeptName());
        node.setDeptName(dept.getDeptName());
        node.setLongitude(blankToNull(dept.getLongitude()));
        node.setLatitude(blankToNull(dept.getLatitude()));
        node.setProvince(dept.getProvince());
        node.setCity(dept.getCity());
        node.setArea(dept.getArea());
        node.setLeader(dept.getLeader());
        node.setPhone(dept.getPhone());
        node.setStatus("normal");
        return node;
    }

    private DashboardMapNode buildDirectGroupNode(SysDept root, List<DashboardMapNode> firePointNodes)
    {
        DashboardMapNode node = new DashboardMapNode();
        node.setNodeType(NODE_DIRECT_GROUP);
        node.setNodeId(NODE_DIRECT_GROUP + "-" + root.getDeptId());
        node.setDeptId(root.getDeptId());
        node.setParentDeptId(root.getDeptId());
        node.setName(DIRECT_GROUP_NAME);
        node.setDeptName(root.getDeptName());
        node.setFirePoints(firePointNodes);
        node.setFirePointCount(firePointNodes.size());
        node.setStatus("normal");
        applyFallbackCoordinate(node, firePointNodes);
        return node;
    }

    private List<DashboardMapNode> buildFirePointNodes(List<DashboardMapFirePoint> firePoints)
    {
        List<DashboardMapNode> nodes = new ArrayList<DashboardMapNode>();
        if (firePoints == null)
        {
            return nodes;
        }
        for (DashboardMapFirePoint point : firePoints)
        {
            DashboardMapNode node = new DashboardMapNode();
            node.setNodeType(NODE_FIRE_POINT);
            node.setNodeId(NODE_FIRE_POINT + "-" + point.getFirePointId());
            node.setFirePointId(point.getFirePointId());
            node.setName(point.getFirePointName());
            node.setFirePointName(point.getFirePointName());
            node.setDeptId(point.getResolvedDeptId());
            node.setDeptName(StringUtils.isEmpty(point.getDeptName()) ? point.getResolvedDeptName() : point.getDeptName());
            node.setLongitude(toPlainString(point.getLongitude()));
            node.setLatitude(toPlainString(point.getLatitude()));
            node.setLocation(point.getLocation());
            node.setBuilding(point.getBuilding());
            node.setFloor(point.getFloor());
            node.setContactPerson(point.getContactPerson());
            node.setContactPhone(point.getContactPhone());
            node.setExternalCompanyId(point.getExternalCompanyId());
            node.setExternalCompanyName(point.getExternalCompanyName());
            node.setStatus(StringUtils.isEmpty(point.getStatus()) ? "normal" : point.getStatus());
            node.setGatewayCount(point.getGatewayCount());
            node.setSensorCount(point.getSensorCount());
            node.setExtinguisherCount(point.getExtinguisherCount());
            node.setNormalExtinguisherCount(point.getNormalExtinguisherCount());
            node.setWarningExtinguisherCount(point.getWarningExtinguisherCount());
            node.setExpiredExtinguisherCount(point.getExpiredExtinguisherCount());
            nodes.add(node);
        }
        return nodes;
    }

    private Long findBusinessRootId(Long deptId, Map<Long, SysDept> deptMap)
    {
        if (deptId == null || ADMIN_ROOT_DEPT_ID.equals(deptId))
        {
            return null;
        }
        SysDept current = deptMap.get(deptId);
        while (current != null)
        {
            if (ADMIN_ROOT_DEPT_ID.equals(current.getParentId()))
            {
                return current.getDeptId();
            }
            Long parentId = current.getParentId();
            if (parentId == null || parentId.longValue() == 0L || ADMIN_ROOT_DEPT_ID.equals(current.getDeptId()))
            {
                return null;
            }
            current = deptMap.get(parentId);
        }
        return null;
    }

    private Long findDirectChildId(Long rootId, Long deptId, Map<Long, SysDept> deptMap)
    {
        if (rootId == null || deptId == null)
        {
            return null;
        }
        if (rootId.equals(deptId))
        {
            return rootId;
        }
        SysDept current = deptMap.get(deptId);
        Long candidate = deptId;
        while (current != null && current.getParentId() != null)
        {
            if (rootId.equals(current.getParentId()))
            {
                return candidate;
            }
            candidate = current.getParentId();
            current = deptMap.get(current.getParentId());
        }
        return null;
    }

    private Map<Long, SysDept> toDeptMap(List<SysDept> depts)
    {
        Map<Long, SysDept> map = new LinkedHashMap<Long, SysDept>();
        for (SysDept dept : depts)
        {
            map.put(dept.getDeptId(), dept);
        }
        return map;
    }

    private List<Long> getPermittedDeptIds()
    {
        if (SecurityUtils.isAdmin())
        {
            return null;
        }
        Long deptId = SecurityUtils.getDeptId();
        if (deptId == null)
        {
            List<Long> emptyScope = new ArrayList<Long>();
            emptyScope.add(-1L);
            return emptyScope;
        }
        List<Long> deptIds = deptService.selectDeptAndChildrenIds(deptId);
        if (deptIds == null || deptIds.isEmpty())
        {
            List<Long> emptyScope = new ArrayList<Long>();
            emptyScope.add(-1L);
            return emptyScope;
        }
        return deptIds;
    }

    private void addToMapList(Map<Long, List<DashboardMapFirePoint>> map, Long key, DashboardMapFirePoint value)
    {
        List<DashboardMapFirePoint> list = map.get(key);
        if (list == null)
        {
            list = new ArrayList<DashboardMapFirePoint>();
            map.put(key, list);
        }
        list.add(value);
    }

    private int countChildDeptNodes(List<DashboardMapNode> nodes)
    {
        int count = 0;
        for (DashboardMapNode node : nodes)
        {
            if (NODE_CHILD_DEPT.equals(node.getNodeType()))
            {
                count++;
            }
        }
        return count;
    }

    private int countFirePoints(List<DashboardMapNode> nodes)
    {
        int count = 0;
        for (DashboardMapNode node : nodes)
        {
            if (NODE_FIRE_POINT.equals(node.getNodeType()))
            {
                count++;
            }
            count += node.getFirePointCount();
        }
        return count;
    }

    private List<DashboardMapNode> collectCoordinateNodes(List<DashboardMapNode> nodes)
    {
        List<DashboardMapNode> result = new ArrayList<DashboardMapNode>();
        for (DashboardMapNode node : nodes)
        {
            if (hasCoordinate(node))
            {
                result.add(node);
            }
            result.addAll(node.getFirePoints());
        }
        return result;
    }

    private void applyFallbackCoordinate(DashboardMapNode target, List<DashboardMapNode> coordinateSources)
    {
        if (hasCoordinate(target) || coordinateSources == null || coordinateSources.isEmpty())
        {
            return;
        }
        BigDecimal lngSum = BigDecimal.ZERO;
        BigDecimal latSum = BigDecimal.ZERO;
        int count = 0;
        for (DashboardMapNode source : coordinateSources)
        {
            if (!hasCoordinate(source))
            {
                continue;
            }
            lngSum = lngSum.add(new BigDecimal(source.getLongitude()));
            latSum = latSum.add(new BigDecimal(source.getLatitude()));
            count++;
        }
        if (count > 0)
        {
            target.setLongitude(lngSum.divide(new BigDecimal(count), 6, RoundingMode.HALF_UP).toPlainString());
            target.setLatitude(latSum.divide(new BigDecimal(count), 6, RoundingMode.HALF_UP).toPlainString());
        }
    }

    private boolean hasCoordinate(SysDept dept)
    {
        return StringUtils.isNotEmpty(dept.getLongitude()) && StringUtils.isNotEmpty(dept.getLatitude());
    }

    private boolean hasCoordinate(DashboardMapNode node)
    {
        return StringUtils.isNotEmpty(node.getLongitude()) && StringUtils.isNotEmpty(node.getLatitude());
    }

    private String blankToNull(String value)
    {
        return StringUtils.isEmpty(value) ? null : value;
    }

    private String toPlainString(BigDecimal value)
    {
        return value == null ? null : value.toPlainString();
    }
}
