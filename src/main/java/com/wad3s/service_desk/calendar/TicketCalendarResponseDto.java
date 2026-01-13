package com.wad3s.service_desk.calendar;

import java.time.LocalDate;
import java.util.List;

public record TicketCalendarResponseDto(
        LocalDate from,
        LocalDate to,
        List<TicketCalendarDayDto> days
) {}
