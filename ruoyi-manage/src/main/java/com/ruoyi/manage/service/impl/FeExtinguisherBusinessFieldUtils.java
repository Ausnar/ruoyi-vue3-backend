package com.ruoyi.manage.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.FeExtinguisher;

final class FeExtinguisherBusinessFieldUtils
{
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final LocalDate NEW_STANDARD_DATE = LocalDate.of(2025, 1, 1);
    static final String PROFILE_SOURCE_DERIVED = "derived";
    static final String PROFILE_SOURCE_MANUAL = "manual";
    static final String PROFILE_SOURCE_MIXED = "mixed";
    static final String PROFILE_SYNC_STATUS_SUCCESS = "success";
    static final String PROFILE_SYNC_STATUS_FAILED = "failed";
    static final String PROFILE_SYNC_STATUS_INCOMPLETE = "incomplete";

    private FeExtinguisherBusinessFieldUtils()
    {
    }

    static void completeWarningBusinessFields(FeExtinguisher extinguisher)
    {
        completeWarningBusinessFields(extinguisher, false);
    }

    static void completeWarningBusinessFields(FeExtinguisher extinguisher, boolean overrideTypeAndForm)
    {
        if (extinguisher == null)
        {
            return;
        }

        if (overrideTypeAndForm || StringUtils.isBlank(extinguisher.getExtinguisherType()))
        {
            String inferredType = inferExtinguisherType(extinguisher);
            if (StringUtils.isNotBlank(inferredType))
            {
                extinguisher.setExtinguisherType(inferredType);
            }
        }
        if (overrideTypeAndForm || StringUtils.isBlank(extinguisher.getExtinguisherForm()))
        {
            String inferredForm = inferExtinguisherForm(extinguisher);
            if (StringUtils.isNotBlank(inferredForm))
            {
                extinguisher.setExtinguisherForm(inferredForm);
            }
        }

        if (extinguisher.getProductionDate() == null)
        {
            return;
        }

        LocalDate productionDate = toLocalDate(extinguisher.getProductionDate());
        Integer serviceLifeYears = getServiceLifeYears(extinguisher.getExtinguisherType());
        if (serviceLifeYears != null)
        {
            extinguisher.setExpiryDate(toDate(productionDate.plusYears(serviceLifeYears)));
        }

        String standardCode = resolveStandardCode(extinguisher.getExtinguisherForm(), productionDate);
        if (StringUtils.isNotBlank(standardCode))
        {
            extinguisher.setStandardCode(standardCode);
        }
    }

    static void markManualProfileEdit(FeExtinguisher extinguisher, String existingSource)
    {
        if (extinguisher == null)
        {
            return;
        }
        if (PROFILE_SOURCE_DERIVED.equals(existingSource) || "sdk".equals(existingSource) || PROFILE_SOURCE_MIXED.equals(existingSource))
        {
            extinguisher.setProfileSource(PROFILE_SOURCE_MIXED);
        }
        else
        {
            extinguisher.setProfileSource(PROFILE_SOURCE_MANUAL);
        }
    }

    static String resolveProfileSyncStatus(FeExtinguisher extinguisher)
    {
        if (extinguisher == null)
        {
            return PROFILE_SYNC_STATUS_INCOMPLETE;
        }
        return extinguisher.getProductionDate() != null
            && StringUtils.isNotBlank(extinguisher.getExtinguisherType())
            && StringUtils.isNotBlank(extinguisher.getExtinguisherForm())
            ? PROFILE_SYNC_STATUS_SUCCESS : PROFILE_SYNC_STATUS_INCOMPLETE;
    }

    private static String inferExtinguisherType(FeExtinguisher extinguisher)
    {
        String text = normalizeText(extinguisher.getSpecification()) + " " + normalizeText(extinguisher.getProductName());
        if (text.contains("水基"))
        {
            return "water_based";
        }
        if (text.contains("干粉"))
        {
            return "dry_powder";
        }
        if (text.contains("洁净气体") || text.contains("洁净"))
        {
            return "clean_gas";
        }
        if (text.contains("二氧化碳") || text.contains("co2") || text.contains("co₂"))
        {
            return "co2";
        }
        return null;
    }

    private static String inferExtinguisherForm(FeExtinguisher extinguisher)
    {
        String text = normalizeText(extinguisher.getSpecification()) + " " + normalizeText(extinguisher.getProductName());
        if (text.contains("推车"))
        {
            return "wheeled";
        }
        if (text.contains("手提"))
        {
            return "portable";
        }
        return null;
    }

    private static String normalizeText(String text)
    {
        return StringUtils.defaultString(text).trim().toLowerCase();
    }

    private static Integer getServiceLifeYears(String extinguisherType)
    {
        if (StringUtils.isBlank(extinguisherType))
        {
            return null;
        }
        switch (extinguisherType)
        {
            case "water_based":
                return 6;
            case "dry_powder":
            case "clean_gas":
                return 10;
            case "co2":
                return 12;
            default:
                return null;
        }
    }

    private static String resolveStandardCode(String extinguisherForm, LocalDate productionDate)
    {
        if (StringUtils.isBlank(extinguisherForm) || productionDate == null)
        {
            return null;
        }
        boolean useNewStandard = !productionDate.isBefore(NEW_STANDARD_DATE);
        if ("portable".equals(extinguisherForm))
        {
            return useNewStandard ? "GB 4351-2023" : "GB 4351.1-2005";
        }
        if ("wheeled".equals(extinguisherForm))
        {
            return useNewStandard ? "GB 8109-2023" : "GB 8109-2005";
        }
        return null;
    }

    private static LocalDate toLocalDate(Date date)
    {
        return date.toInstant().atZone(DEFAULT_ZONE).toLocalDate();
    }

    private static Date toDate(LocalDate localDate)
    {
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE).toInstant());
    }
}
