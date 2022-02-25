package com.justinmtech.customcurrencies.currencies;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

@Getter
public class Currency implements ConfigurationSerializable {
    private final String name;
    private final List<String> aliasNames;
    private final boolean localStorage;
    private final boolean allowDecimals;
    private final boolean allowPay;
    private final String singularReference;
    private final String pluralReference;

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
