package com.justinmtech.customcurrencies.currencies;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class PlayerModel implements ConfigurationSerializable {
    String uuid;
    Map<String, BigDecimal> currencies;

    public PlayerModel(String uuid, Map<String, BigDecimal> currencies) {
        this.uuid = uuid;
        this.currencies = currencies;
    }

    public PlayerModel(String uuid) {
        this.uuid = uuid;
        this.currencies = new HashMap<>();
    }

    public PlayerModel(Map<String, Object> serializedPlayerModel) {
        this.currencies = new HashMap<>();
        Set<String> keys = serializedPlayerModel.keySet();
        for (String key : keys) {
            if (key.equalsIgnoreCase("uuid") || key.equalsIgnoreCase("==")) {
            } else {
                String amount = (String) serializedPlayerModel.get(key);
                BigDecimal bd = BigDecimal.valueOf(Double.parseDouble(amount));
                this.currencies.put(key, bd);
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializableMap = new HashMap<>();

        Set<String> keys = currencies.keySet();

        for (String key : keys) {
            serializableMap.put(key, currencies.get(key).toString());
        }

        return serializableMap;
    }
}
