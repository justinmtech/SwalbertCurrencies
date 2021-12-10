package com.justinmtech.swalbertcurrencies.core;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Currency implements ConfigurationSerializable {
    String name;
    private List<String> aliasNames;
    private boolean localStorage;
    private boolean allowDecimals;
    private boolean allowPay;
    private String singularReference;
    private String pluralReference;

    public Currency(Set<String> keys, Map<String, Object> configMap) {
        List<String> keyList = new LinkedList<>();
        String currencyName = keyList.get(0);
        for (String key : keys) {
            keyList.add(key);
        }
        this.name = (String) configMap.get(currencyName);
        this.aliasNames = (List<String>) configMap.get(currencyName + ".alias-names");
        this.localStorage = (Boolean) configMap.get(currencyName + ".storage");
        this.singularReference = (String) configMap.get(currencyName + ".language.singular");
        this.pluralReference = (String) configMap.get(currencyName + ".language.plural");
        this.allowDecimals = (Boolean) configMap.get(currencyName + ".allow-decimals");
        this.allowPay = (Boolean) configMap.get(currencyName + ".allow-pay");

    }

    public Currency(String name, Map<String, Object> serializedCurrency) {
        this.name = name;
        this.aliasNames = (List<String>) serializedCurrency.get("alias-names");
        pluralReference = (String) serializedCurrency.get("language.plural");;
        singularReference = (String) serializedCurrency.get("language.singular");
        localStorage = (Boolean) serializedCurrency.get("local-storage");
        allowDecimals = (Boolean) serializedCurrency.get("allow-decimals");
        allowPay = (Boolean) serializedCurrency.get("allow-pay");
    }

    public List<String> getAliasNames() {
        return aliasNames;
    }

    public void setAliasNames(List<String> aliasNames) {
        this.aliasNames = aliasNames;
    }

    public boolean isLocalStorage() {
        return localStorage;
    }

    public void setLocalStorage(boolean localStorage) {
        this.localStorage = localStorage;
    }

    public boolean isAllowDecimals() {
        return allowDecimals;
    }

    public void setAllowDecimals(boolean allowDecimals) {
        this.allowDecimals = allowDecimals;
    }

    public boolean isAllowPay() {
        return allowPay;
    }

    public void setAllowPay(boolean allowPay) {
        this.allowPay = allowPay;
    }

    public String getSingularReference() {
        return singularReference;
    }

    public void setSingularReference(String singularReference) {
        this.singularReference = singularReference;
    }

    public String getPluralReference() {
        return pluralReference;
    }

    public void setPluralReference(String pluralReference) {
        this.pluralReference = pluralReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> mapSerializer = new HashMap<>();

        mapSerializer.put("name", name);
        mapSerializer.put("local-storage", localStorage);
        mapSerializer.put("allow-decimals", allowDecimals);
        mapSerializer.put("allow-pay", allowPay);
        mapSerializer.put("singular", singularReference);
        mapSerializer.put("plural", pluralReference);

        return mapSerializer;
    }
}
