package com.example.dhapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/healthcheck
 *
 * 死活監視用。業務処理は行わず HTTP 200 を返す。
 * stale file handle の調査で繰り返し呼ばれることを想定し、INFO を 1 行だけ出す。
 * この 1 行は log4j2 の tracelog（時間ローテーション）に記録される。
 *
 * curl -i http://localhost:8080/dhapp/api/healthcheck
 */
@RestController
@RequestMapping("/api/healthcheck")
public class HealthCheckController {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping
    public ResponseEntity<Void> healthcheck() {
        log.info("GET /api/healthcheck");
        return ResponseEntity.ok().build();
    }
}
