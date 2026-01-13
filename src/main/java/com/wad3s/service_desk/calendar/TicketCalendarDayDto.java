package com.wad3s.service_desk.calendar;

import java.time.LocalDate;
import java.util.List;

public record TicketCalendarDayDto(
        LocalDate date,
        List<TicketCalendarItemDto> tickets
) {}

