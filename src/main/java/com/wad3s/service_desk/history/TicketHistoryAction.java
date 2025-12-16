package com.wad3s.service_desk.history;

public enum TicketHistoryAction {

    CREATED,

    // статус
    STATUS_CHANGED,
    RESOLVED,
    REOPENED,
    REJECTED,

    // назначения
    ASSIGNEE_CHANGED,
    TEAM_CHANGED,

    // поля тикета
    PRIORITY_CHANGED,
    TITLE_CHANGED,
    CATEGORY_CHANGED,
    LOCATION_CHANGED,

    // комментарии
    COMMENT_ADDED
}
