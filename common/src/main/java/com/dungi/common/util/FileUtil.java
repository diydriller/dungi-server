package com.dungi.common.util;

import java.util.Optional;

public class FileUtil {
    public static String extractFileExt(String filename) {
        String[] originFilenameParts = Optional.ofNullable(filename)
                .map(name -> name.split("\\."))
                .orElseThrow(() -> new IllegalArgumentException("파일 이름이 없습니다."));
        return originFilenameParts[originFilenameParts.length - 1];
    }
}
