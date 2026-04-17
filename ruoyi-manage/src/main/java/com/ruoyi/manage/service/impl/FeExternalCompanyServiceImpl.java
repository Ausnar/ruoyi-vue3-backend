package com.ruoyi.manage.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeApiConfigCompanyScope;
import com.ruoyi.manage.domain.FeCompanyDeptMapping;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.FeExternalCompanyMergeLog;
import com.ruoyi.manage.domain.dto.FeExternalCompanyMergeRequest;
import com.ruoyi.manage.mapper.FeApiConfigCompanyScopeMapper;
import com.ruoyi.manage.mapper.FeCompanyDeptMappingMapper;
import com.ruoyi.manage.mapper.FeExternalCompanyMapper;
import com.ruoyi.manage.mapper.FeExtinguisherMapper;
import com.ruoyi.manage.mapper.FeFirePointMapper;
import com.ruoyi.manage.mapper.FeGatewayManualRecordMapper;
import com.ruoyi.manage.mapper.FeGatewayMapper;
import com.ruoyi.manage.mapper.FeSensorManualRecordMapper;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.service.IFeExternalCompanyService;

@Service
public class FeExternalCompanyServiceImpl implements IFeExternalCompanyService
{
    private static final String SOURCE_SDK = "sdk";
    private static final String SOURCE_MANUAL = "manual";
    private static final String YES = "1";
    private static final String NO = "0";
    private static final String RECORD_ACTIVE = "active";
    private static final String RECORD_DISABLED = "disabled";
    private static final String RECORD_MERGED = "merged";
    private static final String SYNC_STATUS_OBSERVED = "observed";
    private static final String SYNC_STATUS_MANUAL = "manual";
    private static final Pattern NORMALIZE_PATTERN = Pattern.compile("[\\s()（）\\-_/、,.，。·]+");

    @Autowired
    private FeExternalCompanyMapper externalCompanyMapper;

    @Autowired
    private FeCompanyDeptMappingMapper companyDeptMappingMapper;

    @Autowired
    private FeApiConfigCompanyScopeMapper apiConfigCompanyScopeMapper;

    @Autowired
    private FeFirePointMapper firePointMapper;

    @Autowired
    private FeGatewayMapper gatewayMapper;

    @Autowired
    private FeSensorMapper sensorMapper;

    @Autowired
    private FeExtinguisherMapper extinguisherMapper;

    @Autowired
    private FeGatewayManualRecordMapper gatewayManualRecordMapper;

    @Autowired
    private FeSensorManualRecordMapper sensorManualRecordMapper;

    @Override
    public FeExternalCompany selectFeExternalCompanyByCompanyRecordId(Long companyRecordId)
    {
        return externalCompanyMapper.selectByCompanyRecordId(companyRecordId);
    }

