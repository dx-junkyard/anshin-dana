package com.anshindana.service;

import com.anshindana.domain.TodayTasks;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    public TodayTasks getTodayTasks(Long userId) {
        return new TodayTasks(
                List.of(
                        new TodayTasks.ExpiringItem("パスタソース", "2024-08-03"),
                        new TodayTasks.ExpiringItem("ツナ缶", "2024-08-10")
                ),
                List.of(
                        new TodayTasks.ExpiredItem("トマトジュース", "2024-07-01")
                ),
                List.of(
                        new TodayTasks.LowStockItem("主食", "お米を2kg買い足し")
                ),
                List.of(
                        new TodayTasks.SuggestedConsume("冷凍唐揚げ", "冷凍庫スペース確保"),
                        new TodayTasks.SuggestedConsume("パスタ", "賞味期限が近い")
                )
        );
    }

    public Map<String, Object> emergencyPlan(int people, int days) {
        return Map.of(
                "people", people,
                "days", days,
                "plan", List.of(
                        Map.of("day", 1, "items", List.of("缶詰セット", "水2L/人")),
                        Map.of("day", 2, "items", List.of("レトルト米", "味噌汁パック"))
                )
        );
    }
}
