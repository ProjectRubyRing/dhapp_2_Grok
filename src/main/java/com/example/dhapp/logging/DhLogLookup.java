package com.example.dhapp.logging;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * log4j2-spring.xml の {@code ${dhlog:...}}。
 *
 * <ul>
 *   <li>{@code ${dhlog:hostip}} … ホスト IPv4（ハイフン区切り）。起動中は同じ値</li>
 *   <li>{@code ${dhlog:rand:20:alnum}} … 英数字。キーごとに 1 回だけ生成してキャッシュする</li>
 *   <li>{@code ${dhlog:rand:17:digits}} … 数字のみ。同上</li>
 * </ul>
 *
 * 設定ファイルの中で同じキーが複数のアペンダから参照されても、ファイル名が割れないようにする。
 */
@Plugin(name = "dhlog", category = StrLookup.CATEGORY)
public class DhLogLookup extends AbstractLookup {

    private static final ConcurrentMap<String, String> CACHE = new ConcurrentHashMap<>();

    @Override
    public String lookup(LogEvent event, String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        if ("hostip".equals(key)) {
            return CACHE.computeIfAbsent(key, ignored -> new HostIpPropertyDefiner().getPropertyValue());
        }
        if (key.startsWith("rand:")) {
            return CACHE.computeIfAbsent(key, DhLogLookup::randomValue);
        }
        return null;
    }

    private static String randomValue(String key) {
        String[] parts = key.split(":", 3);
        int length = 10;
        if (parts.length >= 2) {
            try {
                length = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
                length = 10;
            }
        }
        RandomStringPropertyDefiner definer = new RandomStringPropertyDefiner();
        definer.setLength(length);
        if (parts.length >= 3 && "digits".equals(parts[2])) {
            definer.setCharacters("0123456789");
        }
        return definer.getPropertyValue();
    }
}