    @Override
    public List<FeExternalCompany> selectFeExternalCompanyList(FeExternalCompany company)
    {
        if (company == null)
        {
            company = new FeExternalCompany();
        }
        if (!SecurityUtils.isAdmin() && company.getLastSourceDeptId() == null)
        {
            company.setLastSourceDeptId(SecurityUtils.getDeptId());
        }
        if (StringUtils.isNotBlank(company.getExternalCompanyName()))
        {
            company.setExternalCompanyName(company.getExternalCompanyName().trim());
        }
        return externalCompanyMapper.selectFeExternalCompanyList(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFeExternalCompany(FeExternalCompany company)
    {
        validateCompanyPayload(company, false);
        validateDuplicateName(company, null);
        Date now = DateUtils.getNowDate();
        company.setExternalCompanyName(company.getExternalCompanyName().trim());
        company.setExternalCompanyId(resolveExternalCompanyId(company.getExternalCompanyId()));
        company.setFirstSourceType(defaultValue(company.getFirstSourceType(), SOURCE_MANUAL));
        company.setSdkObserved(defaultValue(company.getSdkObserved(), NO));
        company.setManualCreated(defaultValue(company.getManualCreated(), YES));
        company.setManualCreatedBy(defaultValue(company.getManualCreatedBy(), SecurityUtils.getUsername()));
        company.setManualCreatedTime(company.getManualCreatedTime() == null ? now : company.getManualCreatedTime());
        company.setRecordStatus(defaultValue(company.getRecordStatus(), RECORD_ACTIVE));
        company.setSyncStatus(defaultValue(company.getSyncStatus(), SYNC_STATUS_MANUAL));
        company.setLastSourceDeptId(company.getLastSourceDeptId() == null ? SecurityUtils.getDeptId() : company.getLastSourceDeptId());
        company.setCreateBy(SecurityUtils.getUsername());
        company.setCreateTime(now);
        company.setUpdateBy(SecurityUtils.getUsername());
        company.setUpdateTime(now);
        return externalCompanyMapper.insertFeExternalCompany(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFeExternalCompany(FeExternalCompany company)
    {
        if (company == null || company.getCompanyRecordId() == null)
        {
            throw new ServiceException("未找到需要更新的外部单位记录");
        }
        FeExternalCompany current = requireCompany(company.getCompanyRecordId());
        if (RECORD_MERGED.equals(current.getRecordStatus()))
        {
            throw new ServiceException("已并档的外部单位不能直接修改");
        }
        validateCompanyPayload(company, true);
        validateDuplicateName(company, current.getCompanyRecordId());
        company.setExternalCompanyName(trimToNull(company.getExternalCompanyName()));
        company.setManualConfirmedName(trimToNull(company.getManualConfirmedName()));
        company.setNumberPrefix(trimToNull(company.getNumberPrefix()));
        company.setOrgPath(trimToNull(company.getOrgPath()));
        company.setRemark(trimToNull(company.getRemark()));
        company.setUpdateBy(SecurityUtils.getUsername());
        company.setUpdateTime(DateUtils.getNowDate());
        preserveImmutableFields(company, current);
        return externalCompanyMapper.updateFeExternalCompany(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int toggleFeExternalCompany(Long companyRecordId)
    {
        FeExternalCompany company = requireCompany(companyRecordId);
        if (RECORD_MERGED.equals(company.getRecordStatus()))
        {
            throw new ServiceException("已并档的外部单位不能再切换状态");
        }
        company.setRecordStatus(RECORD_ACTIVE.equals(company.getRecordStatus()) ? RECORD_DISABLED : RECORD_ACTIVE);
        company.setUpdateBy(SecurityUtils.getUsername());
        company.setUpdateTime(DateUtils.getNowDate());
        return externalCompanyMapper.updateFeExternalCompany(company);
    }

    @Override
    public List<FeExternalCompany> selectDuplicateCandidates(Long companyRecordId)
    {
        FeExternalCompany current = requireCompany(companyRecordId);
        List<FeExternalCompany> candidates = externalCompanyMapper.selectActiveExternalCompaniesForMerge(companyRecordId);
        List<FeExternalCompany> result = new ArrayList<>();
        for (FeExternalCompany candidate : candidates)
        {
            DuplicateMatch duplicateMatch = matchDuplicate(current, candidate);
            if (duplicateMatch == null)
            {
                continue;
            }
            candidate.setDuplicateMatchType(duplicateMatch.getType());
            candidate.setDuplicateMatchReason(duplicateMatch.getReason());
            candidate.setDuplicatePriority(duplicateMatch.getPriority());
            result.add(candidate);
        }
        result.sort((left, right) -> {
            int priorityCompare = Integer.compare(defaultInt(right.getDuplicatePriority()), defaultInt(left.getDuplicatePriority()));
            if (priorityCompare != 0)
            {
                return priorityCompare;
            }
            Date leftSeen = left.getLastSeenTime();
            Date rightSeen = right.getLastSeenTime();
            if (leftSeen == null && rightSeen == null)
            {
                return Long.compare(defaultLong(right.getCompanyRecordId()), defaultLong(left.getCompanyRecordId()));
            }
            if (leftSeen == null)
            {
                return 1;
            }
            if (rightSeen == null)
            {
                return -1;
            }
            return rightSeen.compareTo(leftSeen);
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int mergeFeExternalCompany(FeExternalCompanyMergeRequest request)
    {
        if (request == null || request.getSourceCompanyRecordId() == null || request.getTargetCompanyRecordId() == null)
        {
            throw new ServiceException("请先选择需要并档的外部单位");
        }
        if (Objects.equals(request.getSourceCompanyRecordId(), request.getTargetCompanyRecordId()))
        {
            throw new ServiceException("并档源和目标不能是同一条记录");
        }

        FeExternalCompany source = requireCompany(request.getSourceCompanyRecordId());
        FeExternalCompany target = requireCompany(request.getTargetCompanyRecordId());
        validateMergeDirection(source, target);

        FeCompanyDeptMapping sourceMapping = companyDeptMappingMapper.selectByExternalCompanyId(source.getExternalCompanyId());
        FeCompanyDeptMapping targetMapping = companyDeptMappingMapper.selectByExternalCompanyId(target.getExternalCompanyId());
        if (sourceMapping != null && targetMapping != null
            && !Objects.equals(sourceMapping.getDeptId(), targetMapping.getDeptId()))
        {
            throw new ServiceException("两个外部单位已映射到不同合同单位，请先处理映射冲突后再并档");
        }

        String targetDisplayName = resolvePrimaryDisplayName(target);
        mergeCompanyMappings(source, target, sourceMapping, targetMapping, targetDisplayName);
        mergeApiScopes(source, target, targetDisplayName);

        firePointMapper.updateExternalCompanyRef(source.getExternalCompanyId(), target.getExternalCompanyId());
        gatewayMapper.updateExternalCompanyRef(source.getExternalCompanyId(), target.getExternalCompanyId());
        extinguisherMapper.updateExternalCompanyRef(source.getExternalCompanyId(), target.getExternalCompanyId());
        gatewayManualRecordMapper.updateExternalCompanyRef(source.getExternalCompanyId(), target.getExternalCompanyId(), targetDisplayName);
        sensorManualRecordMapper.updateExternalCompanyRef(source.getExternalCompanyId(), target.getExternalCompanyId(), targetDisplayName);

        mergeTargetProfile(source, target);
        target.setUpdateBy(SecurityUtils.getUsername());
        target.setUpdateTime(DateUtils.getNowDate());
        externalCompanyMapper.updateFeExternalCompany(target);

        source.setRecordStatus(RECORD_MERGED);
        source.setMergedToCompanyRecordId(target.getCompanyRecordId());
        source.setMergedToExternalCompanyId(target.getExternalCompanyId());
        source.setSyncStatus(RECORD_MERGED);
        source.setUpdateBy(SecurityUtils.getUsername());
        source.setUpdateTime(DateUtils.getNowDate());
        externalCompanyMapper.updateFeExternalCompany(source);

        Long deptId = resolveMappedDeptId(target.getExternalCompanyId());
        syncRelatedDept(target.getExternalCompanyId(), deptId);

        FeExternalCompanyMergeLog mergeLog = new FeExternalCompanyMergeLog();
        mergeLog.setSourceCompanyRecordId(source.getCompanyRecordId());
        mergeLog.setSourceExternalCompanyId(source.getExternalCompanyId());
        mergeLog.setSourceExternalCompanyName(resolvePrimaryDisplayName(source));
        mergeLog.setTargetCompanyRecordId(target.getCompanyRecordId());
        mergeLog.setTargetExternalCompanyId(target.getExternalCompanyId());
        mergeLog.setTargetExternalCompanyName(resolvePrimaryDisplayName(target));
        mergeLog.setMergeStrategy("manual_confirmed");
        mergeLog.setMergeReason(trimToNull(request.getMergeReason()));
        mergeLog.setMergeTime(DateUtils.getNowDate());
        mergeLog.setCreateBy(SecurityUtils.getUsername());
        mergeLog.setCreateTime(DateUtils.getNowDate());
        externalCompanyMapper.insertMergeLog(mergeLog);
        return 1;
    }

    private void validateCompanyPayload(FeExternalCompany company, boolean update)
    {
        if (company == null)
        {
            throw new ServiceException("外部单位参数不能为空");
        }
        if (StringUtils.isBlank(company.getExternalCompanyName()))
        {
            throw new ServiceException("外部单位名称不能为空");
        }
        if (update && company.getCompanyRecordId() == null)
        {
            throw new ServiceException("缺少外部单位记录ID");
        }
    }

    private void validateDuplicateName(FeExternalCompany company, Long excludeCompanyRecordId)
    {
        FeExternalCompany query = new FeExternalCompany();
        query.setRecordStatus(RECORD_ACTIVE);
        List<FeExternalCompany> exists = externalCompanyMapper.selectFeExternalCompanyList(query);
        String externalName = trimToNull(company.getExternalCompanyName());
        String manualName = trimToNull(company.getManualConfirmedName());
        for (FeExternalCompany item : exists)
        {
            if (excludeCompanyRecordId != null && Objects.equals(item.getCompanyRecordId(), excludeCompanyRecordId))
            {
                continue;
            }
            if (equalsIgnoreTrim(externalName, item.getExternalCompanyName())
                || equalsIgnoreTrim(externalName, item.getManualConfirmedName())
                || equalsIgnoreTrim(manualName, item.getExternalCompanyName())
                || equalsIgnoreTrim(manualName, item.getManualConfirmedName()))
            {
                throw new ServiceException("存在同名外部单位，请先检查是否需要并档");
            }
        }
    }

    private void preserveImmutableFields(FeExternalCompany company, FeExternalCompany current)
    {
        company.setExternalCompanyId(current.getExternalCompanyId());
        company.setFirstSourceType(defaultValue(company.getFirstSourceType(), current.getFirstSourceType()));
        company.setSdkObserved(defaultValue(company.getSdkObserved(), current.getSdkObserved()));
        company.setManualCreated(defaultValue(company.getManualCreated(), current.getManualCreated()));
        company.setManualCreatedBy(defaultValue(company.getManualCreatedBy(), current.getManualCreatedBy()));
        company.setManualCreatedTime(company.getManualCreatedTime() == null ? current.getManualCreatedTime() : company.getManualCreatedTime());
        company.setRecordStatus(defaultValue(company.getRecordStatus(), current.getRecordStatus()));
        company.setSyncStatus(defaultValue(company.getSyncStatus(), current.getSyncStatus()));
        company.setFirstSeenTime(company.getFirstSeenTime() == null ? current.getFirstSeenTime() : company.getFirstSeenTime());
        company.setLastSeenTime(company.getLastSeenTime() == null ? current.getLastSeenTime() : company.getLastSeenTime());
        company.setLastSourceDeptId(company.getLastSourceDeptId() == null ? current.getLastSourceDeptId() : company.getLastSourceDeptId());
        company.setMergedToCompanyRecordId(current.getMergedToCompanyRecordId());
        company.setMergedToExternalCompanyId(current.getMergedToExternalCompanyId());
    }

    private void validateMergeDirection(FeExternalCompany source, FeExternalCompany target)
    {
        if (!RECORD_ACTIVE.equals(source.getRecordStatus()) || !RECORD_ACTIVE.equals(target.getRecordStatus()))
        {
            throw new ServiceException("只有生效中的外部单位才能并档");
        }
        if (YES.equals(source.getSdkObserved()) && !YES.equals(target.getSdkObserved()))
        {
            throw new ServiceException("并档后应以SDK记录为主，请将人工记录并入SDK记录");
        }
    }

    private void mergeCompanyMappings(FeExternalCompany source, FeExternalCompany target,
        FeCompanyDeptMapping sourceMapping, FeCompanyDeptMapping targetMapping, String targetDisplayName)
    {
        if (sourceMapping == null)
        {
            return;
        }
        if (targetMapping == null)
        {
            companyDeptMappingMapper.updateExternalCompanyRef(source.getExternalCompanyId(), target.getExternalCompanyId(), targetDisplayName);
            return;
        }
        companyDeptMappingMapper.deleteByExternalCompanyId(source.getExternalCompanyId());
    }

    private void mergeApiScopes(FeExternalCompany source, FeExternalCompany target, String targetDisplayName)
    {
        FeApiConfigCompanyScope sourceQuery = new FeApiConfigCompanyScope();
        sourceQuery.setExternalCompanyId(source.getExternalCompanyId());
        List<FeApiConfigCompanyScope> sourceScopes = apiConfigCompanyScopeMapper.selectFeApiConfigCompanyScopeList(sourceQuery);
        if (sourceScopes == null || sourceScopes.isEmpty())
        {
            return;
        }

        for (FeApiConfigCompanyScope sourceScope : sourceScopes)
        {
            FeApiConfigCompanyScope targetScope = apiConfigCompanyScopeMapper
                .selectByConfigIdAndExternalCompanyId(sourceScope.getConfigId(), target.getExternalCompanyId());
            if (targetScope == null)
            {
                apiConfigCompanyScopeMapper.updateExternalCompanyRefByScopeId(sourceScope.getScopeId(), target.getExternalCompanyId(), targetDisplayName);
                continue;
            }
            if (minDate(targetScope.getFirstSeenTime(), sourceScope.getFirstSeenTime()) != null)
            {
                targetScope.setFirstSeenTime(minDate(targetScope.getFirstSeenTime(), sourceScope.getFirstSeenTime()));
            }
            if (maxDate(targetScope.getLastSeenTime(), sourceScope.getLastSeenTime()) != null)
            {
                targetScope.setLastSeenTime(maxDate(targetScope.getLastSeenTime(), sourceScope.getLastSeenTime()));
            }
            if (targetScope.getSourceDeptId() == null)
            {
                targetScope.setSourceDeptId(sourceScope.getSourceDeptId());
            }
            targetScope.setExternalCompanyName(targetDisplayName);
            targetScope.setUpdateBy(SecurityUtils.getUsername());
            targetScope.setUpdateTime(DateUtils.getNowDate());
            apiConfigCompanyScopeMapper.updateFeApiConfigCompanyScope(targetScope);
            apiConfigCompanyScopeMapper.deleteByScopeId(sourceScope.getScopeId());
        }
    }

    private void mergeTargetProfile(FeExternalCompany source, FeExternalCompany target)
    {
        target.setSdkObserved(orFlag(target.getSdkObserved(), source.getSdkObserved()));
        target.setManualCreated(orFlag(target.getManualCreated(), source.getManualCreated()));
        target.setFirstSourceType(resolveFirstSourceType(target, source));
        target.setManualCreatedBy(firstNotBlank(target.getManualCreatedBy(), source.getManualCreatedBy()));
        target.setManualCreatedTime(minDate(target.getManualCreatedTime(), source.getManualCreatedTime()));
        target.setFirstSeenTime(minDate(target.getFirstSeenTime(), source.getFirstSeenTime()));
        target.setLastSeenTime(maxDate(target.getLastSeenTime(), source.getLastSeenTime()));
        target.setLastSourceDeptId(firstNotNull(target.getLastSourceDeptId(), source.getLastSourceDeptId()));
        target.setNumberPrefix(firstNotBlank(target.getNumberPrefix(), source.getNumberPrefix()));
        target.setOrgPath(firstNotBlank(target.getOrgPath(), source.getOrgPath()));
        target.setRemark(firstNotBlank(target.getRemark(), source.getRemark()));
        if (StringUtils.isBlank(target.getExternalCompanyName()))
        {
            target.setExternalCompanyName(source.getExternalCompanyName());
        }
        if (StringUtils.isBlank(target.getManualConfirmedName()))
        {
            target.setManualConfirmedName(resolveManualAlias(source));
        }
        target.setSyncStatus(YES.equals(target.getSdkObserved()) ? SYNC_STATUS_OBSERVED : SYNC_STATUS_MANUAL);
        target.setRecordStatus(RECORD_ACTIVE);
    }

    private String resolveFirstSourceType(FeExternalCompany target, FeExternalCompany source)
    {
        if (StringUtils.isBlank(target.getFirstSourceType()))
        {
            return source.getFirstSourceType();
        }
        if (StringUtils.equals(target.getFirstSourceType(), source.getFirstSourceType()))
        {
            return target.getFirstSourceType();
        }
        Date targetFirstTime = resolveFirstSourceTime(target);
        Date sourceFirstTime = resolveFirstSourceTime(source);
        if (targetFirstTime == null || sourceFirstTime == null)
        {
            return target.getFirstSourceType();
        }
        return sourceFirstTime.before(targetFirstTime) ? source.getFirstSourceType() : target.getFirstSourceType();
    }

    private Date resolveFirstSourceTime(FeExternalCompany company)
    {
        if (StringUtils.equals(company.getFirstSourceType(), SOURCE_MANUAL))
        {
            return company.getManualCreatedTime();
        }
        if (StringUtils.equals(company.getFirstSourceType(), SOURCE_SDK))
        {
            return company.getFirstSeenTime();
        }
        return minDate(company.getManualCreatedTime(), company.getFirstSeenTime());
    }

    private String resolveManualAlias(FeExternalCompany source)
    {
        if (StringUtils.isNotBlank(source.getManualConfirmedName()))
        {
            return source.getManualConfirmedName();
        }
        return YES.equals(source.getManualCreated()) ? source.getExternalCompanyName() : null;
    }

    private DuplicateMatch matchDuplicate(FeExternalCompany current, FeExternalCompany candidate)
    {
        Set<String> currentRawNames = collectRawNames(current);
        Set<String> candidateRawNames = collectRawNames(candidate);
        for (String currentName : currentRawNames)
        {
            for (String candidateName : candidateRawNames)
            {
                if (StringUtils.equalsIgnoreCase(currentName, candidateName))
                {
                    return new DuplicateMatch("exact", "存在完全同名的外部单位记录", 100);
                }
            }
        }

        Set<String> currentNormalized = normalizeNames(currentRawNames);
        Set<String> candidateNormalized = normalizeNames(candidateRawNames);
        for (String currentName : currentNormalized)
        {
            for (String candidateName : candidateNormalized)
            {
                if (StringUtils.equals(currentName, candidateName))
                {
                    return new DuplicateMatch("normalized", "规范化名称一致，疑似同一外部单位", 80);
                }
                if (currentName.length() >= 4 && candidateName.length() >= 4
                    && (StringUtils.contains(currentName, candidateName) || StringUtils.contains(candidateName, currentName)))
                {
                    return new DuplicateMatch("contains", "规范化名称存在包含关系，建议人工确认是否并档", 60);
                }
            }
        }
        return null;
    }

    private Set<String> collectRawNames(FeExternalCompany company)
    {
        Set<String> names = new LinkedHashSet<>();
        addTrimmed(names, company.getExternalCompanyName());
        addTrimmed(names, company.getManualConfirmedName());
        return names;
    }

    private Set<String> normalizeNames(Set<String> rawNames)
    {
        return rawNames.stream()
            .map(this::normalizeCompanyName)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeCompanyName(String companyName)
    {
        if (StringUtils.isBlank(companyName))
        {
            return null;
        }
        String normalized = NORMALIZE_PATTERN.matcher(companyName.trim()).replaceAll("");
        normalized = StringUtils.replaceEach(normalized,
            new String[] { "有限责任公司", "股份有限公司", "集团有限公司", "集团", "有限公司", "公司", "分公司", "本部" },
            new String[] { "", "", "", "", "", "", "", "" });
        return normalized.toLowerCase();
    }

    private FeExternalCompany requireCompany(Long companyRecordId)
    {
        FeExternalCompany company = externalCompanyMapper.selectByCompanyRecordId(companyRecordId);
        if (company == null)
        {
            throw new ServiceException("未找到对应的外部单位记录");
        }
        return company;
    }

    private Long resolveExternalCompanyId(Long candidateExternalCompanyId)
    {
        if (candidateExternalCompanyId != null)
        {
            return candidateExternalCompanyId;
        }
        Long minExternalCompanyId = externalCompanyMapper.selectMinExternalCompanyId();
        if (minExternalCompanyId != null && minExternalCompanyId < 0L)
        {
            return minExternalCompanyId - 1L;
        }
        return -1L;
    }

    private Long resolveMappedDeptId(Long externalCompanyId)
    {
        FeCompanyDeptMapping mapping = companyDeptMappingMapper.selectByExternalCompanyId(externalCompanyId);
        return mapping == null ? null : mapping.getDeptId();
    }

    private void syncRelatedDept(Long externalCompanyId, Long deptId)
    {
        if (externalCompanyId == null)
        {
            return;
        }
        firePointMapper.updateDeptIdByExternalCompanyId(externalCompanyId, deptId);
        gatewayMapper.updateDeptIdByExternalCompanyId(externalCompanyId, deptId);
        sensorMapper.updateDeptIdByGatewayExternalCompanyId(externalCompanyId, deptId);
        extinguisherMapper.updateDeptIdByExternalCompanyId(externalCompanyId, deptId);
    }

    private String resolvePrimaryDisplayName(FeExternalCompany company)
    {
        return firstNotBlank(company.getExternalCompanyName(), company.getManualConfirmedName(),
            company.getExternalCompanyId() == null ? null : String.valueOf(company.getExternalCompanyId()));
    }

    private void addTrimmed(Set<String> values, String value)
    {
        String trimmed = trimToNull(value);
        if (trimmed != null)
        {
            values.add(trimmed);
        }
    }

    private String orFlag(String left, String right)
    {
        return YES.equals(left) || YES.equals(right) ? YES : NO;
    }

    private boolean equalsIgnoreTrim(String left, String right)
    {
        return StringUtils.equalsIgnoreCase(trimToNull(left), trimToNull(right));
    }

    private String trimToNull(String value)
    {
        return StringUtils.trimToNull(value);
    }

    private String defaultValue(String value, String defaultValue)
    {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private String firstNotBlank(String... values)
    {
        for (String value : values)
        {
            if (StringUtils.isNotBlank(value))
            {
                return value;
            }
        }
        return null;
    }

    private Long firstNotNull(Long left, Long right)
    {
        return left != null ? left : right;
    }

    private Date minDate(Date left, Date right)
    {
        if (left == null)
        {
            return right;
        }
        if (right == null)
        {
            return left;
        }
        return left.before(right) ? left : right;
    }

    private Date maxDate(Date left, Date right)
    {
        if (left == null)
        {
            return right;
        }
        if (right == null)
        {
            return left;
        }
        return left.after(right) ? left : right;
    }

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value;
    }

    private long defaultLong(Long value)
    {
        return value == null ? 0L : value;
    }

    private static class DuplicateMatch
    {
        private final String type;
        private final String reason;
        private final int priority;

        DuplicateMatch(String type, String reason, int priority)
        {
            this.type = type;
            this.reason = reason;
            this.priority = priority;
        }

        String getType()
        {
            return type;
        }

        String getReason()
        {
            return reason;
        }

        int getPriority()
        {
            return priority;
        }
    }
}
